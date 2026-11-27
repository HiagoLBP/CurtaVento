"""Short-term wind curtailment neural architectures with Time2Vec and Quantile P10/P50/P90 outputs.

Implements four architectures:
  - MLPTime2Vec
  - LSTMTime2Vec
  - DLinearTime2Vec (Zeng et al., 2023)
  - PatchTSTTime2Vec (Nie et al., ICLR 2023 — RevIN + Patching + Channel-Independence)
With monotonic P10/P50/P90 quantile regression inspired by Temporal Fusion Transformers (Lim et al., 2020).
"""
from __future__ import annotations

import torch
import torch.nn as nn
import torch.nn.functional as F

from app.features.time2vec import Time2Vec


class SeriesDecomposition(nn.Module):
    """Moving-average decomposition block separating trend and residual/seasonal components."""

    def __init__(self, kernel_size: int = 5) -> None:
        super().__init__()
        self.kernel_size = kernel_size
        self.avg = nn.AvgPool1d(kernel_size=kernel_size, stride=1, padding=0)

    def forward(self, x: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        pad_left = (self.kernel_size - 1) // 2
        pad_right = self.kernel_size - 1 - pad_left
        front = x[:, :1, :].repeat(1, pad_left, 1)
        end = x[:, -1:, :].repeat(1, pad_right, 1)
        x_padded = torch.cat([front, x, end], dim=1)
        trend = self.avg(x_padded.permute(0, 2, 1)).permute(0, 2, 1)
        seasonal = x - trend
        return seasonal, trend


class RevIN(nn.Module):
    """Reversible Instance Normalization per sample and channel (Nie et al., 2023)."""

    def __init__(self, eps: float = 1e-4) -> None:
        super().__init__()
        self.eps = eps

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        mean = x.mean(dim=1, keepdim=True)
        var = x.var(dim=1, unbiased=False, keepdim=True)
        std = torch.sqrt(var + self.eps)
        return (x - mean) / std


class DualHeadMixin:
    """Common dual-head output: curtailment probability and monotonic P10/P50/P90 quantile MWh."""

    @staticmethod
    def build_head(in_dim: int, dropout: float = 0.15) -> tuple[nn.Module, nn.Module]:
        prob_head = nn.Sequential(
            nn.Dropout(dropout),
            nn.Linear(in_dim, 32),
            nn.GELU(),
            nn.Linear(32, 1),
        )
        quantile_head = nn.Sequential(
            nn.Dropout(dropout),
            nn.Linear(in_dim, 32),
            nn.GELU(),
            nn.Linear(32, 3),
        )
        return prob_head, quantile_head

    @staticmethod
    def decode_quantiles(raw_q: torch.Tensor) -> torch.Tensor:
        """Guarantees 0 <= q10 <= q50 <= q90 via cumulative non-negative increments."""
        q10 = F.relu(raw_q[:, 0])
        q50 = q10 + F.softplus(raw_q[:, 1]) * 0.25
        q90 = q50 + F.softplus(raw_q[:, 2]) * 0.25
        return torch.stack([q10, q50, q90], dim=-1)


class MLPTime2Vec(nn.Module, DualHeadMixin):
    """Multilayer Perceptron enriched with Time2Vec temporal encoding."""

    def __init__(self, seq_len: int, num_features: int, t2v_dim: int = 8, hidden_dim: int = 128) -> None:
        super().__init__()
        self.t2v = Time2Vec(in_features=1, out_features=t2v_dim)
        in_dim = seq_len * (num_features + t2v_dim)
        self.backbone = nn.Sequential(
            nn.Linear(in_dim, hidden_dim),
            nn.LayerNorm(hidden_dim),
            nn.GELU(),
            nn.Dropout(0.15),
            nn.Linear(hidden_dim, 64),
            nn.GELU(),
        )
        self.prob_head, self.quantile_head = self.build_head(64)

    def forward_quantiles(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1).flatten(start_dim=1)
        h = self.backbone(fused)
        prob = torch.sigmoid(self.prob_head(h)).squeeze(-1)
        quantiles = self.decode_quantiles(self.quantile_head(h))
        return prob, quantiles

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        prob, quantiles = self.forward_quantiles(x, tau)
        return prob, quantiles[:, 1]


class LSTMTime2Vec(nn.Module, DualHeadMixin):
    """2-layer LSTM enriched with Time2Vec temporal encoding."""

    def __init__(self, seq_len: int, num_features: int, t2v_dim: int = 8, hidden_dim: int = 64) -> None:
        super().__init__()
        self.t2v = Time2Vec(in_features=1, out_features=t2v_dim)
        self.lstm = nn.LSTM(
            input_size=num_features + t2v_dim,
            hidden_size=hidden_dim,
            num_layers=2,
            batch_first=True,
            dropout=0.15,
        )
        self.norm = nn.LayerNorm(hidden_dim)
        self.prob_head, self.quantile_head = self.build_head(hidden_dim)

    def forward_quantiles(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1)
        out, _ = self.lstm(fused)
        last_hidden = self.norm(out[:, -1, :])
        prob = torch.sigmoid(self.prob_head(last_hidden)).squeeze(-1)
        quantiles = self.decode_quantiles(self.quantile_head(last_hidden))
        return prob, quantiles

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        prob, quantiles = self.forward_quantiles(x, tau)
        return prob, quantiles[:, 1]


class DLinearTime2Vec(nn.Module, DualHeadMixin):
    """Decomposition Linear (DLinear) network fused with Time2Vec temporal encoding."""

    def __init__(self, seq_len: int, num_features: int, t2v_dim: int = 8, hidden_dim: int = 64) -> None:
        super().__init__()
        self.t2v = Time2Vec(in_features=1, out_features=t2v_dim)
        self.decomp = SeriesDecomposition(kernel_size=min(5, seq_len if seq_len % 2 == 1 else max(3, seq_len - 1)))
        total_channels = num_features + t2v_dim
        self.linear_seasonal = nn.Linear(seq_len, hidden_dim)
        self.linear_trend = nn.Linear(seq_len, hidden_dim)
        self.channel_proj = nn.Sequential(
            nn.Linear(total_channels * hidden_dim, 64),
            nn.LayerNorm(64),
            nn.GELU(),
        )
        self.prob_head, self.quantile_head = self.build_head(64)

    def forward_quantiles(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1)
        seasonal, trend = self.decomp(fused)
        s_proj = self.linear_seasonal(seasonal.permute(0, 2, 1))
        t_proj = self.linear_trend(trend.permute(0, 2, 1))
        combined = (s_proj + t_proj).flatten(start_dim=1)
        h = self.channel_proj(combined)
        prob = torch.sigmoid(self.prob_head(h)).squeeze(-1)
        quantiles = self.decode_quantiles(self.quantile_head(h))
        return prob, quantiles

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        prob, quantiles = self.forward_quantiles(x, tau)
        return prob, quantiles[:, 1]


class PatchTSTTime2Vec(nn.Module, DualHeadMixin):
    """PatchTST with RevIN, Time2Vec, and Channel-Independent Transformer Encoder (Nie et al., ICLR 2023)."""

    def __init__(
        self,
        seq_len: int,
        num_features: int,
        t2v_dim: int = 8,
        patch_len: int = 4,
        stride: int = 2,
        d_model: int = 32,
        nhead: int = 4,
        num_layers: int = 2,
    ) -> None:
        super().__init__()
        self.revin = RevIN()
        self.t2v = Time2Vec(in_features=1, out_features=t2v_dim)
        self.patch_len = min(patch_len, seq_len)
        self.stride = max(1, min(stride, self.patch_len))
        self.total_channels = num_features + t2v_dim

        n_patches = ((seq_len - self.patch_len) // self.stride) + 1
        self.patch_proj = nn.Linear(self.patch_len, d_model)
        self.pos_embed = nn.Parameter(torch.zeros(1, n_patches, d_model))
        encoder_layer = nn.TransformerEncoderLayer(
            d_model=d_model,
            nhead=nhead,
            dim_feedforward=d_model * 2,
            dropout=0.15,
            activation="gelu",
            batch_first=True,
        )
        self.encoder = nn.TransformerEncoder(encoder_layer, num_layers=num_layers)
        self.channel_agg = nn.Sequential(
            nn.Linear(self.total_channels * d_model, 64),
            nn.LayerNorm(64),
            nn.GELU(),
        )
        self.prob_head, self.quantile_head = self.build_head(64)

    def forward_quantiles(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        x_norm = self.revin(x)
        t_emb = self.t2v(tau)
        fused = torch.cat([x_norm, t_emb], dim=-1)  # (B, L, C)
        bsz, _, ch = fused.shape

        # Unfold into patches along time for each channel independently: (B, C, N_patches, P)
        patches = fused.permute(0, 2, 1).unfold(dimension=2, size=self.patch_len, step=self.stride)
        n_patches = patches.shape[2]
        patches_ci = patches.reshape(bsz * ch, n_patches, self.patch_len)

        tok = self.patch_proj(patches_ci) + self.pos_embed[:, :n_patches, :]
        enc = self.encoder(tok)  # (B*C, N_patches, d_model)
        pooled = enc.mean(dim=1).reshape(bsz, ch * enc.shape[-1])

        h = self.channel_agg(pooled)
        prob = torch.sigmoid(self.prob_head(h)).squeeze(-1)
        quantiles = self.decode_quantiles(self.quantile_head(h))
        return prob, quantiles

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        prob, quantiles = self.forward_quantiles(x, tau)
        return prob, quantiles[:, 1]


class ITransformerTime2Vec(nn.Module, DualHeadMixin):
    """Inverted Transformer (Liu et al., ICLR 2024): treats each variate series as a token
    and applies self-attention across multivariate physical + Time2Vec channels."""

    def __init__(
        self,
        seq_len: int,
        num_features: int,
        t2v_dim: int = 8,
        d_model: int = 64,
        nhead: int = 4,
        num_layers: int = 2,
    ) -> None:
        super().__init__()
        self.t2v = Time2Vec(in_features=1, out_features=t2v_dim)
        self.variate_embed = nn.Sequential(
            nn.Linear(seq_len, d_model),
            nn.LayerNorm(d_model),
            nn.GELU(),
        )
        encoder_layer = nn.TransformerEncoderLayer(
            d_model=d_model,
            nhead=nhead,
            dim_feedforward=d_model * 2,
            dropout=0.15,
            activation="gelu",
            batch_first=True,
        )
        self.encoder = nn.TransformerEncoder(encoder_layer, num_layers=num_layers)
        self.norm = nn.LayerNorm(d_model)
        self.prob_head, self.quantile_head = self.build_head(d_model)

    def forward_quantiles(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1)  # (B, L, C)
        variate_tokens = self.variate_embed(fused.permute(0, 2, 1))  # (B, C, d_model)
        enc = self.encoder(variate_tokens)  # (B, C, d_model)
        pooled = self.norm(enc.mean(dim=1))  # (B, d_model)
        prob = torch.sigmoid(self.prob_head(pooled)).squeeze(-1)
        quantiles = self.decode_quantiles(self.quantile_head(pooled))
        return prob, quantiles

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        prob, quantiles = self.forward_quantiles(x, tau)
        return prob, quantiles[:, 1]


def create_model(name: str, seq_len: int, num_features: int) -> nn.Module:
    key = name.strip().upper()
    if key == "MLP":
        return MLPTime2Vec(seq_len=seq_len, num_features=num_features)
    if key == "LSTM":
        return LSTMTime2Vec(seq_len=seq_len, num_features=num_features)
    if key == "DLINEAR":
        return DLinearTime2Vec(seq_len=seq_len, num_features=num_features)
    if key == "PATCHTST":
        return PatchTSTTime2Vec(seq_len=seq_len, num_features=num_features)
    if key == "ITRANSFORMER":
        return ITransformerTime2Vec(seq_len=seq_len, num_features=num_features)
    raise ValueError(f"Unsupported architecture: {name}")

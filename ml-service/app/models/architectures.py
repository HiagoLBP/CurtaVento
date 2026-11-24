"""Short-term wind curtailment neural architectures with Time2Vec.

Implements the three architectures specified in the CurtaVento Pre-TCC
(based on Bispo Junior et al., 2025; Zeng et al., 2023; Hochreiter & Schmidhuber, 1997):
  - MLPTime2Vec
  - LSTMTime2Vec
  - DLinearTime2Vec
"""
from __future__ import annotations

import torch
import torch.nn as nn

from app.features.time2vec import Time2Vec


class SeriesDecomposition(nn.Module):
    """Moving-average decomposition block separating trend and residual/seasonal components."""

    def __init__(self, kernel_size: int = 5) -> None:
        super().__init__()
        self.kernel_size = kernel_size
        self.avg = nn.AvgPool1d(kernel_size=kernel_size, stride=1, padding=0)

    def forward(self, x: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        # x shape: (batch, seq_len, channels)
        pad_left = (self.kernel_size - 1) // 2
        pad_right = self.kernel_size - 1 - pad_left
        front = x[:, :1, :].repeat(1, pad_left, 1)
        end = x[:, -1:, :].repeat(1, pad_right, 1)
        x_padded = torch.cat([front, x, end], dim=1)
        trend = self.avg(x_padded.permute(0, 2, 1)).permute(0, 2, 1)
        seasonal = x - trend
        return seasonal, trend


class DualHeadMixin:
    """Common dual-head output: curtailment probability (logit) and normalized curtailed MWh."""

    @staticmethod
    def build_head(in_dim: int, dropout: float = 0.15) -> tuple[nn.Module, nn.Module]:
        prob_head = nn.Sequential(
            nn.Dropout(dropout),
            nn.Linear(in_dim, 32),
            nn.GELU(),
            nn.Linear(32, 1),
        )
        mwh_head = nn.Sequential(
            nn.Dropout(dropout),
            nn.Linear(in_dim, 32),
            nn.GELU(),
            nn.Linear(32, 1),
            nn.ReLU(),
        )
        return prob_head, mwh_head


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
        self.prob_head, self.mwh_head = self.build_head(64)

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1).flatten(start_dim=1)
        h = self.backbone(fused)
        return torch.sigmoid(self.prob_head(h)).squeeze(-1), self.mwh_head(h).squeeze(-1)


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
        self.prob_head, self.mwh_head = self.build_head(hidden_dim)

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1)
        out, _ = self.lstm(fused)
        last_hidden = self.norm(out[:, -1, :])
        return torch.sigmoid(self.prob_head(last_hidden)).squeeze(-1), self.mwh_head(last_hidden).squeeze(-1)


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
        self.prob_head, self.mwh_head = self.build_head(64)

    def forward(self, x: torch.Tensor, tau: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
        t_emb = self.t2v(tau)
        fused = torch.cat([x, t_emb], dim=-1)  # (B, L, C)
        seasonal, trend = self.decomp(fused)
        # Apply linear projection along the temporal axis per channel
        s_proj = self.linear_seasonal(seasonal.permute(0, 2, 1))  # (B, C, hidden)
        t_proj = self.linear_trend(trend.permute(0, 2, 1))        # (B, C, hidden)
        combined = (s_proj + t_proj).flatten(start_dim=1)
        h = self.channel_proj(combined)
        return torch.sigmoid(self.prob_head(h)).squeeze(-1), self.mwh_head(h).squeeze(-1)


def create_model(name: str, seq_len: int, num_features: int) -> nn.Module:
    key = name.strip().upper()
    if key == "MLP":
        return MLPTime2Vec(seq_len=seq_len, num_features=num_features)
    if key == "LSTM":
        return LSTMTime2Vec(seq_len=seq_len, num_features=num_features)
    if key == "DLINEAR":
        return DLinearTime2Vec(seq_len=seq_len, num_features=num_features)
    raise ValueError(f"Unsupported architecture: {name}")

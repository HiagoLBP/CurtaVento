"""Time2Vec temporal encoding layer (Kazemi et al., 2019).

Converts scalar time representations into a linear trend component plus
periodic sinusoidal components to capture diurnal and weekly wind/load cycles.
"""
from __future__ import annotations

import torch
import torch.nn as nn


class Time2Vec(nn.Module):
    """Learnable Time2Vec layer: [w0 * t + b0, sin(w_i * t + b_i)]."""

    def __init__(self, in_features: int = 1, out_features: int = 8) -> None:
        super().__init__()
        if out_features < 2:
            raise ValueError("out_features must be >= 2 (1 linear + >= 1 periodic).")
        self.in_features = in_features
        self.out_features = out_features
        self.w0 = nn.Parameter(torch.randn(in_features, 1) * 0.1)
        self.b0 = nn.Parameter(torch.zeros(1))
        self.w = nn.Parameter(torch.randn(in_features, out_features - 1) * 0.5)
        self.b = nn.Parameter(torch.zeros(out_features - 1))

    def forward(self, tau: torch.Tensor) -> torch.Tensor:
        """Encode temporal tensor of shape (..., in_features) -> (..., out_features)."""
        linear = torch.matmul(tau, self.w0) + self.b0
        periodic = torch.sin(torch.matmul(tau, self.w) + self.b)
        return torch.cat([linear, periodic], dim=-1)

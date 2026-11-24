"""Standard evaluation metrics for short-term wind curtailment forecasting:
nMAE, nRMSE, Pearson correlation coefficient (r), and binary risk accuracy.
"""
from __future__ import annotations

import numpy as np


def compute_nmae(y_true: np.ndarray, y_pred: np.ndarray, capacity_ref: float = 1.0) -> float:
    denom = max(float(capacity_ref), 1e-6)
    return float(np.mean(np.abs(y_true - y_pred)) / denom * 100.0)


def compute_nrmse(y_true: np.ndarray, y_pred: np.ndarray, capacity_ref: float = 1.0) -> float:
    denom = max(float(capacity_ref), 1e-6)
    rmse = float(np.sqrt(np.mean((y_true - y_pred) ** 2)))
    return float(rmse / denom * 100.0)


def compute_pearson_r(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    if len(y_true) < 2 or np.std(y_true) < 1e-9 or np.std(y_pred) < 1e-9:
        return 0.0
    corr = np.corrcoef(y_true, y_pred)[0, 1]
    return float(0.0 if np.isnan(corr) else corr)


def evaluate_predictions(
    y_true_mwh: np.ndarray,
    y_pred_mwh: np.ndarray,
    y_true_prob: np.ndarray,
    y_pred_prob: np.ndarray,
    capacity_ref: float,
) -> dict[str, float]:
    acc = float(np.mean((y_pred_prob >= 0.5) == (y_true_prob >= 0.5)) * 100.0)
    return {
        "nMAE_pct": round(compute_nmae(y_true_mwh, y_pred_mwh, capacity_ref), 2),
        "nRMSE_pct": round(compute_nrmse(y_true_mwh, y_pred_mwh, capacity_ref), 2),
        "pearson_r": round(compute_pearson_r(y_true_mwh, y_pred_mwh), 4),
        "accuracy_pct": round(acc, 2),
    }

"""Standard and quantile evaluation metrics for short-term wind curtailment forecasting:
nMAE, nRMSE, Pearson correlation coefficient (r), confusion-matrix metrics
(accuracy, precision, recall, specificity, F1), and Pinball (quantile) loss.
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


def compute_pinball_loss(
    y_true: np.ndarray,
    y_pred_quantiles: np.ndarray,
    quantiles: tuple[float, ...] = (0.1, 0.5, 0.9),
) -> float:
    losses: list[float] = []
    for idx, q in enumerate(quantiles):
        err = y_true - y_pred_quantiles[:, idx]
        losses.append(float(np.mean(np.maximum(q * err, (q - 1.0) * err))))
    return float(np.mean(losses))


def evaluate_predictions(
    y_true_mwh: np.ndarray,
    y_pred_mwh: np.ndarray,
    y_true_prob: np.ndarray,
    y_pred_prob: np.ndarray,
    capacity_ref: float,
    y_pred_quantiles_mwh: np.ndarray | None = None,
    train_time_sec: float | None = None,
) -> dict[str, object]:
    y_true_bin = y_true_prob >= 0.5
    y_pred_bin = y_pred_prob >= 0.5

    vp = int(np.sum(y_true_bin & y_pred_bin))
    vn = int(np.sum((~y_true_bin) & (~y_pred_bin)))
    fp = int(np.sum((~y_true_bin) & y_pred_bin))
    fn = int(np.sum(y_true_bin & (~y_pred_bin)))

    total = max(vp + vn + fp + fn, 1)
    acc = (vp + vn) / total * 100.0
    precision = (vp / (vp + fp) * 100.0) if (vp + fp) > 0 else 0.0
    recall = (vp / (vp + fn) * 100.0) if (vp + fn) > 0 else 0.0
    specificity = (vn / (vn + fp) * 100.0) if (vn + fp) > 0 else 0.0
    f1 = (2.0 * precision * recall / (precision + recall)) if (precision + recall) > 0 else 0.0

    result: dict[str, object] = {
        "nMAE_pct": round(compute_nmae(y_true_mwh, y_pred_mwh, capacity_ref), 2),
        "nRMSE_pct": round(compute_nrmse(y_true_mwh, y_pred_mwh, capacity_ref), 2),
        "pearson_r": round(compute_pearson_r(y_true_mwh, y_pred_mwh), 4),
        "accuracy_pct": round(acc, 2),
        "precision_pct": round(precision, 2),
        "recall_pct": round(recall, 2),
        "specificity_pct": round(specificity, 2),
        "f1_pct": round(f1, 2),
        "confusion_matrix": {"vp": vp, "vn": vn, "fp": fp, "fn": fn},
    }
    if y_pred_quantiles_mwh is not None:
        result["pinball_loss_mwh"] = round(compute_pinball_loss(y_true_mwh, y_pred_quantiles_mwh), 4)
    if train_time_sec is not None:
        result["train_time_sec"] = round(float(train_time_sec), 3)
    return result

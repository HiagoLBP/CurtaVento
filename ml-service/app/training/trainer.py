"""Training and benchmark pipeline for MLP, LSTM and DLinear with Time2Vec.

Loads real hourly curtailment and generation records from MySQL (`curtavento`),
builds 24h sliding windows for 6h and 12h prediction horizons, trains all three
PyTorch architectures chronologically, and saves checkpoints + evaluation metrics
(`nMAE`, `nRMSE`, Pearson `r`, `accuracy_pct`).
"""
from __future__ import annotations

import json
import os
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

import numpy as np
import pandas as pd
import torch
import torch.nn as nn
from sklearn.preprocessing import StandardScaler
from torch.utils.data import DataLoader, TensorDataset

from app.evaluation.metrics import evaluate_predictions
from app.models.architectures import create_model

ARTIFACTS_DIR = Path(__file__).resolve().parents[2] / "artifacts"
SEQ_LEN = 24
FEATURE_COLS = [
    "vento_ms",
    "radiacao_norm",
    "carga_baixa_flag",
    "geracao_estimada_norm",
    "corte_lag1_norm",
]


@dataclass
class DatasetSplit:
    x_train: np.ndarray
    tau_train: np.ndarray
    y_mwh_train: np.ndarray
    y_prob_train: np.ndarray
    x_test: np.ndarray
    tau_test: np.ndarray
    y_mwh_test: np.ndarray
    y_prob_test: np.ndarray
    scaler: StandardScaler
    capacity_ref: float


def _load_real_mysql_series(max_rows: int = 120000) -> pd.DataFrame:
    import mysql.connector

    conn = mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD", "admin123"),
        database=os.getenv("DB_NAME", "curtavento"),
    )
    query = """
        SELECT
            DATE_FORMAT(b.instante, '%Y-%m-%d %H:00:00') AS hora,
            AVG(COALESCE(b.vento_verificado_ms, 7.0)) AS vento_ms,
            SUM(b.geracao_estimada_mw * 0.5) AS estimada_mwh,
            SUM(b.energia_cortada_mwh) AS cortada_mwh,
            SUM(u.capacidade_instalada_mw) / COUNT(DISTINCT b.usina_id) * COUNT(DISTINCT b.usina_id) AS cap_ref
        FROM historico_corte_ons_bloco b
        JOIN usina u ON u.id = b.usina_id
        GROUP BY hora
        ORDER BY hora ASC
        LIMIT %s
    """
    try:
        cur = conn.cursor(dictionary=True)
        cur.execute(query, (max_rows,))
        rows = cur.fetchall()
        cur.close()
        df = pd.DataFrame(rows)
    finally:
        conn.close()

    if df.empty:
        return df

    df["dt"] = pd.to_datetime(df["hora"])
    df = df.sort_values("dt").reset_index(drop=True)
    return df


def _enrich_features(df: pd.DataFrame) -> tuple[pd.DataFrame, float]:
    df = df.copy()
    for col in ("vento_ms", "estimada_mwh", "cortada_mwh"):
        if col in df.columns:
            df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0.0).astype(float)

    hours = df["dt"].dt.hour.to_numpy(dtype=float)
    dows = df["dt"].dt.day_of_week.to_numpy(dtype=float)

    # Solar duck-curve diurnal profile (0 at night, ~1 at noon)
    solar_norm = np.where((hours >= 6) & (hours <= 18), np.sin((hours - 6.0) * np.pi / 12.0), 0.0)
    carga_baixa = np.where((dows >= 5) | ((hours >= 10) & (hours <= 15)), 1.0, 0.0)

    cap_ref = float(max(df["estimada_mwh"].quantile(0.95), 1.0))
    est_norm = np.clip(df["estimada_mwh"].to_numpy(dtype=float) / cap_ref, 0.0, 2.0)
    corte_norm = np.clip(df["cortada_mwh"].to_numpy(dtype=float) / cap_ref, 0.0, 1.5)
    corte_lag1 = np.roll(corte_norm, 1)
    corte_lag1[0] = 0.0

    df["vento_ms"] = df["vento_ms"].replace(0.0, 7.0)
    df["radiacao_norm"] = solar_norm
    df["carga_baixa_flag"] = carga_baixa
    df["geracao_estimada_norm"] = est_norm
    df["corte_lag1_norm"] = corte_lag1
    df["corte_norm"] = corte_norm
    df["tau"] = (dows * 24.0 + hours) / 168.0
    return df, cap_ref


def generate_synthetic_series(n_hours: int = 2400, seed: int = 42) -> pd.DataFrame:
    """Deterministic fallback series strictly for isolated unit testing."""
    rng = np.random.default_rng(seed)
    dts = pd.date_range("2026-01-01", periods=n_hours, freq="h")
    hours = dts.hour.to_numpy(dtype=float)
    vento = 7.5 + 3.0 * np.sin(hours * np.pi / 12.0 - 0.5) + rng.normal(0, 1.1, size=n_hours)
    vento = np.clip(vento, 1.0, 20.0)
    solar = np.where((hours >= 6) & (hours <= 18), np.sin((hours - 6.0) * np.pi / 12.0), 0.0)
    estimada = np.clip((vento / 12.0) ** 2 * 100.0, 2.0, 120.0)
    prob = np.clip(0.35 * (vento > 8.5) + 0.4 * (solar > 0.5) + rng.normal(0, 0.08, size=n_hours), 0.0, 1.0)
    cortada = np.where(prob > 0.42, estimada * np.clip(prob, 0.2, 0.85), 0.0)
    return pd.DataFrame({
        "dt": dts,
        "vento_ms": vento,
        "estimada_mwh": estimada,
        "cortada_mwh": cortada,
    })


def build_dataset_split(horizon_hours: int, use_synthetic_if_unavailable: bool = False) -> tuple[DatasetSplit, str]:
    fonte = "MYSQL_ONS_REAL"
    try:
        df = _load_real_mysql_series()
        if df.empty or len(df) < SEQ_LEN + horizon_hours + 50:
            raise ValueError("Série insuficiente no MySQL para treinamento")
    except Exception:
        if not use_synthetic_if_unavailable:
            raise
        fonte = "SYNTHETIC_UNIT_TEST"
        df = generate_synthetic_series()

    df, cap_ref = _enrich_features(df)
    feats = df[FEATURE_COLS].to_numpy(dtype=np.float32)
    tau = df["tau"].to_numpy(dtype=np.float32)
    corte_norm = df["corte_norm"].to_numpy(dtype=np.float32)

    xs, taus, ymwh, yprob = [], [], [], []
    limit = len(df) - SEQ_LEN - horizon_hours
    for i in range(limit):
        target_idx = i + SEQ_LEN + horizon_hours - 1
        xs.append(feats[i : i + SEQ_LEN])
        taus.append(tau[i : i + SEQ_LEN, None])
        val = float(corte_norm[target_idx])
        ymwh.append(val)
        yprob.append(1.0 if val > 0.02 else 0.0)

    x_arr = np.asarray(xs, dtype=np.float32)
    tau_arr = np.asarray(taus, dtype=np.float32)
    ymwh_arr = np.asarray(ymwh, dtype=np.float32)
    yprob_arr = np.asarray(yprob, dtype=np.float32)

    split_idx = int(len(x_arr) * 0.8)
    scaler = StandardScaler()
    n_train, l, f = x_arr[:split_idx].shape
    x_train_scaled = scaler.fit_transform(x_arr[:split_idx].reshape(-1, f)).reshape(n_train, l, f).astype(np.float32)
    n_test = len(x_arr) - split_idx
    x_test_scaled = scaler.transform(x_arr[split_idx:].reshape(-1, f)).reshape(n_test, l, f).astype(np.float32)

    split = DatasetSplit(
        x_train=x_train_scaled,
        tau_train=tau_arr[:split_idx],
        y_mwh_train=ymwh_arr[:split_idx],
        y_prob_train=yprob_arr[:split_idx],
        x_test=x_test_scaled,
        tau_test=tau_arr[split_idx:],
        y_mwh_test=ymwh_arr[split_idx:],
        y_prob_test=yprob_arr[split_idx:],
        scaler=scaler,
        capacity_ref=cap_ref,
    )
    return split, fonte


def _train_single_model(
    arch_name: str,
    split: DatasetSplit,
    epochs: int = 18,
    batch_size: int = 64,
    lr: float = 1e-3,
) -> tuple[nn.Module, dict[str, float]]:
    torch.manual_seed(42)
    model = create_model(arch_name, seq_len=SEQ_LEN, num_features=len(FEATURE_COLS))
    optimizer = torch.optim.AdamW(model.parameters(), lr=lr, weight_decay=1e-4)
    bce = nn.BCELoss()
    mse = nn.MSELoss()

    ds = TensorDataset(
        torch.from_numpy(split.x_train),
        torch.from_numpy(split.tau_train),
        torch.from_numpy(split.y_prob_train),
        torch.from_numpy(split.y_mwh_train),
    )
    loader = DataLoader(ds, batch_size=batch_size, shuffle=True)

    model.train()
    for _ in range(epochs):
        for bx, bt, bp, bm in loader:
            optimizer.zero_grad()
            pred_p, pred_m = model(bx, bt)
            loss = bce(pred_p, bp) + 2.0 * mse(pred_m, bm)
            loss.backward()
            optimizer.step()

    model.eval()
    with torch.no_grad():
        tp, tm = model(torch.from_numpy(split.x_test), torch.from_numpy(split.tau_test))
        pred_prob = tp.numpy()
        pred_mwh = tm.numpy() * split.capacity_ref

    true_mwh = split.y_mwh_test * split.capacity_ref
    metrics = evaluate_predictions(
        y_true_mwh=true_mwh,
        y_pred_mwh=pred_mwh,
        y_true_prob=split.y_prob_test,
        y_pred_prob=pred_prob,
        capacity_ref=split.capacity_ref,
    )
    return model, metrics


def run_benchmark_and_save(
    horizons: tuple[int, ...] = (6, 12),
    epochs: int = 18,
    use_synthetic_if_unavailable: bool = False,
) -> dict[str, Any]:
    ARTIFACTS_DIR.mkdir(parents=True, exist_ok=True)
    report: dict[str, Any] = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "seq_len_hours": SEQ_LEN,
        "encoding": "Time2Vec",
        "horizons": {},
    }

    best_overall_arch = "LSTM"
    best_overall_nrmse = float("inf")

    for h in horizons:
        split, fonte = build_dataset_split(horizon_hours=h, use_synthetic_if_unavailable=use_synthetic_if_unavailable)
        report["source"] = fonte
        report["train_samples"] = int(len(split.x_train))
        report["test_samples"] = int(len(split.x_test))
        report["capacity_ref_mwh"] = round(split.capacity_ref, 2)

        horizon_res: dict[str, Any] = {}
        for arch in ("MLP", "LSTM", "DLinear"):
            model, metrics = _train_single_model(arch, split, epochs=epochs)
            horizon_res[f"{arch}+Time2Vec"] = metrics
            ckpt_path = ARTIFACTS_DIR / f"{arch.lower()}_time2vec_h{h}.pt"
            torch.save(
                {
                    "arch": arch,
                    "horizon": h,
                    "seq_len": SEQ_LEN,
                    "num_features": len(FEATURE_COLS),
                    "state_dict": model.state_dict(),
                    "scaler_mean": split.scaler.mean_.tolist(),
                    "scaler_scale": split.scaler.scale_.tolist(),
                    "capacity_ref": split.capacity_ref,
                    "metrics": metrics,
                },
                ckpt_path,
            )
            if h == 12 and metrics["nRMSE_pct"] < best_overall_nrmse:
                best_overall_nrmse = metrics["nRMSE_pct"]
                best_overall_arch = f"{arch}+Time2Vec"

        report["horizons"][f"{h}h"] = horizon_res

    report["recommended_model"] = best_overall_arch
    report_path = ARTIFACTS_DIR / "benchmark_report.json"
    report_path.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding="utf-8")
    return report


if __name__ == "__main__":
    summary = run_benchmark_and_save(horizons=(6, 12), epochs=18, use_synthetic_if_unavailable=False)
    print(json.dumps(summary, indent=2, ensure_ascii=False))

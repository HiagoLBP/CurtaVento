"""Training and benchmark pipeline for MLP, LSTM and DLinear with Time2Vec.

Loads real hourly curtailment and generation records from MySQL (`curtavento`),
builds 24h sliding windows for 6h and 12h prediction horizons, trains all three
PyTorch architectures chronologically, and saves checkpoints + evaluation metrics
(`nMAE`, `nRMSE`, Pearson `r`, `accuracy_pct`).
"""
from __future__ import annotations

import json
import os
import time
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

import numpy as np
import pandas as pd
import torch
import torch.nn as nn
from sklearn.linear_model import LogisticRegression, Ridge
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
    "capacidade_norm",
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


def _load_real_mysql_series(max_usinas: int = 20, max_rows: int = 120000) -> pd.DataFrame:
    import mysql.connector

    conn = mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD", "admin123"),
        database=os.getenv("DB_NAME", "curtavento"),
    )
    # Multi-site panel pooling (Kean & O'Sullivan 2026): train across top wind farms individually normalized
    # by each plant's installed MW capacity instead of collapsing the entire state into a single aggregate series.
    query = """
        SELECT
            b.usina_id AS usina_id,
            DATE_FORMAT(b.instante, '%Y-%m-%d %H:00:00') AS hora,
            AVG(COALESCE(b.vento_verificado_ms, 7.0)) AS vento_ms,
            SUM(b.geracao_estimada_mw * 0.5) AS estimada_mwh,
            SUM(b.energia_cortada_mwh) AS cortada_mwh,
            COALESCE(MAX(u.capacidade_instalada_mw), 30.0) AS cap_mw
        FROM historico_corte_ons_bloco b
        JOIN usina u ON u.id = b.usina_id
        JOIN (
            SELECT usina_id
            FROM historico_corte_ons_bloco
            GROUP BY usina_id
            ORDER BY COUNT(*) DESC
            LIMIT %s
        ) top_u ON top_u.usina_id = b.usina_id
        GROUP BY b.usina_id, hora
        ORDER BY b.usina_id ASC, hora ASC
        LIMIT %s
    """
    try:
        cur = conn.cursor(dictionary=True)
        cur.execute(query, (max_usinas, max_rows))
        rows = cur.fetchall()
        cur.close()
        df = pd.DataFrame(rows)
    finally:
        conn.close()

    if df.empty:
        return df

    df["dt"] = pd.to_datetime(df["hora"])
    df = df.sort_values(["usina_id", "dt"]).reset_index(drop=True)
    return df


def _enrich_features(df: pd.DataFrame) -> tuple[pd.DataFrame, float]:
    df = df.copy()
    for col in ("vento_ms", "estimada_mwh", "cortada_mwh", "cap_mw"):
        if col in df.columns:
            df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0.0).astype(float)
    if "usina_id" not in df.columns:
        df["usina_id"] = 0
    if "cap_mw" not in df.columns or (df["cap_mw"] <= 0).all():
        df["cap_mw"] = float(max(df["estimada_mwh"].quantile(0.95), 30.0))

    hours = df["dt"].dt.hour.to_numpy(dtype=float)
    dows = df["dt"].dt.day_of_week.to_numpy(dtype=float)
    doy = df["dt"].dt.day_of_year.to_numpy(dtype=float)

    # Solar duck-curve diurnal profile (0 at night, ~1 at noon)
    solar_norm = np.where((hours >= 6) & (hours <= 18), np.sin((hours - 6.0) * np.pi / 12.0), 0.0)
    carga_baixa = np.where((dows >= 5) | ((hours >= 10) & (hours <= 15)), 1.0, 0.0)

    plant_cap = np.maximum(df["cap_mw"].to_numpy(dtype=float), 1.0)
    est_norm = np.clip(df["estimada_mwh"].to_numpy(dtype=float) / plant_cap, 0.0, 2.0)
    corte_norm = np.clip(df["cortada_mwh"].to_numpy(dtype=float) / plant_cap, 0.0, 1.5)
    cap_norm = np.clip(plant_cap / 100.0, 0.05, 2.0)

    df["corte_norm"] = corte_norm
    df["corte_lag1_norm"] = df.groupby("usina_id")["corte_norm"].shift(1).fillna(0.0).to_numpy(dtype=float)
    df["vento_ms"] = df["vento_ms"].replace(0.0, 7.0)
    df["radiacao_norm"] = solar_norm
    df["carga_baixa_flag"] = carga_baixa
    df["geracao_estimada_norm"] = est_norm
    df["capacidade_norm"] = cap_norm
    # Continuous monotonic weeks-of-year (avoids Monday 00h discontinuity from mod-168 wrapping)
    df["tau"] = ((doy - 1.0) * 24.0 + hours) / 168.0
    cap_ref = float(np.median(plant_cap))
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
    fonte = "MYSQL_ONS_PANEL"
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
    split_dt = df["dt"].quantile(0.8)

    xs_tr, taus_tr, ymwh_tr, yprob_tr = [], [], [], []
    xs_te, taus_te, ymwh_te, yprob_te = [], [], [], []

    for _, g in df.groupby("usina_id", sort=False):
        if len(g) <= SEQ_LEN + horizon_hours:
            continue
        feats = g[FEATURE_COLS].to_numpy(dtype=np.float32)
        tau = g["tau"].to_numpy(dtype=np.float32)
        corte_norm = g["corte_norm"].to_numpy(dtype=np.float32)
        dts = g["dt"].to_numpy()
        limit = len(g) - SEQ_LEN - horizon_hours
        for i in range(limit):
            target_idx = i + SEQ_LEN + horizon_hours - 1
            win_x = feats[i : i + SEQ_LEN]
            win_t = tau[i : i + SEQ_LEN, None]
            val = float(corte_norm[target_idx])
            prob_val = 1.0 if val > 0.02 else 0.0
            if pd.Timestamp(dts[target_idx]) <= split_dt:
                xs_tr.append(win_x)
                taus_tr.append(win_t)
                ymwh_tr.append(val)
                yprob_tr.append(prob_val)
            else:
                xs_te.append(win_x)
                taus_te.append(win_t)
                ymwh_te.append(val)
                yprob_te.append(prob_val)

    x_tr = np.asarray(xs_tr, dtype=np.float32)
    tau_tr = np.asarray(taus_tr, dtype=np.float32)
    ymwh_tr_arr = np.asarray(ymwh_tr, dtype=np.float32)
    yprob_tr_arr = np.asarray(yprob_tr, dtype=np.float32)

    x_te = np.asarray(xs_te, dtype=np.float32)
    tau_te = np.asarray(taus_te, dtype=np.float32)
    ymwh_te_arr = np.asarray(ymwh_te, dtype=np.float32)
    yprob_te_arr = np.asarray(yprob_te, dtype=np.float32)

    if len(x_tr) > 8000:
        idx_tr = np.linspace(0, len(x_tr) - 1, 8000, dtype=int)
        x_tr, tau_tr, ymwh_tr_arr, yprob_tr_arr = x_tr[idx_tr], tau_tr[idx_tr], ymwh_tr_arr[idx_tr], yprob_tr_arr[idx_tr]
    if len(x_te) > 2500:
        idx_te = np.linspace(0, len(x_te) - 1, 2500, dtype=int)
        x_te, tau_te, ymwh_te_arr, yprob_te_arr = x_te[idx_te], tau_te[idx_te], ymwh_te_arr[idx_te], yprob_te_arr[idx_te]

    scaler = StandardScaler()
    n_train, l, f = x_tr.shape
    x_train_scaled = scaler.fit_transform(x_tr.reshape(-1, f)).reshape(n_train, l, f).astype(np.float32)
    n_test = len(x_te)
    x_test_scaled = scaler.transform(x_te.reshape(-1, f)).reshape(n_test, l, f).astype(np.float32)

    split = DatasetSplit(
        x_train=x_train_scaled,
        tau_train=tau_tr,
        y_mwh_train=ymwh_tr_arr,
        y_prob_train=yprob_tr_arr,
        x_test=x_test_scaled,
        tau_test=tau_te,
        y_mwh_test=ymwh_te_arr,
        y_prob_test=yprob_te_arr,
        scaler=scaler,
        capacity_ref=cap_ref,
    )
    return split, fonte


def _quantile_pinball_loss(
    pred_quantiles: torch.Tensor,
    target: torch.Tensor,
    quantiles: tuple[float, ...] = (0.1, 0.5, 0.9),
) -> torch.Tensor:
    total = torch.tensor(0.0, dtype=pred_quantiles.dtype, device=pred_quantiles.device)
    for idx, q in enumerate(quantiles):
        err = target - pred_quantiles[:, idx]
        total = total + torch.mean(torch.maximum(q * err, (q - 1.0) * err))
    return total / len(quantiles)


def _train_single_model(
    arch_name: str,
    split: DatasetSplit,
    epochs: int = 18,
    batch_size: int = 128,
    lr: float = 1e-3,
) -> tuple[nn.Module, dict[str, Any]]:
    t0 = time.perf_counter()
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
            pred_p, pred_q = model.forward_quantiles(bx, bt)
            loss = bce(pred_p, bp) + 2.0 * mse(pred_q[:, 1], bm) + 1.5 * _quantile_pinball_loss(pred_q, bm)
            loss.backward()
            optimizer.step()

    elapsed = time.perf_counter() - t0
    model.eval()
    with torch.no_grad():
        tp, tq = model.forward_quantiles(torch.from_numpy(split.x_test), torch.from_numpy(split.tau_test))
        pred_prob = tp.numpy()
        pred_q_mwh = tq.numpy() * split.capacity_ref
        pred_mwh = pred_q_mwh[:, 1]

    true_mwh = split.y_mwh_test * split.capacity_ref
    metrics = evaluate_predictions(
        y_true_mwh=true_mwh,
        y_pred_mwh=pred_mwh,
        y_true_prob=split.y_prob_test,
        y_pred_prob=pred_prob,
        capacity_ref=split.capacity_ref,
        y_pred_quantiles_mwh=pred_q_mwh,
        train_time_sec=elapsed,
    )
    return model, metrics


def _train_linear_baseline(split: DatasetSplit) -> dict[str, Any]:
    t0 = time.perf_counter()
    x_tr = split.x_train.reshape(len(split.x_train), -1)
    x_te = split.x_test.reshape(len(split.x_test), -1)

    reg = Ridge(alpha=1.0)
    reg.fit(x_tr, split.y_mwh_train)
    pred_mwh = np.clip(reg.predict(x_te), 0.0, 1.5) * split.capacity_ref

    if len(np.unique(split.y_prob_train)) >= 2:
        clf = LogisticRegression(max_iter=300)
        clf.fit(x_tr, split.y_prob_train)
        pred_prob = clf.predict_proba(x_te)[:, 1]
    else:
        const = float(split.y_prob_train[0]) if len(split.y_prob_train) > 0 else 0.0
        pred_prob = np.full(len(x_te), const, dtype=np.float32)

    elapsed = time.perf_counter() - t0
    true_mwh = split.y_mwh_test * split.capacity_ref
    return evaluate_predictions(
        y_true_mwh=true_mwh,
        y_pred_mwh=pred_mwh,
        y_true_prob=split.y_prob_test,
        y_pred_prob=pred_prob,
        capacity_ref=split.capacity_ref,
        train_time_sec=elapsed,
    )


def run_benchmark_and_save(
    horizons: tuple[int, ...] = (6, 12),
    epochs: int = 18,
    use_synthetic_if_unavailable: bool = False,
) -> dict[str, Any]:
    ARTIFACTS_DIR.mkdir(parents=True, exist_ok=True)
    report: dict[str, Any] = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "seq_len_hours": SEQ_LEN,
        "encoding": "Time2Vec + Quantile P10/P50/P90",
        "horizons": {},
    }

    best_overall_arch = "LSTM+Time2Vec"
    best_overall_nrmse = float("inf")

    for h in horizons:
        split, fonte = build_dataset_split(horizon_hours=h, use_synthetic_if_unavailable=use_synthetic_if_unavailable)
        report["source"] = fonte
        report["train_samples"] = int(len(split.x_train))
        report["test_samples"] = int(len(split.x_test))
        report["capacity_ref_mwh"] = round(split.capacity_ref, 2)

        horizon_res: dict[str, Any] = {}
        horizon_res["LinearBaseline"] = _train_linear_baseline(split)

        for arch in ("MLP", "LSTM", "DLinear", "PatchTST", "iTransformer"):
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

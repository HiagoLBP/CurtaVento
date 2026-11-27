"""FastAPI prediction microservice for CurtaVento.

Serves short-term (6h / 12h) curtailment probability and MWh forecasts using
trained PyTorch + Time2Vec neural architectures (MLP, LSTM, DLinear) and
exposes model comparison metrics (nMAE, nRMSE, Pearson r).
"""
from __future__ import annotations

import json
import math
from datetime import datetime
from typing import Any

import numpy as np
import torch
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from app.models.architectures import create_model
from app.training.trainer import ARTIFACTS_DIR, FEATURE_COLS, SEQ_LEN, run_benchmark_and_save

app = FastAPI(
    title="CurtaVento ML Microservice",
    description="Inferência de risco de curtailment eólico (6h e 12h) com MLP, LSTM e DLinear + Time2Vec.",
    version="1.0.0",
)


class HourlyPointIn(BaseModel):
    dataHora: str
    vento80mMs: float = 0.0
    radiacaoSolarWm2: float = 0.0
    capacidadeMw: float = 30.0
    diasComCorteRecente: int = 0


class PredictRequest(BaseModel):
    usinaId: int
    usinaNome: str = ""
    capacidadeMw: float = 30.0
    diasComCorteRecente: int = 0
    arquitetura: str = Field(default="AUTO", description="AUTO, ITRANSFORMER, PATCHTST, LSTM, DLINEAR ou MLP")
    curvaEntrada: list[HourlyPointIn]


class ModelCache:
    loaded: dict[str, tuple[torch.nn.Module, np.ndarray, np.ndarray, float, dict[str, Any]]] = {}

    @classmethod
    def clear(cls) -> None:
        cls.loaded.clear()

    @classmethod
    def get_model(cls, arch: str, horizon: int):
        key = f"{arch.lower()}_{horizon}"
        if key in cls.loaded:
            return cls.loaded[key]

        ckpt_path = ARTIFACTS_DIR / f"{arch.lower()}_time2vec_h{horizon}.pt"
        if not ckpt_path.exists():
            return None

        ckpt = torch.load(ckpt_path, map_location="cpu", weights_only=False)
        model = create_model(arch, seq_len=ckpt["seq_len"], num_features=ckpt["num_features"])
        model.load_state_dict(ckpt["state_dict"])
        model.eval()
        mean = np.asarray(ckpt["scaler_mean"], dtype=np.float32)
        scale = np.asarray(ckpt["scaler_scale"], dtype=np.float32)
        cap_ref = float(ckpt["capacity_ref"])
        metrics = ckpt.get("metrics", {})
        cls.loaded[key] = (model, mean, scale, cap_ref, metrics)
        return cls.loaded[key]


def _resolve_arch(requested: str) -> str:
    req = requested.strip().upper()
    if req in ("MLP", "LSTM", "DLINEAR", "PATCHTST", "ITRANSFORMER"):
        return req
    report_path = ARTIFACTS_DIR / "benchmark_report.json"
    if report_path.exists():
        try:
            report = json.loads(report_path.read_text(encoding="utf-8"))
            rec = report.get("recommended_model", "LSTM+Time2Vec").split("+")[0].upper()
            if rec in ("MLP", "LSTM", "DLINEAR", "PATCHTST", "ITRANSFORMER"):
                return rec
        except Exception:
            pass
    return "LSTM"


def _build_sequence_tensors(points: list[HourlyPointIn], capacidade_mw: float, dias_corte: int):
    # Ensure at least SEQ_LEN points by repeating earliest or slicing latest 24 points
    if not points:
        raise ValueError("curvaEntrada vazia")

    seq = list(points)
    while len(seq) < SEQ_LEN:
        seq.insert(0, seq[0])

    # Build full feature array so sliding windows of size SEQ_LEN can score each hour
    feats_list = []
    tau_list = []
    corte_lag = min(1.0, max(0.0, dias_corte / 15.0)) * 0.45
    cap_norm = min(2.0, max(0.05, capacidade_mw / 100.0))

    for p in seq:
        dt = datetime.fromisoformat(p.dataHora[:19])
        hour = float(dt.hour)
        dow = float(dt.weekday())
        doy = float(dt.timetuple().tm_yday)
        rad_norm = min(1.2, max(0.0, p.radiacaoSolarWm2 / 850.0))
        carga_baixa = 1.0 if (dow >= 5 or 10 <= hour <= 15) else 0.0
        est_norm = min(1.5, max(0.0, (p.vento80mMs / 12.0) ** 2))
        feats_list.append([
            float(p.vento80mMs),
            rad_norm,
            carga_baixa,
            est_norm,
            corte_lag,
            cap_norm,
        ])
        tau_list.append([((doy - 1.0) * 24.0 + hour) / 168.0])

    return np.asarray(feats_list, dtype=np.float32), np.asarray(tau_list, dtype=np.float32)


@app.get("/health")
def health() -> dict[str, Any]:
    report_path = ARTIFACTS_DIR / "benchmark_report.json"
    return {
        "status": "UP",
        "service": "curtavento-ml-service",
        "modelsReady": report_path.exists(),
    }


@app.get("/models/benchmark")
def get_benchmark() -> dict[str, Any]:
    report_path = ARTIFACTS_DIR / "benchmark_report.json"
    if not report_path.exists():
        raise HTTPException(status_code=404, detail="Benchmark ainda não treinado. Execute /models/train.")
    return json.loads(report_path.read_text(encoding="utf-8"))


@app.post("/models/train")
def train_models(epochs: int = 18, use_synthetic_fallback: bool = False) -> dict[str, Any]:
    ModelCache.clear()
    report = run_benchmark_and_save(
        horizons=(6, 12),
        epochs=epochs,
        use_synthetic_if_unavailable=use_synthetic_fallback,
    )
    return report


@app.post("/predict/curtailment")
def predict_curtailment(req: PredictRequest) -> dict[str, Any]:
    arch = _resolve_arch(req.arquitetura)
    loaded_6h = ModelCache.get_model(arch, 6)
    loaded_12h = ModelCache.get_model(arch, 12)
    if loaded_6h is None or loaded_12h is None:
        raise HTTPException(status_code=503, detail="Modelos PyTorch não treinados. Execute /models/train primeiro.")

    feats, tau = _build_sequence_tensors(req.curvaEntrada, req.capacidadeMw, req.diasComCorteRecente)

    # First window represents current state -> predicts t+6h and t+12h
    w0_x = feats[:SEQ_LEN]
    w0_t = tau[:SEQ_LEN]

    def _infer(bundle, x_win, t_win):
        model, mean, scale, cap_ref, metrics = bundle
        x_scaled = ((x_win - mean) / np.where(scale == 0, 1.0, scale))[None, :, :]
        t_tensor = t_win[None, :, :]
        cap = max(req.capacidadeMw, 1.0)
        with torch.no_grad():
            if hasattr(model, "forward_quantiles"):
                prob_t, q_t = model.forward_quantiles(torch.from_numpy(x_scaled), torch.from_numpy(t_tensor))
                q_arr = np.clip(q_t.squeeze(0).numpy(), 0.0, 1.0)
                mwh_p10, mwh_p50, mwh_p90 = [round(float(v) * cap, 2) for v in q_arr]
            else:
                prob_t, mwh_t = model(torch.from_numpy(x_scaled), torch.from_numpy(t_tensor))
                mwh_p50 = round(float(np.clip(mwh_t.item(), 0.0, 1.0)) * cap, 2)
                mwh_p10, mwh_p90 = mwh_p50, mwh_p50
        prob = float(np.clip(prob_t.item(), 0.0, 1.0))
        return prob, mwh_p10, mwh_p50, mwh_p90, metrics

    prob6, p10_6, mwh6, p90_6, metrics6 = _infer(loaded_6h, w0_x, w0_t)
    prob12, p10_12, mwh12, p90_12, metrics12 = _infer(loaded_12h, w0_x, w0_t)

    # Also compute rolling hourly ML scores across the forecast curve for blending/charting
    curva_ml: list[dict[str, Any]] = []
    for idx in range(len(req.curvaEntrada)):
        end_idx = min(len(feats), max(SEQ_LEN, idx + 1))
        start_idx = end_idx - SEQ_LEN
        p_h, q10_h, q50_h, q90_h, _ = _infer(loaded_6h, feats[start_idx:end_idx], tau[start_idx:end_idx])
        curva_ml.append({
            "dataHora": req.curvaEntrada[idx].dataHora,
            "probabilidadeCorte": round(p_h * 100.0, 1),
            "mwhEstimadoCorte": q50_h,
            "mwhEstimadoP10": q10_h,
            "mwhEstimadoP90": q90_h,
        })

    return {
        "usinaId": req.usinaId,
        "usinaNome": req.usinaNome,
        "modeloAtivo": f"{arch}+Time2Vec",
        "janela6h": {
            "horizonteHoras": 6,
            "probabilidadeCortePct": round(prob6 * 100.0, 1),
            "mwhEstimadoCorte": mwh6,
            "mwhEstimadoP10": p10_6,
            "mwhEstimadoP90": p90_6,
            "metricasValidacao": metrics6,
        },
        "janela12h": {
            "horizonteHoras": 12,
            "probabilidadeCortePct": round(prob12 * 100.0, 1),
            "mwhEstimadoCorte": mwh12,
            "mwhEstimadoP10": p10_12,
            "mwhEstimadoP90": p90_12,
            "metricasValidacao": metrics12,
        },
        "curvaHorariaMl": curva_ml,
    }

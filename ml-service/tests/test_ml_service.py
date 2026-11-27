from __future__ import annotations

import numpy as np
import torch
from fastapi.testclient import TestClient

from app.evaluation.metrics import compute_nmae, compute_nrmse, compute_pearson_r
from app.features.time2vec import Time2Vec
from app.main import app
from app.models.architectures import create_model
from app.training.trainer import run_benchmark_and_save


def test_time2vec_output_shape():
    layer = Time2Vec(in_features=1, out_features=8)
    tau = torch.linspace(0.0, 1.0, 24).unsqueeze(0).unsqueeze(-1)
    out = layer(tau)
    assert out.shape == (1, 24, 8)


def test_all_four_architectures_forward():
    x = torch.randn(4, 24, 6)
    tau = torch.randn(4, 24, 1)
    for arch in ("MLP", "LSTM", "DLINEAR", "PATCHTST", "ITRANSFORMER"):
        model = create_model(arch, seq_len=24, num_features=6)
        prob, mwh = model(x, tau)
        assert prob.shape == (4,)
        assert mwh.shape == (4,)
        assert torch.all((prob >= 0.0) & (prob <= 1.0))
        assert torch.all(mwh >= 0.0)
        prob_q, quantiles = model.forward_quantiles(x, tau)
        assert prob_q.shape == (4,)
        assert quantiles.shape == (4, 3)
        assert torch.all(quantiles[:, 0] <= quantiles[:, 1])
        assert torch.all(quantiles[:, 1] <= quantiles[:, 2])


def test_metrics_computation():
    y_true = np.array([10.0, 20.0, 30.0, 40.0])
    y_pred = np.array([12.0, 18.0, 33.0, 37.0])
    assert compute_nmae(y_true, y_pred, capacity_ref=100.0) == 2.5
    assert round(compute_nrmse(y_true, y_pred, capacity_ref=100.0), 4) == round(np.sqrt(6.5), 4)
    assert compute_pearson_r(y_true, y_pred) > 0.95


def test_train_and_predict_endpoints(tmp_path, monkeypatch):
    import app.main as main_mod
    import app.training.trainer as trainer_mod

    monkeypatch.setattr(trainer_mod, "ARTIFACTS_DIR", tmp_path)
    monkeypatch.setattr(main_mod, "ARTIFACTS_DIR", tmp_path)
    main_mod.ModelCache.clear()

    report = run_benchmark_and_save(horizons=(6, 12), epochs=2, use_synthetic_if_unavailable=True)
    assert "6h" in report["horizons"]
    assert "12h" in report["horizons"]
    assert "LSTM+Time2Vec" in report["horizons"]["12h"]
    assert "PatchTST+Time2Vec" in report["horizons"]["12h"]
    assert "iTransformer+Time2Vec" in report["horizons"]["12h"]
    assert "LinearBaseline" in report["horizons"]["12h"]
    assert "confusion_matrix" in report["horizons"]["12h"]["LSTM+Time2Vec"]

    client = TestClient(app)
    res_bench = client.get("/models/benchmark")
    assert res_bench.status_code == 200
    assert res_bench.json()["recommended_model"].endswith("+Time2Vec")

    points = [
        {
            "dataHora": f"2026-11-24T{h % 24:02d}:00:00",
            "vento80mMs": 9.5 + (h % 4) * 0.6,
            "radiacaoSolarWm2": 720.0 if 8 <= (h % 24) <= 16 else 0.0,
            "capacidadeMw": 30.0,
            "diasComCorteRecente": 6,
        }
        for h in range(24)
    ]
    res_pred = client.post(
        "/predict/curtailment",
        json={
            "usinaId": 371,
            "usinaNome": "Ventos de Santa Luzia 15",
            "capacidadeMw": 30.0,
            "diasComCorteRecente": 6,
            "arquitetura": "ITRANSFORMER",
            "curvaEntrada": points,
        },
    )
    assert res_pred.status_code == 200
    body = res_pred.json()
    assert 0.0 <= body["janela6h"]["probabilidadeCortePct"] <= 100.0
    assert 0.0 <= body["janela12h"]["probabilidadeCortePct"] <= 100.0
    assert body["janela6h"]["mwhEstimadoP10"] <= body["janela6h"]["mwhEstimadoCorte"] <= body["janela6h"]["mwhEstimadoP90"]
    assert len(body["curvaHorariaMl"]) == 24

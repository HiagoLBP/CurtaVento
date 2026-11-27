package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.SimularBessUseCase;
import com.curtavento.curta_vento.application.usecase.SimularCargaFlexivelDataCenterUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simuladores")
public class SimuladorController {

    private final SimularBessUseCase simularBessUseCase;
    private final SimularCargaFlexivelDataCenterUseCase simularCargaFlexivelDataCenterUseCase;

    @Autowired
    public SimuladorController(SimularBessUseCase simularBessUseCase,
                               SimularCargaFlexivelDataCenterUseCase simularCargaFlexivelDataCenterUseCase) {
        this.simularBessUseCase = simularBessUseCase;
        this.simularCargaFlexivelDataCenterUseCase = simularCargaFlexivelDataCenterUseCase;
    }

    public SimuladorController(SimularBessUseCase simularBessUseCase) {
        this(simularBessUseCase, null);
    }

    public record BessSimulacaoRequest(
            Long usinaId,
            Integer ano,
            Double potenciaMw,
            Double capacidadeMwh,
            Double eficienciaRtePercentual,
            Double precoDescargaPontaMwh,
            Double capexPorKwhRs,
            Double opexAnualPercentual,
            Double socMinPct,
            Double socMaxPct,
            Double custoDegradacaoMwhRs,
            Double degradacaoAnualCapacidadePct,
            Double taxaDescontoAnualPct,
            Double receitaFixaCapacidadeKwAnoRs
    ) {}

    public record DataCenterSimulacaoRequest(
            Long usinaId,
            Integer ano,
            Double potenciaTiMw,
            Double pue,
            String modoOperacao,
            Double fatorCargaMinimoPct,
            Double tarifaRedeMwhRs,
            Double valorEnergiaAproveitadaMwhRs,
            Double tustKwMesRs,
            Double horizonteBacklogHoras,
            Double rampaMaxPctHora,
            Long usinaSecundariaId,
            Double percentualFlexEspacial,
            Double fatorEmissaoRedeTco2Mwh
    ) {}

    @PostMapping("/bess")
    public ResponseEntity<Map<String, Object>> simularBess(@RequestBody BessSimulacaoRequest req) {
        if (req == null || req.usinaId() == null) {
            throw new IllegalArgumentException("O campo usinaId é obrigatório para simular o BESS.");
        }
        int ano = req.ano() != null ? req.ano() : 2026;
        double pot = req.potenciaMw() != null ? req.potenciaMw() : 10.0;
        double cap = req.capacidadeMwh() != null ? req.capacidadeMwh() : 40.0;
        double rte = req.eficienciaRtePercentual() != null ? req.eficienciaRtePercentual() : 88.0;
        double precoPonta = req.precoDescargaPontaMwh() != null ? req.precoDescargaPontaMwh() : 350.0;
        double capex = req.capexPorKwhRs() != null ? req.capexPorKwhRs() : 1800.0;
        double opex = req.opexAnualPercentual() != null ? req.opexAnualPercentual() : 2.0;
        double socMin = req.socMinPct() != null ? req.socMinPct() : 5.0;
        double socMax = req.socMaxPct() != null ? req.socMaxPct() : 95.0;
        double degCusto = req.custoDegradacaoMwhRs() != null ? req.custoDegradacaoMwhRs() : 10.0;
        double degAnual = req.degradacaoAnualCapacidadePct() != null ? req.degradacaoAnualCapacidadePct() : 2.0;
        double wacc = req.taxaDescontoAnualPct() != null ? req.taxaDescontoAnualPct() : 10.0;
        double recCapKwAno = req.receitaFixaCapacidadeKwAnoRs() != null ? req.receitaFixaCapacidadeKwAnoRs() : 0.0;

        return ResponseEntity.ok(simularBessUseCase.executar(
                req.usinaId(), ano, pot, cap, rte, precoPonta, capex, opex,
                socMin, socMax, degCusto, degAnual, wacc, recCapKwAno));
    }

    @PostMapping("/datacenter")
    public ResponseEntity<Map<String, Object>> simularDataCenter(@RequestBody DataCenterSimulacaoRequest req) {
        if (req == null || req.usinaId() == null) {
            throw new IllegalArgumentException("O campo usinaId é obrigatório para simular o Data Center flexível.");
        }
        if (simularCargaFlexivelDataCenterUseCase == null) {
            throw new IllegalStateException("Simulador de Data Center não configurado.");
        }
        int ano = req.ano() != null ? req.ano() : 2026;
        double potTi = req.potenciaTiMw() != null ? req.potenciaTiMw() : 15.0;
        double pue = req.pue() != null ? req.pue() : 1.35;
        String modo = req.modoOperacao() != null ? req.modoOperacao() : "FLEXIVEL_CURTAILMENT";
        double floorPct = req.fatorCargaMinimoPct() != null ? req.fatorCargaMinimoPct() : 25.0;
        double tarifaRede = req.tarifaRedeMwhRs() != null ? req.tarifaRedeMwhRs() : 220.0;
        double valorMwh = req.valorEnergiaAproveitadaMwhRs() != null ? req.valorEnergiaAproveitadaMwhRs() : 280.0;
        double tust = req.tustKwMesRs() != null ? req.tustKwMesRs() : 12.0;
        double hBacklog = req.horizonteBacklogHoras() != null ? req.horizonteBacklogHoras() : 0.0;
        double rampaMax = req.rampaMaxPctHora() != null ? req.rampaMaxPctHora() : 100.0;
        double betaEspacial = req.percentualFlexEspacial() != null ? req.percentualFlexEspacial() : 25.0;
        double fatorCo2 = req.fatorEmissaoRedeTco2Mwh() != null ? req.fatorEmissaoRedeTco2Mwh() : 0.085;

        return ResponseEntity.ok(simularCargaFlexivelDataCenterUseCase.executar(
                req.usinaId(), ano, potTi, pue, modo, floorPct, tarifaRede, valorMwh, tust,
                hBacklog, rampaMax, req.usinaSecundariaId(), betaEspacial, fatorCo2));
    }
}

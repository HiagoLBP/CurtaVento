package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.SimularBessUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simuladores")
public class SimuladorController {

    private final SimularBessUseCase simularBessUseCase;

    public SimuladorController(SimularBessUseCase simularBessUseCase) {
        this.simularBessUseCase = simularBessUseCase;
    }

    public record BessSimulacaoRequest(
            Long usinaId,
            Integer ano,
            Double potenciaMw,
            Double capacidadeMwh,
            Double eficienciaRtePercentual,
            Double precoDescargaPontaMwh,
            Double capexPorKwhRs,
            Double opexAnualPercentual
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

        return ResponseEntity.ok(simularBessUseCase.executar(
                req.usinaId(), ano, pot, cap, rte, precoPonta, capex, opex));
    }
}

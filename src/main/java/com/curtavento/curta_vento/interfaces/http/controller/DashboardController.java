package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.ObterResumoDashboardUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ObterResumoDashboardUseCase obterResumoDashboardUseCase;

    public DashboardController(ObterResumoDashboardUseCase obterResumoDashboardUseCase) {
        this.obterResumoDashboardUseCase = obterResumoDashboardUseCase;
    }

    @GetMapping("/resumo-geral")
    public ResponseEntity<Map<String, Object>> getResumoGeral(
            @RequestParam(defaultValue = "2026", required = false) int ano,
            @RequestParam(defaultValue = "1000", required = false) int limite) {
        return ResponseEntity.ok(obterResumoDashboardUseCase.executar(ano, limite));
    }
}

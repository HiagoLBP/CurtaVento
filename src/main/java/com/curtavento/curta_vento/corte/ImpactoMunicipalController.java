package com.curtavento.curta_vento.corte;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/impacto-vaf")
public class ImpactoMunicipalController {

    private final HistoricoCorteOnsRepository repository;

    public ImpactoMunicipalController(HistoricoCorteOnsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getImpactoPorMunicipio(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "2026", required = false) int ano) {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate dataFim = ano == hoje.getYear() ? hoje
                : (ano < hoje.getYear() ? java.time.LocalDate.of(ano, 12, 31) : java.time.LocalDate.of(ano, 1, 1).minusDays(1));
        java.time.LocalDateTime start = java.time.LocalDate.of(ano, 1, 1).atStartOfDay();
        java.time.LocalDateTime end = dataFim.atTime(23, 59, 59);
        List<Object[]> resultados = repository.findImpactoPorMunicipio(start, end);
        List<Map<String, Object>> impactos = new ArrayList<>();

        for (Object[] linha : resultados) {
            String municipio = (String) linha[0];
            String estado = "BA"; // Hardcoded default as all our windfarms in this scope are in Bahia
            Long qtdUsinas = (Long) linha[1];
            BigDecimal totalCortado = (BigDecimal) linha[2];
            
            Double mwh = totalCortado != null ? totalCortado.doubleValue() : null;

            Map<String, Object> map = new HashMap<>();
            map.put("municipio", municipio);
            map.put("estado", estado);
            map.put("qtdUsinas", qtdUsinas);
            map.put("energiaCortadaMwh", mwh);
            map.put("faturamentoPerdidoRs", null);
            map.put("icmsNaoGeradoRs", null);
            map.put("arrecadacaoPerdidaVafRs", null);
            map.put("financeiroDisponivel", false);
            
            impactos.add(map);
        }

        return ResponseEntity.ok(impactos);
    }
}

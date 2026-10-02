package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/impacto-vaf")
public class ImpactoMunicipalController {

    private final SpringDataHistoricoCorteOnsRepository repository;

    private static final double ALIQUOTA_ICMS = 0.18;
    private static final double REPASSE_MUNICIPIO = 0.25;

    public ImpactoMunicipalController(SpringDataHistoricoCorteOnsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getImpactoMunicipal() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 31, 23, 59, 59);

        List<Object[]> resultados = repository.findImpactoPorMunicipio(start, end);
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (Object[] linha : resultados) {
            String municipioNome = (String) linha[0];
            Long qtdUsinas = (Long) linha[1];
            BigDecimal energiaCortada = (BigDecimal) linha[2];

            if (energiaCortada == null) {
                energiaCortada = BigDecimal.ZERO;
            }

            double faturamentoPerdido = energiaCortada.doubleValue() * 200.0;
            double icmsNaoGerado = faturamentoPerdido * ALIQUOTA_ICMS;
            double arrecadacaoPerdidaVaf = icmsNaoGerado * REPASSE_MUNICIPIO;

            Map<String, Object> dados = new HashMap<>();
            dados.put("municipio", municipioNome);
            dados.put("estado", "Bahia");
            dados.put("qtdUsinas", qtdUsinas);
            dados.put("energiaCortadaMwh", energiaCortada.doubleValue());
            dados.put("faturamentoPerdidoRs", faturamentoPerdido);
            dados.put("icmsNaoGeradoRs", icmsNaoGerado);
            dados.put("arrecadacaoPerdidaVafRs", arrecadacaoPerdidaVaf);

            resposta.add(dados);
        }

        return ResponseEntity.ok(resposta);
    }
}
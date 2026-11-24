package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.SimularImpactoGovTechUseCase;
import com.curtavento.curta_vento.domain.model.PrecoEnergia;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final SimularImpactoGovTechUseCase govTechUseCase;

    private static final double ALIQUOTA_ICMS = 0.18;
    private static final double REPASSE_MUNICIPIO = 0.25;

    public ImpactoMunicipalController(SpringDataHistoricoCorteOnsRepository repository,
                                      SimularImpactoGovTechUseCase govTechUseCase) {
        this.repository = repository;
        this.govTechUseCase = govTechUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getImpactoMunicipal(@RequestParam(defaultValue = "2026") int ano) {
        LocalDateTime start = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        List<Object[]> resultados = repository.findImpactoPorMunicipio(start, end);
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (Object[] linha : resultados) {
            String municipioNome = (String) linha[0];
            Long qtdUsinas = (Long) linha[1];
            BigDecimal energiaCortada = (BigDecimal) linha[2];

            if (energiaCortada == null) {
                energiaCortada = BigDecimal.ZERO;
            }

            double faturamentoPerdido = energiaCortada.doubleValue() * PrecoEnergia.PRECO_MWH;
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

    @GetMapping("/projecao-plurianual")
    public ResponseEntity<Map<String, Object>> getProjecaoPlurianual(
            @RequestParam(defaultValue = "2026") int ano,
            @RequestParam(required = false) BigDecimal precoReferenciaMwh,
            @RequestParam(required = false) BigDecimal vafTotalBa,
            @RequestParam(required = false) BigDecimal icmsTotalBa) {
        return ResponseEntity.ok(govTechUseCase.calcularProjecaoPlurianualVaf(
                ano, precoReferenciaMwh, vafTotalBa, icmsTotalBa));
    }

    @GetMapping("/arrendamento-rural")
    public ResponseEntity<Map<String, Object>> auditarArrendamentoRural(
            @RequestParam Long usinaId,
            @RequestParam(defaultValue = "2026") int ano,
            @RequestParam(defaultValue = "1.5") double percentualRoyalty,
            @RequestParam(defaultValue = "5") int numProprietarios) {
        return ResponseEntity.ok(govTechUseCase.auditarArrendamentoRural(
                usinaId, ano, percentualRoyalty, numProprietarios));
    }
}

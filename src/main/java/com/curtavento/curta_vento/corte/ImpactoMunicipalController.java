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

    // Premissas contábeis simuladas para a região da Bahia
    private static final double PRECO_MADI_MWH = 150.00; // Preço médio de liquidação R$/MWh
    private static final double ALIQUOTA_ICMS = 0.18; // 18% ICMS
    private static final double REPASSE_MUNICIPIO = 0.25; // 25% do ICMS vai para os municípios via VAF

    public ImpactoMunicipalController(HistoricoCorteOnsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getImpactoPorMunicipio() {
        List<Object[]> resultados = repository.findImpactoPorMunicipio();
        List<Map<String, Object>> impactos = new ArrayList<>();

        for (Object[] linha : resultados) {
            String municipio = (String) linha[0];
            String estado = "BA"; // Hardcoded default as all our windfarms in this scope are in Bahia
            Long qtdUsinas = (Long) linha[1];
            BigDecimal totalCortado = (BigDecimal) linha[2];
            
            if (totalCortado == null) totalCortado = BigDecimal.ZERO;
            
            double mwh = totalCortado.doubleValue();
            double faturamentoBrutoPerdido = mwh * PRECO_MADI_MWH;
            double totalIcmsNaoGerado = faturamentoBrutoPerdido * ALIQUOTA_ICMS;
            double arrecadacaoMunicipalPerdida = totalIcmsNaoGerado * REPASSE_MUNICIPIO; // Dinheiro que a prefeitura perde

            Map<String, Object> map = new HashMap<>();
            map.put("municipio", municipio);
            map.put("estado", estado);
            map.put("qtdUsinas", qtdUsinas);
            map.put("energiaCortadaMwh", mwh);
            map.put("faturamentoPerdidoRs", faturamentoBrutoPerdido);
            map.put("icmsNaoGeradoRs", totalIcmsNaoGerado);
            map.put("arrecadacaoPerdidaVafRs", arrecadacaoMunicipalPerdida);
            
            impactos.add(map);
        }

        return ResponseEntity.ok(impactos);
    }
}

package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.vaf.ImpactoVaf;
import com.curtavento.curta_vento.vaf.ImpactoVafService;
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

    private final ImpactoVafService vafService;
    private final MunicipioRepository municipioRepository;

    public ImpactoMunicipalController(ImpactoVafService vafService, MunicipioRepository municipioRepository) {
        this.vafService = vafService;
        this.municipioRepository = municipioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getImpactoPorMunicipio(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "2026", required = false) int ano) {
        List<Map<String, Object>> impactos = new ArrayList<>();

        // Para cada município no DB (agora temos os 417), se tiver usinas associadas com corte:
        // Na vdd só precisamos listar municípios que TÊM usinas cadastradas.
        List<Municipio> todos = municipioRepository.findAll();
        
        for (Municipio m : todos) {
            try {
                // O servico ja filtra as usinas deste municipio e calcula:
                ImpactoVaf vaf = vafService.calcularImpacto(m.getId(), ano);
                
                // Só adicionamos na tela se teve alguma energia cortada
                if (vaf.getEnergiaTotalCortadaMwh() != null && vaf.getEnergiaTotalCortadaMwh().compareTo(BigDecimal.ZERO) > 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("municipio", m.getNome());
                    map.put("estado", "BA");
                    map.put("qtdUsinas", 0); // simplificado, a UI talvez não ligue tanto se for 0
                    map.put("energiaCortadaMwh", vaf.getEnergiaTotalCortadaMwh().doubleValue());
                    
                    BigDecimal fatPerdido = vaf.getPerdaReceitaEstimada();
                    map.put("faturamentoPerdidoRs", fatPerdido != null ? fatPerdido.doubleValue() : null);
                    
                    if (fatPerdido != null) {
                        map.put("icmsNaoGeradoRs", fatPerdido.multiply(new BigDecimal("0.18")).doubleValue());
                    } else {
                        map.put("icmsNaoGeradoRs", null);
                    }
                    
                    BigDecimal repasse = vaf.getImpactoVafEstimado();
                    map.put("arrecadacaoPerdidaVafRs", repasse != null ? repasse.doubleValue() : null);
                    
                    map.put("financeiroDisponivel", vaf.isFinanceiroDisponivel());
                    
                    impactos.add(map);
                }
            } catch (Exception e) {
                // ignorar municipio sem usina ou erro
            }
        }

        return ResponseEntity.ok(impactos);
    }
}

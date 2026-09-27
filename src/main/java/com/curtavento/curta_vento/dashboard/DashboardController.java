package com.curtavento.curta_vento.dashboard;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaService;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioService;
import com.curtavento.curta_vento.risco.PrevisaoRisco;
import com.curtavento.curta_vento.risco.PrevisaoRiscoService;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaService;
import com.curtavento.curta_vento.vaf.ImpactoVaf;
import com.curtavento.curta_vento.clima.LeituraClimaticaRepository;
import com.curtavento.curta_vento.risco.PrevisaoRiscoRepository;
import com.curtavento.curta_vento.vaf.ImpactoVafRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UsinaService usinaService;
    private final MunicipioService municipioService;
    private final PrevisaoRiscoService riscoService;
    private final ImpactoVafRepository impactoRepository;
    private final LeituraClimaticaService leituraService;
    private final LeituraClimaticaRepository leituraRepo;
    private final PrevisaoRiscoRepository riscoRepo;
    private final com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository corteRepository;

    public DashboardController(UsinaService usinaService,
                               MunicipioService municipioService,
                               PrevisaoRiscoService riscoService,
                               ImpactoVafRepository impactoRepository,
                               LeituraClimaticaService leituraService,
                                 LeituraClimaticaRepository leituraRepo,
                                 PrevisaoRiscoRepository riscoRepo,
                               com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository corteRepository) {
        this.usinaService = usinaService;
        this.municipioService = municipioService;
        this.riscoService = riscoService;
        this.impactoRepository = impactoRepository;
        this.leituraService = leituraService;
        this.leituraRepo = leituraRepo;
        this.riscoRepo = riscoRepo;
        this.corteRepository = corteRepository;
    }

    @GetMapping("/resumo-geral")
    public ResponseEntity<Map<String, Object>> getResumoGeral(@RequestParam(defaultValue = "2026", required = false) int ano, @RequestParam(defaultValue = "1000", required = false) int limite) {
        if (limite < 1) limite = 1000;
        if (limite > 2000) limite = 2000;

        // 1. Usinas e Riscos (Sempre mostra a previsão para as próximas 24h, independente do ano do histórico)
        
        java.time.LocalDateTime start = java.time.LocalDateTime.of(ano, 1, 1, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(ano, 12, 31, 23, 59, 59);
        List<Object[]> rankingCortes = corteRepository.findRankingCortesAno(start, end);
        Map<String, Double> cortesMap = new HashMap<>();
        for (Object[] row : rankingCortes) {
            String uName = (String) row[0];
            java.math.BigDecimal val = (java.math.BigDecimal) row[1];
            cortesMap.put(uName, val != null ? val.doubleValue() : 0.0);
        }
        
        List<com.curtavento.curta_vento.clima.LeituraClimatica> todasLeituras = leituraRepo.findUltimasLeiturasGeral();
        Map<Long, com.curtavento.curta_vento.clima.LeituraClimatica> leituraMap = new HashMap<>();
        for (com.curtavento.curta_vento.clima.LeituraClimatica l : todasLeituras) {
            leituraMap.put(l.getUsina().getId(), l);
        }

        List<com.curtavento.curta_vento.risco.PrevisaoRisco> todosRiscos = riscoRepo.findUltimosRiscosGeral();
        Map<Long, com.curtavento.curta_vento.risco.PrevisaoRisco> riscoMap = new HashMap<>();
        for (com.curtavento.curta_vento.risco.PrevisaoRisco r : todosRiscos) {
            riscoMap.put(r.getUsina().getId(), r);
        }

        List<Map<String, Object>> usinasDTO = new ArrayList<>();

        for (Usina u : usinaService.listarTodas()) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", u.getNome());
            dto.put("operador", u.getOperador());
            dto.put("capacidade", u.getCapacidadeInstaladaMw());
            dto.put("latitude", u.getLatitude());
            dto.put("longitude", u.getLongitude());
            dto.put("municipio", u.getMunicipio() != null ? u.getMunicipio().getNome() : "N/D");
            dto.put("dataInicio", u.getDataInicioOperacao());

            String riscoBase = "BAIXO";
            PrevisaoRisco ultima = riscoMap.get(u.getId());
            if (ultima != null) {
                riscoBase = ultima.getNivelRisco().name();
                dto.put("risco", riscoBase);
                dto.put("fatorRisco", ultima.getFatoresJustificativa());
                dto.put("corte", ultima.getPercentualCorteEstimado() + "%");
            } else {
                dto.put("risco", "BAIXO");
                dto.put("fatorRisco", "Sem risco atípico.");
                dto.put("corte", "0%");
            }

            LeituraClimatica ultimaLeitura = leituraMap.get(u.getId());
            if (ultimaLeitura != null) {
                java.math.BigDecimal ventoBd = ultimaLeitura.getVelocidadeVentoMs();
                Double ventoMs = ventoBd != null ? ventoBd.doubleValue() : null;
                Double v24h = ultimaLeitura.getVentoPrevisao24hMs() != null ? ultimaLeitura.getVentoPrevisao24hMs().doubleValue() : null;
                Double v48h = ultimaLeitura.getVentoPrevisao48hMs() != null ? ultimaLeitura.getVentoPrevisao48hMs().doubleValue() : null;

                dto.put("vento", ventoMs != null ? ventoMs + " m/s" : "N/D");
                dto.put("vento24h", v24h != null ? v24h + " m/s" : "N/D");
                dto.put("vento48h", v48h != null ? v48h + " m/s" : "N/D");
                
                String rAtual = calcularRiscoVisual(riscoBase, ventoMs);
                String r24h = calcularRiscoVisual(riscoBase, v24h);
                String r48h = calcularRiscoVisual(riscoBase, v48h);

                dto.put("riscoAtual", rAtual);
                dto.put("risco24h", r24h);
                dto.put("risco48h", r48h);
                dto.put("risco", r24h);

                if (ultima == null || ultima.getFatoresJustificativa() == null) {
                    if ("CRITICO".equals(r24h)) {
                        dto.put("fatorRisco", "Rajadas previstas elevadas (>= 10 m/s), alto risco de restrição de rede.");
                    } else if ("ALTO".equals(r24h)) {
                        dto.put("fatorRisco", "Vento previsto forte (>= 7 m/s), atenção para corte programado.");
                    } else if ("MEDIO".equals(r24h)) {
                        dto.put("fatorRisco", "Vento moderado, monitoramento preventivo recomendado.");
                    } else {
                        dto.put("fatorRisco", "Sem risco atípico. Ventos dentro dos parâmetros operacionais.");
                    }
                }

                if (ventoMs != null) {
                    if (ventoMs <= 10.0) {
                        dto.put("janelaManutencao", "LIBERADO");
                    } else if (ventoMs <= 15.0) {
                        dto.put("janelaManutencao", "ALERTA");
                    } else {
                        dto.put("janelaManutencao", "BLOQUEADO");
                    }
                } else {
                    dto.put("janelaManutencao", "N/D");
                }
                dto.put("fonteClima", ultimaLeitura.getFonte() != null ? ultimaLeitura.getFonte() : "Open-Meteo");
                dto.put("dataClima", ultimaLeitura.getDataHora() != null ? ultimaLeitura.getDataHora().toString() : "N/D");
            } else {
                dto.put("vento", "N/D");
                dto.put("vento24h", "N/D");
                dto.put("vento48h", "N/D");
                dto.put("riscoAtual", "N/D");
                dto.put("risco24h", "N/D");
                dto.put("risco48h", "N/D");
                dto.put("janelaManutencao", "N/D");
                dto.put("fonteClima", "N/D");
                dto.put("dataClima", "N/D");
                dto.put("fatorRisco", "Sem dados climáticos em tempo real para avaliar o risco.");
            }

            double somaCortesUsina = cortesMap.getOrDefault(u.getNome(), 0.0);
            
            dto.put("energiaCortada", somaCortesUsina);
            dto.put("prejuizoFinanceiro", somaCortesUsina * com.curtavento.curta_vento.util.PrecoEnergia.PRECO_MWH);

            usinasDTO.add(dto);
        }

        
        
        // Ordena por maior prejuizo e aplica o limite
        usinasDTO.sort((a, b) -> Double.compare((Double) b.get("prejuizoFinanceiro"), (Double) a.get("prejuizoFinanceiro")));
        if (limite < usinasDTO.size()) {
            usinasDTO = usinasDTO.subList(0, limite);
        }

        // 2. Municípios e Totais do VAF baseados no ANO selecionado no Dropdown
        List<Map<String, Object>> municipiosDTO = new ArrayList<>();
        double somaEnergiaTotal = 0.0;
        double somaImpactoTotal = 0.0;
        double somaPerdaReceitaTotal = 0.0;

        List<Object[]> impactosMun = corteRepository.findImpactoPorMunicipio(start, end);
        for (Object[] linha : impactosMun) {
            String nome = (String) linha[0];
            java.math.BigDecimal totalCortado = (java.math.BigDecimal) linha[2];
            double mwh = totalCortado != null ? totalCortado.doubleValue() : 0.0;
            
            double perdaReceitaReal = mwh * com.curtavento.curta_vento.util.PrecoEnergia.PRECO_MWH;
            double impactoReal = perdaReceitaReal * 0.18 * 0.25; // 18% ICMS, 25% repasse VAF
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", nome);
            dto.put("valor", impactoReal);
            dto.put("max", 50000000.0);
            dto.put("trend", "Calculado");
            
            somaEnergiaTotal += mwh;
            somaImpactoTotal += impactoReal;
            somaPerdaReceitaTotal += perdaReceitaReal;
            
            municipiosDTO.add(dto);
        }

        // Injeção dos dados analisados da subestação BAIGT para o painel de impacto
        Map<String, Double> motivos = new HashMap<>();
        motivos.put("ENE (Energética)", somaEnergiaTotal * 0.55);
        motivos.put("REL (Rede Local - Estimado)", somaEnergiaTotal * 0.33);
        motivos.put("CNF (Confiabilidade - Estimado)", somaEnergiaTotal * 0.12);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("usinas", usinasDTO);
        resposta.put("municipios", municipiosDTO);
        resposta.put("energiaTotal", somaEnergiaTotal);
        resposta.put("impactoTotal", somaImpactoTotal);
        resposta.put("perdaReceitaTotal", somaPerdaReceitaTotal);
        resposta.put("taxaDesperdicio", 31.9);
        resposta.put("motivosCorte", motivos);

        return ResponseEntity.ok(resposta);
    }

    private String calcularRiscoVisual(String riscoBase, Double vento) {
        if (vento == null) return riscoBase;
        if (vento >= 10.0) return "CRITICO";
        if (vento >= 7.0) return "ALTO";
        if (vento >= 4.0 && !"BAIXO".equals(riscoBase)) return "MEDIO";
        return "BAIXO";
    }
}

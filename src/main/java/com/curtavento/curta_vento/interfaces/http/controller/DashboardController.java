package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
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

    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataMunicipioRepository springDataMunicipioRepository;
    private final SpringDataPrevisaoRiscoRepository riscoService;
    private final SpringDataImpactoVafRepository impactoRepository;
    private final SpringDataLeituraClimaticaRepository leituraService;
    private final SpringDataLeituraClimaticaRepository leituraRepo;
    private final SpringDataPrevisaoRiscoRepository riscoRepo;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    public DashboardController(SpringDataUsinaRepository springDataUsinaRepository,
                               SpringDataMunicipioRepository springDataMunicipioRepository,
                               SpringDataPrevisaoRiscoRepository riscoService,
                               SpringDataImpactoVafRepository impactoRepository,
                               SpringDataLeituraClimaticaRepository leituraService,
                                 SpringDataLeituraClimaticaRepository leituraRepo,
                                 SpringDataPrevisaoRiscoRepository riscoRepo,
                               SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.springDataMunicipioRepository = springDataMunicipioRepository;
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

        // 1. Usinas e Riscos (Sempre mostra a previsÃ£o para as prÃ³ximas 24h, independente do ano do histÃ³rico)
        
        java.time.LocalDateTime start = java.time.LocalDateTime.of(ano, 1, 1, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(ano, 12, 31, 23, 59, 59);
        List<Object[]> rankingCortes = corteRepository.findRankingCortesAno(start, end);
        Map<String, Double> cortesMap = new HashMap<>();
        for (Object[] row : rankingCortes) {
            String uName = (String) row[0];
            java.math.BigDecimal val = (java.math.BigDecimal) row[1];
            cortesMap.put(uName, val != null ? val.doubleValue() : 0.0);
        }
        
        List<LeituraClimaticaEntity> todasLeituras = leituraRepo.findUltimasLeiturasGeral();
        Map<Long, LeituraClimaticaEntity> leituraMap = new HashMap<>();
        for (LeituraClimaticaEntity l : todasLeituras) {
            leituraMap.put(l.getUsina().getId(), l);
        }

        List<PrevisaoRiscoEntity> todosRiscos = riscoRepo.findUltimosRiscosGeral();
        Map<Long, PrevisaoRiscoEntity> riscoMap = new HashMap<>();
        for (PrevisaoRiscoEntity r : todosRiscos) {
            riscoMap.put(r.getUsina().getId(), r);
        }

        List<Map<String, Object>> usinasDTO = new ArrayList<>();

        for (UsinaEntity u : springDataUsinaRepository.findAll()) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", u.getNome());
            dto.put("operador", u.getOperador());
            dto.put("capacidade", u.getCapacidadeInstaladaMw());
            dto.put("latitude", u.getLatitude());
            dto.put("longitude", u.getLongitude());
            dto.put("municipio", u.getMunicipio() != null ? u.getMunicipio().getNome() : "N/D");
            dto.put("dataInicio", u.getDataInicioOperacao());

            String riscoBase = "BAIXO";
            PrevisaoRiscoEntity ultima = riscoMap.get(u.getId());
            if (ultima != null) {
                riscoBase = ultima.getNivelRisco().name();
                dto.put("risco", riscoBase);
                dto.put("fatorRisco", ultima.getFatoresJustificativa());
                dto.put("corte", ultima.getPercentualCorteEstimado() + "%");
            } else {
                dto.put("risco", "BAIXO");
                dto.put("fatorRisco", "Sem risco atÃ­pico.");
                dto.put("corte", "0%");
            }

            LeituraClimaticaEntity ultimaLeitura = leituraMap.get(u.getId());
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
                        dto.put("fatorRisco", "Rajadas previstas elevadas (>= 10 m/s), alto risco de restriÃ§Ã£o de rede.");
                    } else if ("ALTO".equals(r24h)) {
                        dto.put("fatorRisco", "Vento previsto forte (>= 7 m/s), atenÃ§Ã£o para corte programado.");
                    } else if ("MEDIO".equals(r24h)) {
                        dto.put("fatorRisco", "Vento moderado, monitoramento preventivo recomendado.");
                    } else {
                        dto.put("fatorRisco", "Sem risco atÃ­pico. Ventos dentro dos parÃ¢metros operacionais.");
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
                dto.put("fatorRisco", "Sem dados climÃ¡ticos em tempo real para avaliar o risco.");
            }

            double somaCortesUsina = cortesMap.getOrDefault(u.getNome(), 0.0);
            
            dto.put("energiaCortada", somaCortesUsina);
            dto.put("prejuizoFinanceiro", somaCortesUsina * com.curtavento.curta_vento.domain.model.PrecoEnergia.PRECO_MWH);

            usinasDTO.add(dto);
        }

        
        
        // Ordena por maior prejuizo e aplica o limite
        usinasDTO.sort((a, b) -> Double.compare((Double) b.get("prejuizoFinanceiro"), (Double) a.get("prejuizoFinanceiro")));
        if (limite < usinasDTO.size()) {
            usinasDTO = usinasDTO.subList(0, limite);
        }

        // 2. MunicÃ­pios e Totais do VAF baseados no ANO selecionado no Dropdown
        List<Map<String, Object>> municipiosDTO = new ArrayList<>();
        double somaEnergiaTotal = 0.0;
        double somaImpactoTotal = 0.0;
        double somaPerdaReceitaTotal = 0.0;

        List<com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity> impactosDb = impactoRepository.findByAnoReferencia(ano);
        for (com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity impacto : impactosDb) {
            String nome = impacto.getMunicipio().getNome();
            double mwh = impacto.getEnergiaTotalCortadaMwh() != null ? impacto.getEnergiaTotalCortadaMwh().doubleValue() : 0.0;
            double perdaReceitaReal = impacto.getPerdaReceitaEstimada() != null ? impacto.getPerdaReceitaEstimada().doubleValue() : 0.0;
            double impactoReal = impacto.getImpactoVafEstimado() != null ? impacto.getImpactoVafEstimado().doubleValue() : 0.0;
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", nome);
            dto.put("valor", impactoReal);
            dto.put("max", 50000000.0);
            dto.put("trend", "Oficial");
            
            somaEnergiaTotal += mwh;
            somaImpactoTotal += impactoReal;
            somaPerdaReceitaTotal += perdaReceitaReal;
            
            municipiosDTO.add(dto);
        }

        // InjeÃ§Ã£o dos dados analisados da subestaÃ§Ã£o BAIGT para o painel de impacto
        Map<String, Double> motivos = new HashMap<>();
        List<Object[]> motivosDb = corteRepository.findMotivosCorte(start, end);
        for (Object[] linha : motivosDb) {
            String motivoNome = (String) linha[0];
            java.math.BigDecimal val = (java.math.BigDecimal) linha[1];
            motivos.put(motivoNome != null ? motivoNome : "Não Informado", val != null ? val.doubleValue() : 0.0);
        }

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


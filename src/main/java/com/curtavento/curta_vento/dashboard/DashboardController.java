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
        
                LocalDate hoje = LocalDate.now();
                LocalDate dataInicio = LocalDate.of(ano, 1, 1);
                LocalDate dataFim = ano == hoje.getYear()
                        ? hoje
                        : (ano < hoje.getYear() ? LocalDate.of(ano, 12, 31) : dataInicio.minusDays(1));
                java.time.LocalDateTime start = dataInicio.atStartOfDay();
                java.time.LocalDateTime end = dataFim.atTime(23, 59, 59);
        List<Object[]> rankingCortes = corteRepository.findRankingCortesAno(start, end);
        Map<String, Double> cortesMap = new HashMap<>();
        for (Object[] row : rankingCortes) {
            String uName = (String) row[0];
            java.math.BigDecimal val = (java.math.BigDecimal) row[1];
            if (val != null) {
                cortesMap.put(uName, val.doubleValue());
            }
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
        List<Usina> todasUsinas = usinaService.listarTodas();

        for (Usina u : todasUsinas) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", u.getNome());
            dto.put("operador", u.getOperador());
            dto.put("capacidade", u.getCapacidadeInstaladaMw());
            dto.put("latitude", u.getLatitude());
            dto.put("longitude", u.getLongitude());
            dto.put("municipio", u.getMunicipio() != null ? u.getMunicipio().getNome() : "N/D");
            dto.put("dataInicio", u.getDataInicioOperacao());

            String riscoBase = "INDETERMINADO";
            PrevisaoRisco ultima = riscoMap.get(u.getId());
            LeituraClimatica ultimaLeitura = leituraMap.get(u.getId());
            boolean climaDisponivel = leituraAtualizada(ultimaLeitura)
                    && ultimaLeitura.getVentoPrevisao24hMs() != null;
            if (ultima != null && climaDisponivel) {
                riscoBase = ultima.getNivelRisco().name();
                dto.put("risco", riscoBase);
                dto.put("fatorRisco", ultima.getFatoresJustificativa());
                dto.put("corte", ultima.getPercentualCorteEstimado() + "%");
            } else {
                dto.put("risco", "INDETERMINADO");
                dto.put("fatorRisco", "Sem dados climáticos suficientes para confirmar o risco.");
                dto.put("corte", "N/D");
            }

            if (ultimaLeitura != null) {
                java.math.BigDecimal ventoBd = ultimaLeitura.getVelocidadeVentoMs();
                Double ventoMs = ventoBd != null ? ventoBd.doubleValue() : null;
                Double v24h = ultimaLeitura.getVentoPrevisao24hMs() != null ? ultimaLeitura.getVentoPrevisao24hMs().doubleValue() : null;
                Double v48h = ultimaLeitura.getVentoPrevisao48hMs() != null ? ultimaLeitura.getVentoPrevisao48hMs().doubleValue() : null;

                dto.put("vento", ventoMs != null ? ventoMs + " m/s" : "N/D");
                dto.put("vento24h", v24h != null ? v24h + " m/s" : "N/D");
                dto.put("vento48h", v48h != null ? v48h + " m/s" : "N/D");
                
                dto.put("riscoAtual", calcularRiscoVisual(riscoBase, ventoMs));
                dto.put("risco24h", calcularRiscoVisual(riscoBase, v24h));
                dto.put("risco48h", calcularRiscoVisual(riscoBase, v48h));

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
            }

            double somaCortesUsina = cortesMap.getOrDefault(u.getNome(), 0.0);
            
            dto.put("energiaCortada", somaCortesUsina);
            dto.put("prejuizoFinanceiro", null);
            dto.put("financeiroDisponivel", false);
            dto.put("energiaCortadaDefinicao", "Soma dos registros ONS no período selecionado, em MWh.");

            usinasDTO.add(dto);
        }

        
        
        // Ordena por maior prejuizo e aplica o limite
        usinasDTO.sort((a, b) -> Double.compare(
                (Double) b.get("energiaCortada"),
                (Double) a.get("energiaCortada")));
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
            if (totalCortado == null) {
                continue;
            }
            double mwh = totalCortado.doubleValue();
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", nome);
            dto.put("valor", null);
            dto.put("max", null);
            dto.put("trend", "Indisponível: faltam preço verificável e regra fiscal municipal.");
            
            somaEnergiaTotal += mwh;
            
            municipiosDTO.add(dto);
        }

        Map<String, Double> motivos = new HashMap<>();
        for (Object[] linha : corteRepository.findDistribuicaoMotivos(start, end)) {
            String motivo = linha[0] != null && !linha[0].toString().isBlank()
                    ? linha[0].toString()
                    : "Não informado";
            java.math.BigDecimal total = (java.math.BigDecimal) linha[1];
            if (total != null) {
                motivos.put(motivo, total.doubleValue());
            }
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("usinas", usinasDTO);
        resposta.put("municipios", municipiosDTO);
        resposta.put("energiaTotal", somaEnergiaTotal);
        resposta.put("impactoTotal", null);
        resposta.put("perdaReceitaTotal", null);
        resposta.put("taxaDesperdicio", null);
        resposta.put("motivosCorte", motivos);
        resposta.put("periodoInicio", dataInicio.toString());
        resposta.put("periodoFim", dataFim.isBefore(dataInicio) ? null : dataFim.toString());
        resposta.put("dadosDisponiveisAte", hoje.toString());
        resposta.put("periodoCompleto", ano < hoje.getYear());
        resposta.put("usinasConsideradas", todasUsinas.size());
        resposta.put("usinasAfetadas", corteRepository.countUsinasComEnergiaCortada(start, end));
        resposta.put("definicaoUsinaAfetada", "Usina com pelo menos um registro ONS de energia cortada maior que zero no período.");
        long climaComDados = leituraMap.values().stream()
                .filter(l -> l.getVentoPrevisao24hMs() != null)
                .count();
        resposta.put("coberturaClimatica", Map.of(
                "comDados", climaComDados,
                "semDados", Math.max(0, todasUsinas.size() - climaComDados),
                "total", todasUsinas.size()));
        resposta.put("financeiroDisponivel", false);
        resposta.put("motivoIndisponibilidadeFinanceira", "Não há preço de energia verificável nem regra fiscal municipal parametrizada no banco.");
        resposta.put("metodologiaFinanceira", "Não calculada: falta fonte verificável de preço por período.");
        resposta.put("metodologiaImpactoMunicipal", "Não calculado: a relação entre ICMS, VAF e município não está parametrizada com fonte oficial.");

        return ResponseEntity.ok(resposta);
    }

    private boolean leituraAtualizada(LeituraClimatica leitura) {
        if (leitura == null || leitura.getDataHora() == null) return false;
        if ("CACHE_ANTIGO".equalsIgnoreCase(leitura.getFonte())) return false;
        return !leitura.getDataHora().isBefore(java.time.LocalDateTime.now().minusHours(2));
    }

    private String calcularRiscoVisual(String riscoBase, Double vento) {
        if (vento == null) return riscoBase;
        if (vento >= 10.0) return "CRITICO";
        if (vento >= 7.0) return "ALTO";
        if (vento >= 4.0 && !"BAIXO".equals(riscoBase)) return "MEDIO";
        return "BAIXO";
    }
}

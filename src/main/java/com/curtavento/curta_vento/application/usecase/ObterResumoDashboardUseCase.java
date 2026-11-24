package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.PrecoEnergia;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObterResumoDashboardUseCase {

    private static final double VAF_ESCALA_MAXIMA = 50_000_000.0;

    private final SpringDataUsinaRepository usinaRepository;
    private final SpringDataImpactoVafRepository impactoRepository;
    private final SpringDataLeituraClimaticaRepository leituraRepository;
    private final SpringDataPrevisaoRiscoRepository riscoRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;
    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    private final SpringDataMunicipioRepository municipioRepository;

    @Autowired
    public ObterResumoDashboardUseCase(SpringDataUsinaRepository usinaRepository,
                                       SpringDataImpactoVafRepository impactoRepository,
                                       SpringDataLeituraClimaticaRepository leituraRepository,
                                       SpringDataPrevisaoRiscoRepository riscoRepository,
                                       SpringDataHistoricoCorteOnsRepository corteRepository,
                                       SpringDataHistoricoCorteOnsBlocoRepository blocoRepository,
                                       SpringDataMunicipioRepository municipioRepository) {
        this.usinaRepository = usinaRepository;
        this.impactoRepository = impactoRepository;
        this.leituraRepository = leituraRepository;
        this.riscoRepository = riscoRepository;
        this.corteRepository = corteRepository;
        this.blocoRepository = blocoRepository;
        this.municipioRepository = municipioRepository;
    }

    public Map<String, Object> executar(int ano, int limite) {
        int limiteAjustado = (limite < 1 || limite > 2000)
                ? (limite < 1 ? 1000 : 2000)
                : limite;

        LocalDateTime start = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        Map<String, Double> cortesMap = carregarCortesPorUsina(start, end);
        Map<Long, Double> desperdicioUsinaMap = carregarDesperdicioPorUsina(start, end);
        Map<Long, LeituraClimaticaEntity> leituraMap = carregarUltimasLeituras();
        Map<Long, PrevisaoRiscoEntity> riscoMap = carregarUltimosRiscos();

        List<Map<String, Object>> usinasDTO = montarUsinasDTO(cortesMap, desperdicioUsinaMap, leituraMap, riscoMap);
        usinasDTO.sort((a, b) -> Double.compare((Double) b.get("prejuizoFinanceiro"), (Double) a.get("prejuizoFinanceiro")));
        if (limiteAjustado < usinasDTO.size()) {
            usinasDTO = usinasDTO.subList(0, limiteAjustado);
        }

        List<Map<String, Object>> municipiosDTO = new ArrayList<>();
        double somaEnergiaTotal = 0.0;
        double somaImpactoTotal = 0.0;
        double somaPerdaReceitaTotal = 0.0;

        List<ImpactoVafEntity> impactosDb = impactoRepository.findByAnoReferencia(ano);
        if (impactosDb != null && !impactosDb.isEmpty()) {
            for (ImpactoVafEntity impacto : impactosDb) {
                double mwh = impacto.getEnergiaTotalCortadaMwh() != null ? impacto.getEnergiaTotalCortadaMwh().doubleValue() : 0.0;
                double perdaReceitaReal = impacto.getPerdaReceitaEstimada() != null ? impacto.getPerdaReceitaEstimada().doubleValue() : 0.0;
                double impactoReal = impacto.getImpactoVafEstimado() != null ? impacto.getImpactoVafEstimado().doubleValue() : 0.0;

                Map<String, Object> dto = new HashMap<>();
                dto.put("nome", impacto.getMunicipio().getNome());
                dto.put("valor", impactoReal);
                dto.put("max", VAF_ESCALA_MAXIMA);
                dto.put("trend", "Oficial");

                somaEnergiaTotal += mwh;
                somaImpactoTotal += impactoReal;
                somaPerdaReceitaTotal += perdaReceitaReal;

                municipiosDTO.add(dto);
            }
        }

        if (municipiosDTO.isEmpty() || somaEnergiaTotal == 0.0) {
            List<Object[]> cortesPorMunicipio = corteRepository.findImpactoPorMunicipio(start, end);
            if (cortesPorMunicipio != null && !cortesPorMunicipio.isEmpty()) {
                municipiosDTO.clear();
                somaEnergiaTotal = 0.0;
                somaImpactoTotal = 0.0;
                somaPerdaReceitaTotal = 0.0;

                Map<String, MunicipioEntity> munPorNome = new HashMap<>();
                if (municipioRepository != null) {
                    for (MunicipioEntity m : municipioRepository.findAll()) {
                        munPorNome.put(m.getNome(), m);
                    }
                }

                double maiorValor = 1.0;
                List<Map<String, Object>> temp = new ArrayList<>();
                for (Object[] row : cortesPorMunicipio) {
                    String nomeMun = (String) row[0];
                    BigDecimal mwhBd = (BigDecimal) row[2];
                    double mwh = mwhBd != null ? mwhBd.doubleValue() : 0.0;
                    double receitaPerdida = mwh * PrecoEnergia.PRECO_MWH;
                    double ipm = resolverIpm(munPorNome.get(nomeMun));
                    double impactoCalc = receitaPerdida * ipm;

                    if (impactoCalc > maiorValor) maiorValor = impactoCalc;
                    somaEnergiaTotal += mwh;
                    somaPerdaReceitaTotal += receitaPerdida;
                    somaImpactoTotal += impactoCalc;

                    Map<String, Object> dto = new HashMap<>();
                    dto.put("nome", nomeMun);
                    dto.put("valor", impactoCalc);
                    dto.put("trend", "Dinâmico");
                    temp.add(dto);
                }
                for (Map<String, Object> dto : temp) {
                    dto.put("max", maiorValor);
                    municipiosDTO.add(dto);
                }
            }
        }

        Map<String, Double> motivos = carregarMotivosCorte(start, end);
        double taxaDesperdicio = calcularTaxaDesperdicioReal(start, end);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("usinas", usinasDTO);
        resposta.put("municipios", municipiosDTO);
        resposta.put("energiaTotal", somaEnergiaTotal);
        resposta.put("impactoTotal", somaImpactoTotal);
        resposta.put("perdaReceitaTotal", somaPerdaReceitaTotal);
        resposta.put("taxaDesperdicio", taxaDesperdicio);
        resposta.put("motivosCorte", motivos);

        return resposta;
    }

    private double calcularTaxaDesperdicioReal(LocalDateTime start, LocalDateTime end) {
        List<Object[]> totais = corteRepository.findTotaisConsolidadosPeriodo(start, end);
        if (totais != null && !totais.isEmpty()) {
            Object[] row = totais.get(0);
            BigDecimal cortada = (BigDecimal) row[0];
            BigDecimal estimada = (BigDecimal) row[1];
            if (cortada != null && estimada != null && estimada.compareTo(BigDecimal.ZERO) > 0) {
                double pct = (cortada.doubleValue() / estimada.doubleValue()) * 100.0;
                return Math.round(pct * 10.0) / 10.0;
            }
        }
        return 0.0;
    }

    private Map<Long, Double> carregarDesperdicioPorUsina(LocalDateTime start, LocalDateTime end) {
        Map<Long, Double> mapa = new HashMap<>();
        List<Object[]> rows = corteRepository.findDesperdicioPorUsina(start, end);
        if (rows == null || rows.isEmpty()) {
            rows = blocoRepository.findDesperdicioPorUsina(start, end);
        }
        if (rows == null) return mapa;
        for (Object[] row : rows) {
            Long usinaId = ((Number) row[0]).longValue();
            BigDecimal cortada = (BigDecimal) row[1];
            BigDecimal estimada = (BigDecimal) row[2];
            if (cortada != null && estimada != null && estimada.compareTo(BigDecimal.ZERO) > 0) {
                double pct = (cortada.doubleValue() / estimada.doubleValue()) * 100.0;
                mapa.put(usinaId, Math.round(pct * 10.0) / 10.0);
            }
        }
        return mapa;
    }

    private Map<String, Double> carregarCortesPorUsina(LocalDateTime start, LocalDateTime end) {
        Map<String, Double> cortesMap = new HashMap<>();
        for (Object[] row : corteRepository.findRankingCortesAno(start, end)) {
            String uName = (String) row[0];
            BigDecimal val = (BigDecimal) row[1];
            cortesMap.put(uName, val != null ? val.doubleValue() : 0.0);
        }
        return cortesMap;
    }

    private Map<Long, LeituraClimaticaEntity> carregarUltimasLeituras() {
        Map<Long, LeituraClimaticaEntity> leituraMap = new HashMap<>();
        for (LeituraClimaticaEntity l : leituraRepository.findUltimasLeiturasGeral()) {
            leituraMap.put(l.getUsina().getId(), l);
        }
        return leituraMap;
    }

    private Map<Long, PrevisaoRiscoEntity> carregarUltimosRiscos() {
        Map<Long, PrevisaoRiscoEntity> riscoMap = new HashMap<>();
        for (PrevisaoRiscoEntity r : riscoRepository.findUltimosRiscosGeral()) {
            riscoMap.put(r.getUsina().getId(), r);
        }
        return riscoMap;
    }

    private List<Map<String, Object>> montarUsinasDTO(Map<String, Double> cortesMap,
                                                      Map<Long, Double> desperdicioUsinaMap,
                                                      Map<Long, LeituraClimaticaEntity> leituraMap,
                                                      Map<Long, PrevisaoRiscoEntity> riscoMap) {
        List<Map<String, Object>> usinasDTO = new ArrayList<>();

        for (UsinaEntity u : usinaRepository.findAll()) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", u.getId());
            dto.put("nome", u.getNome());
            dto.put("operador", u.getOperador());
            dto.put("capacidade", u.getCapacidadeInstaladaMw());
            dto.put("latitude", u.getLatitude());
            dto.put("longitude", u.getLongitude());
            dto.put("municipio", u.getMunicipio() != null ? u.getMunicipio().getNome() : "N/D");
            dto.put("dataInicio", u.getDataInicioOperacao());
            dto.put("subestacao", u.getSubestacaoConexao() != null ? u.getSubestacaoConexao() : "N/D");
            dto.put("taxaDesperdicio", desperdicioUsinaMap.getOrDefault(u.getId(), 0.0));

            String riscoBase = "BAIXO";
            PrevisaoRiscoEntity ultima = riscoMap.get(u.getId());
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

            preencherDadosClimaticos(dto, leituraMap.get(u.getId()), ultima, riscoBase);

            double somaCortesUsina = cortesMap.getOrDefault(u.getNome(), 0.0);
            double precoMwh = u.getPrecoPpaMwh() != null ? u.getPrecoPpaMwh().doubleValue() : PrecoEnergia.PRECO_MWH;
            dto.put("energiaCortada", somaCortesUsina);
            dto.put("prejuizoFinanceiro", somaCortesUsina * precoMwh);

            usinasDTO.add(dto);
        }

        return usinasDTO;
    }

    private void preencherDadosClimaticos(Map<String, Object> dto,
                                          LeituraClimaticaEntity ultimaLeitura,
                                          PrevisaoRiscoEntity ultimaPrevisao,
                                          String riscoBase) {
        if (ultimaLeitura == null) {
            dto.put("vento", "N/D");
            dto.put("vento24h", "N/D");
            dto.put("vento48h", "N/D");
            dto.put("radiacaoSolar", null);
            dto.put("riscoAtual", "N/D");
            dto.put("risco24h", "N/D");
            dto.put("risco48h", "N/D");
            dto.put("janelaManutencao", "N/D");
            dto.put("fonteClima", "N/D");
            dto.put("dataClima", "N/D");
            dto.put("fatorRisco", "Sem dados climáticos em tempo real para avaliar o risco.");
            return;
        }

        Double ventoMs = ultimaLeitura.getVelocidadeVentoMs() != null ? ultimaLeitura.getVelocidadeVentoMs().doubleValue() : null;
        Double v24h = ultimaLeitura.getVentoPrevisao24hMs() != null ? ultimaLeitura.getVentoPrevisao24hMs().doubleValue() : null;
        Double v48h = ultimaLeitura.getVentoPrevisao48hMs() != null ? ultimaLeitura.getVentoPrevisao48hMs().doubleValue() : null;

        dto.put("vento", ventoMs != null ? ventoMs + " m/s" : "N/D");
        dto.put("vento24h", v24h != null ? v24h + " m/s" : "N/D");
        dto.put("vento48h", v48h != null ? v48h + " m/s" : "N/D");
        dto.put("radiacaoSolar", ultimaLeitura.getRadiacaoSolarWm2() != null ? ultimaLeitura.getRadiacaoSolarWm2().doubleValue() : null);

        String rAtual = calcularRiscoVisual(riscoBase, ventoMs);
        String r24h = calcularRiscoVisual(riscoBase, v24h);
        String r48h = calcularRiscoVisual(riscoBase, v48h);

        dto.put("riscoAtual", rAtual);
        dto.put("risco24h", r24h);
        dto.put("risco48h", r48h);
        dto.put("risco", r24h);

        if (ultimaPrevisao == null || ultimaPrevisao.getFatoresJustificativa() == null) {
            dto.put("fatorRisco", descreverFatorRisco(r24h));
        }

        dto.put("janelaManutencao", calcularJanelaManutencao(ventoMs));
        dto.put("fonteClima", ultimaLeitura.getFonte() != null ? ultimaLeitura.getFonte() : "Open-Meteo");
        dto.put("dataClima", ultimaLeitura.getDataHora() != null ? ultimaLeitura.getDataHora().toString() : "N/D");
    }

    private Map<String, Double> carregarMotivosCorte(LocalDateTime start, LocalDateTime end) {
        Map<String, Double> motivos = new HashMap<>();
        List<Object[]> linhas = corteRepository.findMotivosCorte(start, end);
        if (linhas == null || linhas.isEmpty()) {
            linhas = blocoRepository.findResumoPorMotivo(start, end);
        }
        if (linhas == null) return motivos;

        for (Object[] linha : linhas) {
            String motivoNome = (String) linha[0];
            BigDecimal val = (BigDecimal) linha[1];
            motivos.put(motivoNome != null ? motivoNome : "Não Informado", val != null ? val.doubleValue() : 0.0);
        }
        return motivos;
    }

    private String descreverFatorRisco(String risco) {
        return switch (risco) {
            case "CRITICO" -> "Rajadas previstas elevadas (>= 10 m/s), alto risco de restrição de rede.";
            case "ALTO" -> "Vento previsto forte (>= 7 m/s), atenção para corte programado.";
            case "MEDIO" -> "Vento moderado, monitoramento preventivo recomendado.";
            default -> "Sem risco atípico. Ventos dentro dos parâmetros operacionais.";
        };
    }

    private String calcularJanelaManutencao(Double ventoMs) {
        if (ventoMs == null) return "N/D";
        if (ventoMs <= 11.1) return "LIBERADO";
        if (ventoMs <= 15.0) return "ALERTA";
        return "BLOQUEADO";
    }

    private double resolverIpm(MunicipioEntity mun) {
        if (mun == null) return 0.02;
        BigDecimal bruto = mun.getIpm() != null ? mun.getIpm() : mun.getVafAnoBase();
        if (bruto == null) return 0.02;
        double v = bruto.doubleValue();
        return v > 1.0 ? v / 100.0 : v;
    }

    private String calcularRiscoVisual(String riscoBase, Double vento) {
        if (vento == null) return riscoBase;
        if (vento >= 10.0) return "CRITICO";
        if (vento >= 7.0) return "ALTO";
        if (vento >= 4.0 && !"BAIXO".equals(riscoBase)) return "MEDIO";
        return "BAIXO";
    }
}

package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.NivelRisco;
import com.curtavento.curta_vento.infrastructure.client.MlPrevisaoClient;
import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient;
import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient.PontoHorarioClimatico;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PrevisaoCurvaRisco48hUseCase {

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final SpringDataUsinaRepository usinaRepository;
    private final SpringDataLeituraClimaticaRepository leituraRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;
    private final OpenMeteoClient openMeteoClient;
    private final MlPrevisaoClient mlPrevisaoClient;
    private final Clock clock;

    @Autowired
    public PrevisaoCurvaRisco48hUseCase(SpringDataUsinaRepository usinaRepository,
                                        SpringDataLeituraClimaticaRepository leituraRepository,
                                        SpringDataHistoricoCorteOnsRepository corteRepository,
                                        OpenMeteoClient openMeteoClient,
                                        MlPrevisaoClient mlPrevisaoClient) {
        this(usinaRepository, leituraRepository, corteRepository, openMeteoClient, mlPrevisaoClient, Clock.systemDefaultZone());
    }

    public PrevisaoCurvaRisco48hUseCase(SpringDataUsinaRepository usinaRepository,
                                        SpringDataLeituraClimaticaRepository leituraRepository,
                                        SpringDataHistoricoCorteOnsRepository corteRepository,
                                        OpenMeteoClient openMeteoClient,
                                        Clock clock) {
        this(usinaRepository, leituraRepository, corteRepository, openMeteoClient, null, clock);
    }

    public PrevisaoCurvaRisco48hUseCase(SpringDataUsinaRepository usinaRepository,
                                        SpringDataLeituraClimaticaRepository leituraRepository,
                                        SpringDataHistoricoCorteOnsRepository corteRepository,
                                        OpenMeteoClient openMeteoClient,
                                        MlPrevisaoClient mlPrevisaoClient,
                                        Clock clock) {
        this.usinaRepository = usinaRepository;
        this.leituraRepository = leituraRepository;
        this.corteRepository = corteRepository;
        this.openMeteoClient = openMeteoClient;
        this.mlPrevisaoClient = mlPrevisaoClient;
        this.clock = clock;
    }

    public Map<String, Object> executar(Long usinaId) {
        return executar(usinaId, "AUTO");
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> executar(Long usinaId, String arquiteturaMl) {
        UsinaEntity usina = usinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        LocalDateTime agora = LocalDateTime.now(clock).withMinute(0).withSecond(0).withNano(0);
        List<HistoricoCorteOnsEntity> cortesRecentes = corteRepository.findByUsinaIdAndDataCorteBetween(
                usinaId, agora.minusDays(15), agora);
        long diasComCorteRecente = cortesRecentes.stream()
                .map(c -> c.getDataCorte().toLocalDate())
                .distinct()
                .count();

        String fontePrevisao = "OPEN_METEO_HORARIO";
        List<PontoHorarioClimatico> pontos;
        try {
            pontos = openMeteoClient.fetchHourly48hForecast(usina);
            if (pontos == null || pontos.isEmpty()) {
                fontePrevisao = "FALLBACK_HISTORICO";
                pontos = gerarPontosFallback(usinaId, agora);
            }
        } catch (Exception ex) {
            fontePrevisao = "FALLBACK_HISTORICO";
            pontos = gerarPontosFallback(usinaId, agora);
        }

        double capacidadeMw = usina.getCapacidadeInstaladaMw() != null ? usina.getCapacidadeInstaladaMw().doubleValue() : 30.0;
        List<Map<String, Object>> curva = new ArrayList<>(pontos.size());
        List<Map<String, Object>> melhoresJanelasOm = new ArrayList<>();

        int horasAltoCritico = 0;
        int horasOtimaOm = 0;
        double economiaTotalMwh = 0.0;

        for (PontoHorarioClimatico p : pontos) {
            int score = calcularScoreHorario(p, diasComCorteRecente);
            NivelRisco nivel = classificarNivel(score);
            if (nivel == NivelRisco.ALTO || nivel == NivelRisco.CRITICO) {
                horasAltoCritico++;
            }

            double vento = p.velocidadeVento80mMs() != null ? p.velocidadeVento80mMs().doubleValue() : 0.0;
            double rajada = p.rajada10mMs() != null ? p.rajada10mMs().doubleValue() : vento;
            double radiacao = p.radiacaoSolarWm2() != null ? p.radiacaoSolarWm2().doubleValue() : 0.0;

            String categoriaOm = classificarJanelaOm(vento);
            String descricaoOm = descreverJanelaOm(categoriaOm);
            boolean janelaOtimaOm = score >= 55 && vento <= 11.1;

            double mwhEconomizavel = 0.0;
            if (janelaOtimaOm) {
                horasOtimaOm++;
                double fatorCorte = score >= 80 ? 0.80 : 0.45;
                mwhEconomizavel = arredondar(capacidadeMw * fatorCorte);
                economiaTotalMwh += mwhEconomizavel;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("dataHora", p.dataHora().toString());
            item.put("rotuloHora", p.dataHora().format(FMT_HORA));
            item.put("vento80mMs", arredondar(vento));
            item.put("rajadaMs", arredondar(rajada));
            item.put("radiacaoSolarWm2", arredondar(radiacao));
            item.put("scoreCurtailment", score);
            item.put("nivelRisco", nivel.name());
            item.put("categoriaOm", categoriaOm);
            item.put("descricaoOm", descricaoOm);
            item.put("janelaOtimaOm", janelaOtimaOm);
            item.put("mwhEconomizavel", mwhEconomizavel);
            curva.add(item);

            if (janelaOtimaOm) {
                melhoresJanelasOm.add(item);
            }
        }

        melhoresJanelasOm.sort((a, b) -> Integer.compare((Integer) b.get("scoreCurtailment"), (Integer) a.get("scoreCurtailment")));
        if (melhoresJanelasOm.size() > 8) {
            melhoresJanelasOm = melhoresJanelasOm.subList(0, 8);
        }

        Map<String, Object> usinaDto = new HashMap<>();
        usinaDto.put("id", usina.getId());
        usinaDto.put("nome", usina.getNome());
        usinaDto.put("operador", usina.getOperador());
        usinaDto.put("municipio", usina.getMunicipio() != null ? usina.getMunicipio().getNome() : "N/D");
        usinaDto.put("subestacao", usina.getSubestacaoConexao() != null ? usina.getSubestacaoConexao() : "N/D");
        usinaDto.put("capacidadeMw", capacidadeMw);

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("horasAltoOuCritico", horasAltoCritico);
        resumo.put("horasOtimaOm", horasOtimaOm);
        resumo.put("economiaPotencialMwh", arredondar(economiaTotalMwh));
        resumo.put("diasComCorteRecente", diasComCorteRecente);

        Map<String, Object> modeloPreditivo = new HashMap<>();
        modeloPreditivo.put("ativo", false);
        modeloPreditivo.put("origem", "FALLBACK_HEURISTICO_JAVA");

        if (mlPrevisaoClient != null) {
            Optional<Map<String, Object>> mlOpt = mlPrevisaoClient.preverCurtailment(
                    usina, pontos, diasComCorteRecente, arquiteturaMl);
            if (mlOpt.isPresent()) {
                Map<String, Object> mlResp = mlOpt.get();
                modeloPreditivo.put("ativo", true);
                modeloPreditivo.put("origem", "PYTHON_FASTAPI_PYTORCH");
                modeloPreditivo.put("modeloAtivo", mlResp.get("modeloAtivo"));
                modeloPreditivo.put("janela6h", mlResp.get("janela6h"));
                modeloPreditivo.put("janela12h", mlResp.get("janela12h"));

                Object curvaMlObj = mlResp.get("curvaHorariaMl");
                if (curvaMlObj instanceof List<?> mlLista) {
                    Map<String, Map<String, Object>> porHora = new HashMap<>();
                    for (Object elem : mlLista) {
                        if (elem instanceof Map<?, ?> m && m.get("dataHora") != null) {
                            porHora.put(m.get("dataHora").toString(), (Map<String, Object>) m);
                        }
                    }
                    for (Map<String, Object> item : curva) {
                        Map<String, Object> mlPonto = porHora.get(String.valueOf(item.get("dataHora")));
                        if (mlPonto != null) {
                            item.put("probabilidadeMlPct", mlPonto.get("probabilidadeCorte"));
                            item.put("mwhEstimadoCorteMl", mlPonto.get("mwhEstimadoCorte"));
                            item.put("mwhEstimadoCorteMlP10", mlPonto.get("mwhEstimadoP10"));
                            item.put("mwhEstimadoCorteMlP90", mlPonto.get("mwhEstimadoP90"));
                        }
                    }
                }
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("usina", usinaDto);
        resultado.put("fontePrevisao", fontePrevisao);
        resultado.put("modeloPreditivo", modeloPreditivo);
        resultado.put("resumo", resumo);
        resultado.put("curvaHoraria", curva);
        resultado.put("melhoresJanelasOm", melhoresJanelasOm);
        return resultado;
    }

    int calcularScoreHorario(PontoHorarioClimatico p, long diasComCorteRecente) {
        int score = 0;
        double vento = p.velocidadeVento80mMs() != null ? p.velocidadeVento80mMs().doubleValue() : 0.0;
        double radiacao = p.radiacaoSolarWm2() != null ? p.radiacaoSolarWm2().doubleValue() : 0.0;
        LocalDateTime dt = p.dataHora();
        int hora = dt.getHour();

        // 1. Fator Eólico (0 a 35 pts)
        if (vento >= 10.0) score += 35;
        else if (vento >= 7.0) score += 22;
        else if (vento >= 5.0) score += 10;

        // 2. Fator Solar / Curva do Pato (0 a 30 pts)
        if (radiacao >= 600.0) score += 30;
        else if (radiacao >= 350.0) score += 20;
        else if (radiacao >= 150.0) score += 8;

        // 3. Fator Carga do SIN (-10 a +20 pts)
        DayOfWeek diaSemana = dt.getDayOfWeek();
        if (diaSemana == DayOfWeek.SUNDAY) score += 15;
        else if (diaSemana == DayOfWeek.SATURDAY) score += 10;

        if (hora >= 10 && hora <= 15) score += 5;
        else if (hora >= 18 && hora <= 21) score -= 10;

        // 4. Fator Sazonal NE (Jul-Nov) e Tendência dos últimos 15 dias (0 a 15 pts)
        int mes = dt.getMonthValue();
        score += (mes >= 7 && mes <= 11) ? 10 : 3;
        if (diasComCorteRecente > 3) score += 5;

        return Math.max(0, Math.min(100, score));
    }

    private NivelRisco classificarNivel(int score) {
        if (score <= 29) return NivelRisco.BAIXO;
        if (score <= 59) return NivelRisco.MEDIO;
        if (score <= 79) return NivelRisco.ALTO;
        return NivelRisco.CRITICO;
    }

    private String classificarJanelaOm(double ventoMs) {
        if (ventoMs <= 11.1) return "ACESSO_CORDA_PAS";
        if (ventoMs <= 12.5) return "PLATAFORMA_ICAMENTO";
        if (ventoMs <= 15.0) return "TORRE_INTERNA";
        return "BLOQUEADO";
    }

    private String descreverJanelaOm(String categoria) {
        return switch (categoria) {
            case "ACESSO_CORDA_PAS" -> "Liberado: Acesso por Corda / Pás (NR-35 <= 11,1 m/s)";
            case "PLATAFORMA_ICAMENTO" -> "Liberado: Plataforma Elevatória (<= 12,5 m/s)";
            case "TORRE_INTERNA" -> "Restrito: Apenas Subida Interna Torre (GWO <= 15,0 m/s)";
            default -> "Bloqueado: Vento acima do limite seguro (> 15,0 m/s)";
        };
    }

    private List<PontoHorarioClimatico> gerarPontosFallback(Long usinaId, LocalDateTime inicio) {
        LeituraClimaticaEntity ultima = leituraRepository.findFirstByUsinaIdOrderByDataHoraDesc(usinaId);
        double ventoBase = ultima != null && ultima.getVelocidadeVentoMs() != null
                ? ultima.getVelocidadeVentoMs().doubleValue() : 7.5;
        double tempBase = ultima != null && ultima.getTemperaturaC() != null
                ? ultima.getTemperaturaC().doubleValue() : 27.0;

        List<PontoHorarioClimatico> lista = new ArrayList<>(48);
        for (int i = 0; i < 48; i++) {
            LocalDateTime dt = inicio.plusHours(i);
            int h = dt.getHour();
            double radSimulada = (h >= 7 && h <= 17) ? Math.sin((h - 6) * Math.PI / 12.0) * 750.0 : 0.0;
            lista.add(new PontoHorarioClimatico(
                    dt,
                    BigDecimal.valueOf(ventoBase),
                    BigDecimal.valueOf(ventoBase * 1.15),
                    BigDecimal.valueOf(Math.max(0.0, radSimulada)),
                    BigDecimal.valueOf(tempBase)
            ));
        }
        return lista;
    }

    private double arredondar(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

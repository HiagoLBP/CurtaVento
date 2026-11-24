package com.curtavento.curta_vento.infrastructure.client;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Cliente para a API pública NASA POWER (Prediction of Worldwide Energy Resources).
 * Fornece reanálise atmosférica horária (vento a 50m extrapolado para 80m, temperatura e radiação)
 * conforme previsto na metodologia do projeto CurtaVento (Pessanha; Almeida; Melo, 2020).
 */
@Component
public class NasaPowerClient {

    private static final Logger logger = LoggerFactory.getLogger(NasaPowerClient.class);
    private static final DateTimeFormatter NASA_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final double SHEAR_EXPONENT_BA = 0.14;
    private static final double FATOR_EXTRAPOLACAO_50M_PARA_80M = Math.pow(80.0 / 50.0, SHEAR_EXPONENT_BA);

    private final RestTemplate restTemplate;

    public NasaPowerClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(6));
        factory.setReadTimeout(Duration.ofSeconds(12));
        this.restTemplate = new RestTemplate(factory);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> buscarReanaliseHoraria(UsinaEntity usina, int dias) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            return Map.of("disponivel", false, "motivo", "Usina sem coordenadas geográficas.");
        }

        int diasAjustados = Math.max(1, Math.min(14, dias));
        // NASA POWER possui defasagem de ~2 dias na publicação consolidada
        LocalDate fim = LocalDate.now().minusDays(2);
        LocalDate inicio = fim.minusDays(diasAjustados - 1L);

        String url = String.format(Locale.US,
                "https://power.larc.nasa.gov/api/temporal/hourly/point" +
                        "?parameters=WS50M,T2M,ALLSKY_SFC_SW_DWN&community=RE" +
                        "&longitude=%s&latitude=%s&start=%s&end=%s&format=JSON",
                usina.getLongitude(), usina.getLatitude(),
                inicio.format(NASA_DATE_FMT), fim.format(NASA_DATE_FMT));

        try {
            Map<String, Object> body = restTemplate.getForObject(url, Map.class);
            Map<String, Object> properties = body != null ? (Map<String, Object>) body.get("properties") : null;
            Map<String, Object> parameter = properties != null ? (Map<String, Object>) properties.get("parameter") : null;
            if (parameter == null) {
                return Map.of("disponivel", false, "motivo", "Resposta vazia da API NASA POWER.");
            }

            Map<String, Object> ws50 = (Map<String, Object>) parameter.getOrDefault("WS50M", Map.of());
            Map<String, Object> t2m = (Map<String, Object>) parameter.getOrDefault("T2M", Map.of());
            Map<String, Object> rad = (Map<String, Object>) parameter.getOrDefault("ALLSKY_SFC_SW_DWN", Map.of());

            List<Map<String, Object>> serie = new ArrayList<>();
            double somaVento80 = 0.0;
            int countVento = 0;

            for (String chaveHora : new TreeSet<>(ws50.keySet())) {
                double v50 = toDouble(ws50.get(chaveHora));
                if (v50 < 0 || v50 > 900) continue; // -999 é sentinela de dado ausente na NASA POWER
                double v80 = v50 * FATOR_EXTRAPOLACAO_50M_PARA_80M;
                double temp = toDouble(t2m.get(chaveHora));
                double radiacao = Math.max(0.0, toDouble(rad.get(chaveHora)));

                somaVento80 += v80;
                countVento++;

                Map<String, Object> ponto = new HashMap<>();
                ponto.put("chaveHora", chaveHora);
                ponto.put("vento50mMs", arredondar(v50));
                ponto.put("vento80mEstimadoMs", arredondar(v80));
                ponto.put("temperaturaC", temp > -900 ? arredondar(temp) : null);
                ponto.put("radiacaoSolarWm2", radiacao < 9000 ? arredondar(radiacao) : 0.0);
                serie.add(ponto);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("disponivel", true);
            resp.put("fonte", "NASA_POWER_REANALISE");
            resp.put("usinaId", usina.getId());
            resp.put("usina", usina.getNome());
            resp.put("inicio", inicio.toString());
            resp.put("fim", fim.toString());
            resp.put("totalPontos", serie.size());
            resp.put("vento80mMedioMs", countVento > 0 ? arredondar(somaVento80 / countVento) : 0.0);
            resp.put("serie", serie);
            return resp;
        } catch (Exception ex) {
            logger.warn("Falha ao consultar NASA POWER para usina {}: {}", usina.getId(), ex.getMessage());
            return Map.of("disponivel", false, "fonte", "NASA_POWER_REANALISE", "motivo", ex.getMessage());
        }
    }

    private double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        try {
            return val != null ? Double.parseDouble(val.toString()) : -999.0;
        } catch (NumberFormatException e) {
            return -999.0;
        }
    }

    private double arredondar(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

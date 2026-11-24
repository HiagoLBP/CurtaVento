package com.curtavento.curta_vento.infrastructure.client;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenMeteoClient {

    public record PontoHorarioClimatico(
            LocalDateTime dataHora,
            BigDecimal velocidadeVento80mMs,
            BigDecimal rajada10mMs,
            BigDecimal radiacaoSolarWm2,
            BigDecimal temperaturaC
    ) {}

    private final RestTemplate restTemplate;

    public OpenMeteoClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restTemplate = new RestTemplate(factory);
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Map<String, BigDecimal> fetchOpenMeteoForecast(UsinaEntity usina) {
        Map<?, ?> response = requisitarPrevisao(usina);
        Map<?, ?> current = (Map<?, ?>) response.get("current");
        Map<?, ?> hourly = (Map<?, ?>) response.get("hourly");

        BigDecimal ventoAtual = toBigDecimal(current.get("wind_speed_80m"), BigDecimal.ZERO);
        BigDecimal tempAtual = toBigDecimal(current.get("temperature_2m"), BigDecimal.ZERO);
        BigDecimal direcaoVento = toBigDecimal(current.get("wind_direction_80m"), null);
        BigDecimal radiacaoAtual = toBigDecimal(current.get("shortwave_radiation"), BigDecimal.ZERO);
        BigDecimal rajadaAtual = toBigDecimal(current.get("wind_gusts_10m"), BigDecimal.ZERO);

        List<?> windList = (List<?>) hourly.get("wind_speed_80m");
        BigDecimal max24h = BigDecimal.ZERO;
        BigDecimal max48h = BigDecimal.ZERO;

        if (windList != null && windList.size() >= 48) {
            for (int i = 0; i < 24; i++) {
                BigDecimal v = toBigDecimal(windList.get(i), null);
                if (v != null && v.compareTo(max24h) > 0) max24h = v;
            }
            for (int i = 24; i < 48; i++) {
                BigDecimal v = toBigDecimal(windList.get(i), null);
                if (v != null && v.compareTo(max48h) > 0) max48h = v;
            }
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("vento_atual", ventoAtual);
        result.put("vento_24h", max24h);
        result.put("vento_48h", max48h);
        result.put("temperatura", tempAtual);
        result.put("direcao_vento", direcaoVento);
        result.put("radiacao_solar", radiacaoAtual);
        result.put("rajada_atual", rajadaAtual);
        return result;
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<PontoHorarioClimatico> fetchHourly48hForecast(UsinaEntity usina) {
        Map<?, ?> response = requisitarPrevisao(usina);
        Map<?, ?> hourly = (Map<?, ?>) response.get("hourly");

        List<?> times = (List<?>) hourly.get("time");
        List<?> winds = (List<?>) hourly.get("wind_speed_80m");
        List<?> gusts = (List<?>) hourly.get("wind_gusts_10m");
        List<?> radiations = (List<?>) hourly.get("shortwave_radiation");
        List<?> temps = (List<?>) hourly.get("temperature_2m");

        if (times == null || winds == null) {
            return List.of();
        }

        int total = Math.min(48, Math.min(times.size(), winds.size()));
        List<PontoHorarioClimatico> pontos = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            LocalDateTime dt = LocalDateTime.parse(times.get(i).toString());
            BigDecimal wind = toBigDecimal(winds.get(i), BigDecimal.ZERO);
            BigDecimal gust = gusts != null && i < gusts.size() ? toBigDecimal(gusts.get(i), BigDecimal.ZERO) : BigDecimal.ZERO;
            BigDecimal rad = radiations != null && i < radiations.size() ? toBigDecimal(radiations.get(i), BigDecimal.ZERO) : BigDecimal.ZERO;
            BigDecimal temp = temps != null && i < temps.size() ? toBigDecimal(temps.get(i), BigDecimal.ZERO) : BigDecimal.ZERO;
            pontos.add(new PontoHorarioClimatico(dt, wind, gust, rad, temp));
        }
        return pontos;
    }

    private Map<?, ?> requisitarPrevisao(UsinaEntity usina) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            throw new RuntimeException("Usina sem coordenadas para buscar no Open-Meteo.");
        }

        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&timezone=America/Bahia" +
                "&current=temperature_2m,wind_speed_80m,wind_direction_80m,wind_gusts_10m,shortwave_radiation" +
                "&hourly=wind_speed_80m,wind_gusts_10m,shortwave_radiation,temperature_2m&wind_speed_unit=ms&forecast_days=2",
                usina.getLatitude(), usina.getLongitude());

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("hourly") || !response.containsKey("current")) {
            throw new RuntimeException("Dados do Open-Meteo inválidos ou lista horária vazia.");
        }
        return response;
    }

    private BigDecimal toBigDecimal(Object value, BigDecimal fallback) {
        return value != null ? new BigDecimal(value.toString()) : fallback;
    }
}

package com.curtavento.curta_vento.infrastructure.client;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenMeteoClient {

    private final RestTemplate restTemplate;

    public OpenMeteoClient() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Map<String, BigDecimal> fetchOpenMeteoForecast(UsinaEntity usina) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            throw new RuntimeException("UsinaEntity sem coordenadas para buscar no Open-Meteo.");
        }

        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m,wind_speed_80m,wind_direction_80m&hourly=wind_speed_80m&wind_speed_unit=ms&forecast_days=2",
                usina.getLatitude(), usina.getLongitude());

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("hourly") && response.containsKey("current")) {
            Map<?, ?> current = (Map<?, ?>) response.get("current");
            Map<?, ?> hourly = (Map<?, ?>) response.get("hourly");
            
            Object currentWindObj = current.get("wind_speed_80m");
            Object currentTempObj = current.get("temperature_2m");
            Object currentDirObj = current.get("wind_direction_80m");
            
            BigDecimal ventoAtual = currentWindObj != null ? new BigDecimal(currentWindObj.toString()) : BigDecimal.ZERO;
            BigDecimal tempAtual = currentTempObj != null ? new BigDecimal(currentTempObj.toString()) : BigDecimal.ZERO;
            BigDecimal direcaoVento = currentDirObj != null ? new BigDecimal(currentDirObj.toString()) : null;
            
            List<?> windList = (List<?>) hourly.get("wind_speed_80m");
            
            BigDecimal max24h = BigDecimal.ZERO;
            BigDecimal max48h = BigDecimal.ZERO;
            
            if (windList != null && windList.size() >= 48) {
                // Primeira 24 horas
                for (int i = 0; i < 24; i++) {
                    Object wObj = windList.get(i);
                    if (wObj != null) {
                        BigDecimal v = new BigDecimal(wObj.toString());
                        if (v.compareTo(max24h) > 0) max24h = v;
                    }
                }
                // PrÃ³ximas 24 horas (24 a 47)
                for (int i = 24; i < 48; i++) {
                    Object wObj = windList.get(i);
                    if (wObj != null) {
                        BigDecimal v = new BigDecimal(wObj.toString());
                        if (v.compareTo(max48h) > 0) max48h = v;
                    }
                }
            }
            
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("vento_atual", ventoAtual);
            result.put("vento_24h", max24h);
            result.put("vento_48h", max48h);
            result.put("temperatura", tempAtual);
            result.put("direcao_vento", direcaoVento);
            return result;
        }
        throw new RuntimeException("Dados do Open-Meteo invÃ¡lidos ou lista horÃ¡ria vazia.");
    }
}


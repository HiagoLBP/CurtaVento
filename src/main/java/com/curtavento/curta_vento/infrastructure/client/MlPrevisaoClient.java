package com.curtavento.curta_vento.infrastructure.client;

import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient.PontoHorarioClimatico;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

/**
 * Cliente HTTP para o microsserviço Python FastAPI (PyTorch + Time2Vec).
 * Opera com timeouts curtos e retorno Optional para garantir fallback resiliente
 * ao motor heurístico Java caso o serviço ML esteja indisponível.
 */
@Component
public class MlPrevisaoClient {

    private static final Logger logger = LoggerFactory.getLogger(MlPrevisaoClient.class);

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;

    public MlPrevisaoClient(@Value("${curtavento.ml.url:http://localhost:8001}") String mlServiceUrl) {
        this.mlServiceUrl = mlServiceUrl != null ? mlServiceUrl.replaceAll("/+$", "") : "http://localhost:8001";
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(1500));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restTemplate = new RestTemplate(factory);
    }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> preverCurtailment(UsinaEntity usina,
                                                           List<PontoHorarioClimatico> pontos,
                                                           long diasComCorteRecente,
                                                           String arquitetura) {
        if (pontos == null || pontos.isEmpty()) {
            return Optional.empty();
        }
        try {
            double capacidadeMw = usina.getCapacidadeInstaladaMw() != null
                    ? usina.getCapacidadeInstaladaMw().doubleValue() : 30.0;

            List<Map<String, Object>> curvaEntrada = new ArrayList<>(pontos.size());
            for (PontoHorarioClimatico p : pontos) {
                Map<String, Object> item = new HashMap<>();
                item.put("dataHora", p.dataHora().toString());
                item.put("vento80mMs", p.velocidadeVento80mMs() != null ? p.velocidadeVento80mMs().doubleValue() : 0.0);
                item.put("radiacaoSolarWm2", p.radiacaoSolarWm2() != null ? p.radiacaoSolarWm2().doubleValue() : 0.0);
                item.put("capacidadeMw", capacidadeMw);
                item.put("diasComCorteRecente", diasComCorteRecente);
                curvaEntrada.add(item);
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("usinaId", usina.getId());
            payload.put("usinaNome", usina.getNome());
            payload.put("capacidadeMw", capacidadeMw);
            payload.put("diasComCorteRecente", diasComCorteRecente);
            payload.put("arquitetura", (arquitetura == null || arquitetura.isBlank()) ? "AUTO" : arquitetura);
            payload.put("curvaEntrada", curvaEntrada);

            Map<String, Object> resp = restTemplate.postForObject(
                    mlServiceUrl + "/predict/curtailment", payload, Map.class);
            return Optional.ofNullable(resp);
        } catch (Exception ex) {
            logger.debug("Microsserviço ML indisponível em {}: {}. Usando fallback heurístico Java.",
                    mlServiceUrl, ex.getMessage());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> obterBenchmarkModelos() {
        try {
            Map<String, Object> resp = restTemplate.getForObject(
                    mlServiceUrl + "/models/benchmark", Map.class);
            return Optional.ofNullable(resp);
        } catch (Exception ex) {
            logger.debug("Benchmark ML indisponível em {}: {}", mlServiceUrl, ex.getMessage());
            return Optional.empty();
        }
    }
}

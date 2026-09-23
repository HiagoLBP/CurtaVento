package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaService;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IngestaoDadosClimaticosJob {

    private static final Logger logger = LoggerFactory.getLogger(IngestaoDadosClimaticosJob.class);

    private final LeituraClimaticaService leituraService;
    private final UsinaRepository usinaRepository;
    private final RestTemplate restTemplate;

    public IngestaoDadosClimaticosJob(LeituraClimaticaService leituraService, UsinaRepository usinaRepository) {
        this.leituraService = leituraService;
        this.usinaRepository = usinaRepository;
        this.restTemplate = new RestTemplate();
    }

    // Para testes: roda 4 segundos após iniciar a aplicação, e depois 1x por dia.
    @Scheduled(initialDelay = 4000, fixedDelay = 86400000)
    public void buscarPrevisaoClimatica() {
        logger.info("JOB ATIVO: Buscando PREVISÃO climática (próximas 48h) no Open-Meteo...");

        List<Usina> usinas = usinaRepository.findAll();
        for (Usina usina : usinas) {
            BigDecimal ventoMaximo = null;
            BigDecimal tempMaximo = null;
            String fonte = null;

            try {
                Map<String, BigDecimal> dadosOpenMeteo = fetchOpenMeteoForecast(usina);
                ventoMaximo = dadosOpenMeteo.get("vento");
                tempMaximo = dadosOpenMeteo.get("temperatura");
                fonte = "Open-Meteo (Previsão 48h - Pico)";
            } catch (Exception ex) {
                logger.error("Falha no Open-Meteo para a usina ID {}. Buscando no cache. Erro: {}", usina.getId(), ex.getMessage());
            }

            if (ventoMaximo == null) {
                LeituraClimatica ultima = leituraService.buscarUltimaLeitura(usina.getId());
                if (ultima != null) {
                    ventoMaximo = ultima.getVelocidadeVentoMs();
                    tempMaximo = ultima.getTemperaturaC();
                    fonte = "CACHE_ANTIGO";
                } else {
                    logger.error("Nenhuma leitura anterior encontrada para usina ID {}. Usando valores zerados.", usina.getId());
                    ventoMaximo = BigDecimal.ZERO;
                    tempMaximo = BigDecimal.ZERO;
                    fonte = "DEFAULT_ZERADO";
                }
            }

            LeituraClimatica leitura = new LeituraClimatica();
            leitura.setUsina(usina);
            leitura.setVelocidadeVentoMs(ventoMaximo);
            leitura.setTemperaturaC(tempMaximo != null ? tempMaximo : BigDecimal.ZERO);
            leitura.setFonte(fonte);
            leitura.setDataHora(LocalDateTime.now());

            leituraService.salvar(leitura);
            logger.info("Previsão atualizada: Usina {} - Pico Vento 48h: {} m/s - Fonte: {}", usina.getId(), ventoMaximo, fonte);
        }
    }

    private Map<String, BigDecimal> fetchOpenMeteoForecast(Usina usina) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            throw new RuntimeException("Usina sem coordenadas para buscar no Open-Meteo.");
        }

        // Busca a previsão HORA A HORA (hourly) para os próximos 2 dias, vento a 80m
        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&hourly=temperature_2m,wind_speed_80m&wind_speed_unit=ms&forecast_days=2",
                usina.getLatitude(), usina.getLongitude());

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("hourly")) {
            Map<?, ?> hourly = (Map<?, ?>) response.get("hourly");
            
            List<?> windList = (List<?>) hourly.get("wind_speed_80m");
            List<?> tempList = (List<?>) hourly.get("temperature_2m");
            
            BigDecimal maxVento = BigDecimal.ZERO;
            BigDecimal maxTemp = BigDecimal.ZERO;
            
            if (windList != null && !windList.isEmpty()) {
                // Iterar sobre as 48 horas previstas para achar o pico de vento
                for (int i = 0; i < windList.size(); i++) {
                    Object windObj = windList.get(i);
                    Object tempObj = tempList != null && tempList.size() > i ? tempList.get(i) : null;
                    
                    if (windObj != null) {
                        BigDecimal v = new BigDecimal(windObj.toString());
                        if (v.compareTo(maxVento) > 0) {
                            maxVento = v;
                            if (tempObj != null) {
                                maxTemp = new BigDecimal(tempObj.toString());
                            }
                        }
                    }
                }
                
                Map<String, BigDecimal> result = new HashMap<>();
                result.put("vento", maxVento);
                result.put("temperatura", maxTemp);
                return result;
            }
        }
        throw new RuntimeException("Dados do Open-Meteo inválidos ou lista horária vazia.");
    }
}
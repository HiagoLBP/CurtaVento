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
            BigDecimal ventoAtual = null;
            BigDecimal vento24h = null;
            BigDecimal vento48h = null;
            BigDecimal tempAtual = null;
            String fonte = null;

            try {
                Map<String, BigDecimal> dadosOpenMeteo = fetchOpenMeteoForecast(usina);
                ventoAtual = dadosOpenMeteo.get("vento_atual");
                vento24h = dadosOpenMeteo.get("vento_24h");
                vento48h = dadosOpenMeteo.get("vento_48h");
                tempAtual = dadosOpenMeteo.get("temperatura");
                fonte = "Open-Meteo (Atual e Prev 48h)";
            } catch (Exception ex) {
                logger.error("Falha no Open-Meteo para a usina ID {}. Buscando no cache. Erro: {}", usina.getId(), ex.getMessage());
            }

            if (ventoAtual == null) {
                LeituraClimatica ultima = leituraService.buscarUltimaLeitura(usina.getId());
                if (ultima != null) {
                    ventoAtual = ultima.getVelocidadeVentoMs();
                    vento24h = ultima.getVentoPrevisao24hMs();
                    vento48h = ultima.getVentoPrevisao48hMs();
                    tempAtual = ultima.getTemperaturaC();
                    fonte = "CACHE_ANTIGO";
                } else {
                    logger.error("Nenhuma leitura anterior encontrada para usina ID {}. Usando valores zerados.", usina.getId());
                    ventoAtual = BigDecimal.ZERO;
                    vento24h = BigDecimal.ZERO;
                    vento48h = BigDecimal.ZERO;
                    tempAtual = BigDecimal.ZERO;
                    fonte = "DEFAULT_ZERADO";
                }
            }

            LeituraClimatica leitura = new LeituraClimatica();
            leitura.setUsina(usina);
            leitura.setVelocidadeVentoMs(ventoAtual);
            leitura.setVentoPrevisao24hMs(vento24h);
            leitura.setVentoPrevisao48hMs(vento48h);
            leitura.setTemperaturaC(tempAtual != null ? tempAtual : BigDecimal.ZERO);
            leitura.setFonte(fonte);
            leitura.setDataHora(LocalDateTime.now());

            leituraService.salvar(leitura);
            logger.info("Clima atualizado: Usina {} - Atual: {} m/s - 24h: {} - 48h: {}", usina.getId(), ventoAtual, vento24h, vento48h);
        }
    }

    private Map<String, BigDecimal> fetchOpenMeteoForecast(Usina usina) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            throw new RuntimeException("Usina sem coordenadas para buscar no Open-Meteo.");
        }

        // Busca o 'current' e a 'hourly' para 2 dias
        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m,wind_speed_80m&hourly=wind_speed_80m&wind_speed_unit=ms&forecast_days=2",
                usina.getLatitude(), usina.getLongitude());

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("hourly") && response.containsKey("current")) {
            Map<?, ?> current = (Map<?, ?>) response.get("current");
            Map<?, ?> hourly = (Map<?, ?>) response.get("hourly");
            
            Object currentWindObj = current.get("wind_speed_80m");
            Object currentTempObj = current.get("temperature_2m");
            
            BigDecimal ventoAtual = currentWindObj != null ? new BigDecimal(currentWindObj.toString()) : BigDecimal.ZERO;
            BigDecimal tempAtual = currentTempObj != null ? new BigDecimal(currentTempObj.toString()) : BigDecimal.ZERO;
            
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
                // Próximas 24 horas (24 a 47)
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
            return result;
        }
        throw new RuntimeException("Dados do Open-Meteo inválidos ou lista horária vazia.");
    }
}
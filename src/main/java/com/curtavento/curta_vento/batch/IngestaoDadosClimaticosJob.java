package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaService;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

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
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 segundos
        factory.setReadTimeout(5000);    // 5 segundos
        this.restTemplate = new RestTemplate(factory);
    }

    // Para testes/apresentação: roda 4 segundos após iniciar a aplicação, e depois a cada 10 minutos.
    @Scheduled(initialDelay = 4000, fixedDelay = 600000)
    public void buscarPrevisaoClimatica() {
        logger.info("JOB ATIVO: Buscando PREVISÃO climática (próximas 48h) no Open-Meteo...");

        List<Usina> usinas = usinaRepository.findAll();
        
        // CACHE GEOGRÁFICO: Reduz as chamadas da API de 381 para ~10 chamadas (usinas na mesma latitude/longitude não chamam a API novamente)
        Map<String, Map<String, BigDecimal>> cacheCoordenadas = new HashMap<>();
        
        for (Usina usina : usinas) {
            BigDecimal ventoAtual = null;
            BigDecimal vento24h = null;
            BigDecimal vento48h = null;
            BigDecimal tempAtual = null;
            BigDecimal direcaoVento = null;
            String fonte = null;

                        Map<String, BigDecimal> dadosOpenMeteo = null;
            String coordKey = String.format(java.util.Locale.US, "%.1f,%.1f", usina.getLatitude(), usina.getLongitude());
            
            if (cacheCoordenadas.containsKey(coordKey)) {
                dadosOpenMeteo = cacheCoordenadas.get(coordKey);
            } else {
                int tentativas = 0;
                while(tentativas < 3) {
                    try {
                        dadosOpenMeteo = fetchOpenMeteoForecast(usina);
                        cacheCoordenadas.put(coordKey, dadosOpenMeteo);
                        // Pausa gentil para não bater no rate-limit (apenas quando vai realmente chamar a API)
                        try { Thread.sleep(200); } catch(InterruptedException ie) {}
                        break;
                    } catch (Exception ex) {
                        tentativas++;
                        if(tentativas >= 3) {
                            logger.error("Falha no Open-Meteo para usina ID " + usina.getId() + ". Erro: " + ex.getMessage());
                            // Salva um cache vazio para não metralhar a API de novo nessa mesma coordenada quebrada (Ex: Erro 429)
                            cacheCoordenadas.put(coordKey, null);
                        } else {
                            try { Thread.sleep(2000); } catch(InterruptedException ie) {}
                        }
                    }
                }
            }
            if (dadosOpenMeteo != null) {
                ventoAtual = dadosOpenMeteo.get("vento_atual");
                vento24h = dadosOpenMeteo.get("vento_24h");
                vento48h = dadosOpenMeteo.get("vento_48h");
                tempAtual = dadosOpenMeteo.get("temperatura");
                direcaoVento = dadosOpenMeteo.get("direcao_vento");
                fonte = "Open-Meteo (Atual e Prev 48h)";
            }
                

            if (ventoAtual == null) {
                LeituraClimatica ultima = leituraService.buscarUltimaLeitura(usina.getId());
                if (ultima != null) {
                    ventoAtual = ultima.getVelocidadeVentoMs();
                    vento24h = ultima.getVentoPrevisao24hMs();
                    vento48h = ultima.getVentoPrevisao48hMs();
                    tempAtual = ultima.getTemperaturaC();
                    direcaoVento = ultima.getDirecaoVentoGraus(); // Correção: restaurando a direção
                    fonte = "CACHE_ANTIGO";
                } else {
                    logger.error("Nenhuma leitura anterior encontrada para usina ID {}. Usando valores zerados.", usina.getId());
                    ventoAtual = null;
                    vento24h = null;
                    vento48h = null;
                    tempAtual = null;
                    fonte = "SEM_DADOS";
                    // Removendo a atribuição de zeros que distorcia o modelo.
                }
            }

            
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime inicioHora = agora.withMinute(0).withSecond(0).withNano(0);
            
            // Lógica de idempotência (simplificada): se a última leitura for da mesma hora, atualize-a em vez de duplicar.
            LeituraClimatica leitura = leituraService.buscarUltimaLeitura(usina.getId());
            if (leitura == null || leitura.getDataHora().isBefore(inicioHora)) {
                leitura = new LeituraClimatica();
                leitura.setUsina(usina);
            }

            leitura.setVelocidadeVentoMs(ventoAtual);
            leitura.setVentoPrevisao24hMs(vento24h);
            leitura.setVentoPrevisao48hMs(vento48h);
            leitura.setTemperaturaC(tempAtual != null ? tempAtual : BigDecimal.ZERO);
            leitura.setDirecaoVentoGraus(direcaoVento);
            leitura.setFonte(fonte);
            leitura.setDataHora(agora);

            leituraService.salvar(leitura);

            logger.info("Clima atualizado: Usina {} - Atual: {} m/s - 24h: {} - 48h: {}", usina.getId(), ventoAtual, vento24h, vento48h);
        }
    }

    
    private Map<String, BigDecimal> fetchOpenMeteoForecast(Usina usina) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            throw new RuntimeException("Usina sem coordenadas para buscar no Open-Meteo.");
        }

        // Busca o 'current' e a 'hourly' para 2 dias
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
            result.put("direcao_vento", direcaoVento);
            return result;
        }
        throw new RuntimeException("Dados do Open-Meteo inválidos ou lista horária vazia.");
    }
}
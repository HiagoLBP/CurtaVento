package com.curtavento.curta_vento.infrastructure.batch;

import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
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

    private final SpringDataLeituraClimaticaRepository springDataLeituraClimaticaRepository;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final OpenMeteoClient openMeteoClient;
    
    public IngestaoDadosClimaticosJob(SpringDataLeituraClimaticaRepository springDataLeituraClimaticaRepository, SpringDataUsinaRepository springDataUsinaRepository, com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient openMeteoClient) {
        this.springDataLeituraClimaticaRepository = springDataLeituraClimaticaRepository;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.openMeteoClient = openMeteoClient;
    }

    // Para testes/apresentaÃƒÂ§ÃƒÂ£o: roda 4 segundos apÃƒÂ³s iniciar a aplicaÃƒÂ§ÃƒÂ£o, e depois a cada 10 minutos.
    @Scheduled(initialDelay = 4000, fixedDelay = 600000)
    public void buscarPrevisaoClimatica() {
        logger.info("JOB ATIVO: Buscando PREVISÃƒÆ’O climÃƒÂ¡tica (prÃƒÂ³ximas 48h) no Open-Meteo...");

        List<UsinaEntity> usinas = springDataUsinaRepository.findAll();
        
        // CACHE GEOGRÃƒÂFICO: Reduz as chamadas da API de 381 para ~10 chamadas (usinas na mesma latitude/longitude nÃƒÂ£o chamam a API novamente)
        Map<String, Map<String, BigDecimal>> cacheCoordenadas = new HashMap<>();
        
        for (UsinaEntity usina : usinas) {
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
                try {
                    dadosOpenMeteo = openMeteoClient.fetchOpenMeteoForecast(usina);
                    cacheCoordenadas.put(coordKey, dadosOpenMeteo);
                } catch (Exception ex) {
                    logger.error("Falha no Open-Meteo para usina ID " + usina.getId() + " apos retentativas. Erro: " + ex.getMessage());
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
                LeituraClimaticaEntity ultima = springDataLeituraClimaticaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usina.getId());
                if (ultima != null) {
                    ventoAtual = ultima.getVelocidadeVentoMs();
                    vento24h = ultima.getVentoPrevisao24hMs();
                    vento48h = ultima.getVentoPrevisao48hMs();
                    tempAtual = ultima.getTemperaturaC();
                    direcaoVento = ultima.getDirecaoVentoGraus(); // CorreÃƒÂ§ÃƒÂ£o: restaurando a direÃƒÂ§ÃƒÂ£o
                    fonte = "CACHE_ANTIGO";
                } else {
                    logger.error("Nenhuma leitura anterior encontrada para usina ID {}. Usando valores zerados.", usina.getId());
                    ventoAtual = null;
                    vento24h = null;
                    vento48h = null;
                    tempAtual = null;
                    fonte = "SEM_DADOS";
                    // Removendo a atribuiÃƒÂ§ÃƒÂ£o de zeros que distorcia o modelo.
                }
            }

            
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime inicioHora = agora.withMinute(0).withSecond(0).withNano(0);
            
            // LÃƒÂ³gica de idempotÃƒÂªncia (simplificada): se a ÃƒÂºltima leitura for da mesma hora, atualize-a em vez de duplicar.
            LeituraClimaticaEntity leitura = springDataLeituraClimaticaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usina.getId());
            if (leitura == null || leitura.getDataHora().isBefore(inicioHora)) {
                leitura = new LeituraClimaticaEntity();
                leitura.setUsina(usina);
            }

            leitura.setVelocidadeVentoMs(ventoAtual);
            leitura.setVentoPrevisao24hMs(vento24h);
            leitura.setVentoPrevisao48hMs(vento48h);
            leitura.setTemperaturaC(tempAtual != null ? tempAtual : BigDecimal.ZERO);
            leitura.setDirecaoVentoGraus(direcaoVento);
            leitura.setFonte(fonte);
            leitura.setDataHora(agora);

            springDataLeituraClimaticaRepository.save(leitura);

            logger.info("Clima atualizado: UsinaEntity {} - Atual: {} m/s - 24h: {} - 48h: {}", usina.getId(), ventoAtual, vento24h, vento48h);
        }
    }
}


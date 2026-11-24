package com.curtavento.curta_vento.infrastructure.batch;

import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class IngestaoDadosClimaticosJob {

    private static final Logger logger = LoggerFactory.getLogger(IngestaoDadosClimaticosJob.class);

    private final SpringDataLeituraClimaticaRepository springDataLeituraClimaticaRepository;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final OpenMeteoClient openMeteoClient;

    public IngestaoDadosClimaticosJob(SpringDataLeituraClimaticaRepository springDataLeituraClimaticaRepository,
                                      SpringDataUsinaRepository springDataUsinaRepository,
                                      OpenMeteoClient openMeteoClient) {
        this.springDataLeituraClimaticaRepository = springDataLeituraClimaticaRepository;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.openMeteoClient = openMeteoClient;
    }

    @Scheduled(initialDelay = 4000, fixedDelay = 600000)
    public void buscarPrevisaoClimatica() {
        logger.info("JOB ATIVO: Buscando PREVISÃO climática (próximas 48h + radiação solar) no Open-Meteo...");

        List<UsinaEntity> usinas = springDataUsinaRepository.findAll();
        Map<String, Map<String, BigDecimal>> cacheCoordenadas = new HashMap<>();

        for (UsinaEntity usina : usinas) {
            BigDecimal ventoAtual = null;
            BigDecimal vento24h = null;
            BigDecimal vento48h = null;
            BigDecimal tempAtual = null;
            BigDecimal direcaoVento = null;
            BigDecimal radiacaoSolar = null;
            BigDecimal rajadaAtual = null;
            String fonte = null;

            Map<String, BigDecimal> dadosOpenMeteo = null;
            if (usina.getLatitude() != null && usina.getLongitude() != null) {
                String coordKey = String.format(Locale.US, "%.1f,%.1f", usina.getLatitude(), usina.getLongitude());
                if (cacheCoordenadas.containsKey(coordKey)) {
                    dadosOpenMeteo = cacheCoordenadas.get(coordKey);
                } else {
                    try {
                        dadosOpenMeteo = openMeteoClient.fetchOpenMeteoForecast(usina);
                        cacheCoordenadas.put(coordKey, dadosOpenMeteo);
                    } catch (Exception ex) {
                        logger.error("Falha no Open-Meteo para usina ID {} após retentativas. Erro: {}", usina.getId(), ex.getMessage());
                    }
                }
            } else {
                logger.warn("Usina ID {} sem coordenadas cadastradas; pulando consulta Open-Meteo.", usina.getId());
            }

            if (dadosOpenMeteo != null) {
                ventoAtual = dadosOpenMeteo.get("vento_atual");
                vento24h = dadosOpenMeteo.get("vento_24h");
                vento48h = dadosOpenMeteo.get("vento_48h");
                tempAtual = dadosOpenMeteo.get("temperatura");
                direcaoVento = dadosOpenMeteo.get("direcao_vento");
                radiacaoSolar = dadosOpenMeteo.get("radiacao_solar");
                rajadaAtual = dadosOpenMeteo.get("rajada_atual");
                fonte = "Open-Meteo (Atual e Prev 48h)";
            }

            if (ventoAtual == null) {
                LeituraClimaticaEntity ultima = springDataLeituraClimaticaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usina.getId());
                if (ultima != null) {
                    ventoAtual = ultima.getVelocidadeVentoMs();
                    vento24h = ultima.getVentoPrevisao24hMs();
                    vento48h = ultima.getVentoPrevisao48hMs();
                    tempAtual = ultima.getTemperaturaC();
                    direcaoVento = ultima.getDirecaoVentoGraus();
                    radiacaoSolar = ultima.getRadiacaoSolarWm2();
                    rajadaAtual = ultima.getRajadaVentoMs();
                    fonte = "CACHE_ANTIGO";
                } else {
                    logger.error("Nenhuma leitura anterior encontrada para usina ID {}.", usina.getId());
                    fonte = "SEM_DADOS";
                }
            }

            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime inicioHora = agora.withMinute(0).withSecond(0).withNano(0);

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
            leitura.setRadiacaoSolarWm2(radiacaoSolar);
            leitura.setRajadaVentoMs(rajadaAtual);
            leitura.setFonte(fonte);
            leitura.setDataHora(agora);

            springDataLeituraClimaticaRepository.save(leitura);

            logger.info("Clima atualizado: Usina {} - Atual: {} m/s - 24h: {} - 48h: {} - Radiação: {} W/m²",
                    usina.getId(), ventoAtual, vento24h, vento48h, radiacaoSolar);
        }
    }
}

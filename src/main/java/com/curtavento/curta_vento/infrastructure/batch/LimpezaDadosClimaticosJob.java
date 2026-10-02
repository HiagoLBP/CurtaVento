package com.curtavento.curta_vento.infrastructure.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LimpezaDadosClimaticosJob {

    private static final Logger logger = LoggerFactory.getLogger(LimpezaDadosClimaticosJob.class);
    private final JdbcTemplate jdbcTemplate;

    public LimpezaDadosClimaticosJob(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Executa todos os dias as 03:00 da manhǜ
    @Scheduled(cron = "0 0 3 * * *")
    public void limparDadosAntigos() {
        logger.info("Iniciando rotina de limpeza de dados climáticos antigos...");
        LocalDateTime limite = LocalDateTime.now().minusDays(7);
        
        try {
            int deletados = jdbcTemplate.update("DELETE FROM leitura_climatica WHERE data_hora < ?", limite);
            logger.info("Limpeza concluída. {} registros climáticos mais antigos que 7 dias foram deletados.", deletados);
        } catch (Exception e) {
            logger.error("Erro ao limpar dados climáticos antigos", e);
        }
    }
}

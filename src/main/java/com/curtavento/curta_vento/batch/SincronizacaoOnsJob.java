package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.corte.BackfillOnsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class SincronizacaoOnsJob {

    private static final Logger logger = LoggerFactory.getLogger(SincronizacaoOnsJob.class);
    private final BackfillOnsService backfillOnsService;
    private final RestTemplate restTemplate;

    public SincronizacaoOnsJob(BackfillOnsService backfillOnsService) {
        this.backfillOnsService = backfillOnsService;
        this.restTemplate = new RestTemplate();
    }

    // Cron configurado para executar as 19:00 todos os dias, no fuso horario de Sao Paulo
    @Scheduled(cron = "0 0 19 * * *", zone = "America/Sao_Paulo")
    public void verificarEBaixarDadosDoDia() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        String url = String.format("https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_%d_%02d.csv", ano, mes);

        logger.info("Iniciando rotina de verificacao do ONS as 19:00. Verificando S3: {}", url);

        try {
            // Requisicao HEAD: extremamente leve, baixa apenas os cabecalhos e nao o CSV de 20MB
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.HEAD, null, Void.class);
            long lastModifiedEpoch = response.getHeaders().getLastModified();

            if (lastModifiedEpoch != -1) {
                LocalDate dataModificacao = java.time.Instant.ofEpochMilli(lastModifiedEpoch)
                        .atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();

                if (dataModificacao.isEqual(hoje)) {
                    logger.info("Confirmado: o ONS atualizou o arquivo hoje ({}). Iniciando o processamento em memoria do mes {}/{}...", dataModificacao, mes, ano);
                    int linhas = backfillOnsService.baixarEProcessarMes(ano, mes);
                    logger.info("Processamento diario do ONS concluido com sucesso. {} registros processados e inseridos no banco.", linhas);
                } else {
                    logger.warn("Pausa: O arquivo do ONS no S3 ainda nao recebeu upload hoje. A ultima modificacao real foi em {}.", dataModificacao);
                }
            } else {
                logger.warn("O S3 do ONS nao retornou a data de modificacao no cabecalho (Last-Modified). Iniciando download por precaucao...");
                backfillOnsService.baixarEProcessarMes(ano, mes);
            }
        } catch (Exception e) {
            logger.error("Falha ao verificar ou atualizar os dados diarios do ONS: {}", e.getMessage());
        }
    }
}

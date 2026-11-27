package com.curtavento.curta_vento.infrastructure.batch;

import com.curtavento.curta_vento.infrastructure.ons.BackfillOnsService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.OnsSyncCheckpointEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataOnsSyncCheckpointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SincronizacaoOnsJob {

    private static final Logger logger = LoggerFactory.getLogger(SincronizacaoOnsJob.class);
    private static final String URL_EOLICA_DETAIL =
            "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_%d_%02d.csv";
    private static final String URL_SOLAR_DETAIL =
            "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_fotovoltaica_detail_tm/RESTRICAO_COFF_FOTOVOLTAICA_DETAIL_%d_%02d.csv";

    private final BackfillOnsService backfillOnsService;
    private final SpringDataOnsSyncCheckpointRepository checkpointRepository;
    private final RestTemplate restTemplate;
    private final Map<String, Long> cacheMemoriaFallback = new ConcurrentHashMap<>();

    public SincronizacaoOnsJob(BackfillOnsService backfillOnsService) {
        this(backfillOnsService, null);
    }

    @Autowired
    public SincronizacaoOnsJob(BackfillOnsService backfillOnsService,
                               SpringDataOnsSyncCheckpointRepository checkpointRepository) {
        this.backfillOnsService = backfillOnsService;
        this.checkpointRepository = checkpointRepository;
        this.restTemplate = new RestTemplate();
    }

    // Cron configurado para executar as 19:00 todos os dias, no fuso horario de Sao Paulo.
    // Verifica os ultimos 3 meses (Submodulo 6.5 Quadro 1: M+1 e janela de 90 dias) para eolica e solar.
    @Scheduled(cron = "0 0 19 * * *", zone = "America/Sao_Paulo")
    public void verificarEBaixarDadosDoDia() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        YearMonth mesAtual = YearMonth.from(hoje);

        if (hoje.getMonthValue() == 1 && hoje.getDayOfMonth() >= 18 && hoje.getDayOfMonth() <= 25) {
            logger.info("Lembrete regulatorio (Submodulo 6.5 Quadro 1 Ativ. 10): verificar publicacao anual da DISPF_m do ONS (15º dia util de janeiro).");
        }

        for (int offset = 0; offset < 3; offset++) {
            YearMonth ym = mesAtual.minusMonths(offset);
            int ano = ym.getYear();
            int mes = ym.getMonthValue();

            boolean precisaEolica = verificarAlteracao(
                    String.format("EOL_%d_%02d", ano, mes),
                    String.format(URL_EOLICA_DETAIL, ano, mes),
                    hoje, offset);
            boolean precisaSolar = verificarAlteracao(
                    String.format("UFV_%d_%02d", ano, mes),
                    String.format(URL_SOLAR_DETAIL, ano, mes),
                    hoje, offset);

            if (precisaEolica || precisaSolar) {
                int linhas = backfillOnsService.baixarEProcessarMes(ano, mes);
                logger.info("Sincronização {}/{} concluída (eólica={}, solar={}): {} registros processados.",
                        mes, ano, precisaEolica, precisaSolar, linhas);
            }
        }
    }

    private boolean verificarAlteracao(String chave, String url, LocalDate hoje, int offset) {
        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.HEAD, null, Void.class);
            long lastModifiedEpoch = response.getHeaders().getLastModified();
            if (lastModifiedEpoch != -1) {
                LocalDate dataModificacao = java.time.Instant.ofEpochMilli(lastModifiedEpoch)
                        .atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
                Long anterior = lerCheckpoint(chave);
                boolean alterado = (anterior == null && (dataModificacao.isEqual(hoje) || offset > 0))
                        || (anterior != null && lastModifiedEpoch > anterior);

                if (alterado) {
                    logger.info("ONS atualizou {} em {}. Disparando sincronização (revisão D+3/M+8/90d)...",
                            chave, dataModificacao);
                    salvarCheckpoint(chave, lastModifiedEpoch);
                    return true;
                } else if (offset == 0) {
                    logger.info("Arquivo ONS {} sem alteração nova (último upload: {}).", chave, dataModificacao);
                }
            } else if (offset == 0 && chave.startsWith("EOL_")) {
                logger.warn("O S3 do ONS nao retornou Last-Modified para {}. Baixando mes corrente...", chave);
                return true;
            }
        } catch (Exception e) {
            logger.debug("Falha ao verificar S3 do ONS para {}: {}", chave, e.getMessage());
        }
        return false;
    }

    private Long lerCheckpoint(String chave) {
        if (checkpointRepository != null) {
            return checkpointRepository.findById(chave)
                    .map(OnsSyncCheckpointEntity::getLastModifiedEpoch)
                    .orElse(null);
        }
        return cacheMemoriaFallback.get(chave);
    }

    private void salvarCheckpoint(String chave, long lastModifiedEpoch) {
        cacheMemoriaFallback.put(chave, lastModifiedEpoch);
        if (checkpointRepository != null) {
            checkpointRepository.save(new OnsSyncCheckpointEntity(chave, lastModifiedEpoch, LocalDateTime.now()));
        }
    }
}

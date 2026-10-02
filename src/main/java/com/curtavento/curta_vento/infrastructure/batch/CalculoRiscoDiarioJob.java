package com.curtavento.curta_vento.infrastructure.batch;

import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.application.usecase.PrevisaoRiscoService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculoRiscoDiarioJob {

    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataLeituraClimaticaRepository climaRepository;
    private final PrevisaoRiscoService riscoService;
    private final GeracaoAlertasJob alertasJob;

    public CalculoRiscoDiarioJob(SpringDataUsinaRepository springDataUsinaRepository,
                                 SpringDataLeituraClimaticaRepository climaRepository,
                                 PrevisaoRiscoService riscoService,
                                 GeracaoAlertasJob alertasJob) {
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.climaRepository = climaRepository;
        this.riscoService = riscoService;
        this.alertasJob = alertasJob;
    }

    // Cron configurado para rodar de hora em hora (0 15 * * * *)
    // Mas para facilitar seus testes manuais via API de Backfill, deixei rodando a cada 1 min.
    // Como implementamos o Upsert (update or insert), ele NÃO vai duplicar dados!
    @Scheduled(cron = "0 30 2 * * *")
    public void processarCalculoDeRisco() {
        System.out.println("JOB ATIVO: A processar o cálculo diário de risco de curtailment...");

        try {
            List<UsinaEntity> usinas = springDataUsinaRepository.findAll();

            for (UsinaEntity usina : usinas) {
                // 1. Lê a última leitura climática do INMET que o outro job guardou
                LeituraClimaticaEntity ultimaLeitura = climaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usina.getId());

                // Usando o horizonte padrão de 24 horas
                riscoService.calcularRisco(usina, ultimaLeitura, 24);

                System.out.println("Sucesso: Risco calculado para a usina ID " + usina.getId());
            }
            
            // Depois de calcular todos os riscos, chama o gerador de alertas!
            alertasJob.analisarEGerarAlertas();
            
        } catch (Exception e) {
            System.err.println("Erro ao executar job de cálculo de risco: " + e.getMessage());
        }
    }
}
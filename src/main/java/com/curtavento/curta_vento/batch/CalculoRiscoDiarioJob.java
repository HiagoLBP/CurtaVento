package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaRepository;
import com.curtavento.curta_vento.risco.PrevisaoRiscoService;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculoRiscoDiarioJob {

    private final UsinaService usinaService;
    private final LeituraClimaticaRepository climaRepository;
    private final PrevisaoRiscoService riscoService;
    private final GeracaoAlertasJob alertasJob;

    public CalculoRiscoDiarioJob(UsinaService usinaService,
                                 LeituraClimaticaRepository climaRepository,
                                 PrevisaoRiscoService riscoService,
                                 GeracaoAlertasJob alertasJob) {
        this.usinaService = usinaService;
        this.climaRepository = climaRepository;
        this.riscoService = riscoService;
        this.alertasJob = alertasJob;
    }

    // Cron configurado para rodar de hora em hora (0 15 * * * *)
    // Mas para facilitar seus testes manuais via API de Backfill, deixei rodando a cada 1 min.
    // Como implementamos o Upsert (update or insert), ele NÃO vai duplicar dados!
    @Scheduled(fixedRate = 60000)
    public void processarCalculoDeRisco() {
        System.out.println("JOB ATIVO: A processar o cálculo diário de risco de curtailment...");

        try {
            List<Usina> usinas = usinaService.listarTodas();

            for (Usina usina : usinas) {
                // 1. Lê a última leitura climática do INMET que o outro job guardou
                LeituraClimatica ultimaLeitura = climaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usina.getId());

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
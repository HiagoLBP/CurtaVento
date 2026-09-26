package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioService;
import com.curtavento.curta_vento.vaf.ImpactoVafService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecalculoImpactoMunicipalJob {

    private final MunicipioService municipioService;
    private final ImpactoVafService impactoVafService;
    private final GeracaoAlertasJob alertasJob;

    public RecalculoImpactoMunicipalJob(MunicipioService municipioService, ImpactoVafService impactoVafService, GeracaoAlertasJob alertasJob) {
        this.municipioService = municipioService;
        this.impactoVafService = impactoVafService;
        this.alertasJob = alertasJob;
    }

    // Configurado para as 03:30 da madrugada (0 30 3 * * *)
    // Mas para testes manuais via API, roda a cada 1 min. (O Upsert protege contra duplicações)
    @Scheduled(cron = "0 0 2 * * *")
    public void recalcularImpacto() {
        System.out.println("JOB ATIVO: A recalcular impacto financeiro (VAF/ICMS) para os municípios...");

        try {
            List<Municipio> municipios = municipioService.listarTodos();
            int anoAtual = LocalDate.now().getYear();

            for (Municipio municipio : municipios) {

                impactoVafService.calcularImpacto(municipio.getId(), anoAtual);

                System.out.println("Sucesso: Impacto VAF recalculado para o município ID " + municipio.getId());
            }

            // Depois de calcular todos os impactos, chama o gerador de alertas
            alertasJob.analisarEGerarAlertas();

        } catch (Exception e) {
            System.err.println("Erro ao calcular impacto VAF: " + e.getMessage());
        }
    }
}
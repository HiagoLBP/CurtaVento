package com.curtavento.curta_vento.infrastructure.batch;

import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecalculoImpactoMunicipalJob {

    private final SpringDataMunicipioRepository springDataMunicipioRepository;
    private final com.curtavento.curta_vento.application.usecase.ImpactoVafService impactoVafService;
    private final GeracaoAlertasJob alertasJob;

    public RecalculoImpactoMunicipalJob(SpringDataMunicipioRepository springDataMunicipioRepository, com.curtavento.curta_vento.application.usecase.ImpactoVafService impactoVafService, GeracaoAlertasJob alertasJob) {
        this.springDataMunicipioRepository = springDataMunicipioRepository;
        this.impactoVafService = impactoVafService;
        this.alertasJob = alertasJob;
    }

    // Configurado para as 03:30 da madrugada (0 30 3 * * *)
    // Mas para testes manuais via API, roda a cada 1 min. (O Upsert protege contra duplicações)
    @Scheduled(cron = "0 0 2 * * *")
    public void recalcularImpacto() {
        System.out.println("JOB ATIVO: A recalcular impacto financeiro (VAF/ICMS) para os municípios...");

        try {
            List<MunicipioEntity> municipios = springDataMunicipioRepository.findAll();
            int anoAtual = LocalDate.now().getYear();

            for (MunicipioEntity municipio : municipios) {

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
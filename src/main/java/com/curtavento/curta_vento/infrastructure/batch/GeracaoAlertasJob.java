package com.curtavento.curta_vento.infrastructure.batch;

import com.curtavento.curta_vento.application.usecase.AlertaService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GeracaoAlertasJob {

    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataMunicipioRepository springDataMunicipioRepository;
    private final SpringDataPrevisaoRiscoRepository riscoRepository;
    private final SpringDataImpactoVafRepository impactoRepository;
    private final AlertaService alertaService;

    public GeracaoAlertasJob(SpringDataUsinaRepository springDataUsinaRepository, SpringDataMunicipioRepository springDataMunicipioRepository,
                             SpringDataPrevisaoRiscoRepository riscoRepository, SpringDataImpactoVafRepository impactoRepository,
                             AlertaService alertaService) {
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.springDataMunicipioRepository = springDataMunicipioRepository;
        this.riscoRepository = riscoRepository;
        this.impactoRepository = impactoRepository;
        this.alertaService = alertaService;
    }

    public void analisarEGerarAlertas() {
        System.out.println("JOB ATIVO: Analisando limiares para geração de alertas...");
        
        gerarAlertasRiscoCurtailment();
        gerarAlertasQuedaVaf();
        
        System.out.println("JOB CONCLUÍDO: Análise de alertas finalizada.");
    }

    private void gerarAlertasRiscoCurtailment() {
        List<UsinaEntity> usinas = springDataUsinaRepository.findAll();
        for (UsinaEntity u : usinas) {
            PrevisaoRiscoEntity ultimaPrevisao = riscoRepository.findFirstByUsinaIdOrderByDataReferenciaDesc(u.getId());
            if (ultimaPrevisao != null && ultimaPrevisao.getNivelRisco() != null &&
               ("ALTO".equals(ultimaPrevisao.getNivelRisco().name()) || "CRITICO".equals(ultimaPrevisao.getNivelRisco().name()))) {
                
                String msg = String.format("Atenção: Risco de Curtailment %s detectado para a usina %s. Estimativa de corte: %.1f%%",
                        ultimaPrevisao.getNivelRisco().name(), u.getNome(), ultimaPrevisao.getPercentualCorteEstimado());
                
                alertaService.criarAlertaUsina("RISCO_CURTAILMENT", ultimaPrevisao.getNivelRisco().name(), msg, u);
            }
        }
    }

    private void gerarAlertasQuedaVaf() {
        int anoAtual = LocalDate.now().getYear();
        List<MunicipioEntity> municipios = springDataMunicipioRepository.findAll();
        for (MunicipioEntity m : municipios) {
            List<ImpactoVafEntity> impactos = impactoRepository.findByMunicipioIdAndAnoReferencia(m.getId(), anoAtual);
            ImpactoVafEntity impacto = !impactos.isEmpty() ? impactos.get(0) : null;
            
            if (impacto != null && impacto.getImpactoVafEstimado() != null) {
                double perdaVaf = impacto.getImpactoVafEstimado().doubleValue();
                
                if (perdaVaf > 100000.0) { // Se a perda estimada no VAF for maior que 100 mil
                    String msg = String.format("Alerta de Arrecadação: Impacto estimado de R$ %.2f mil no VAF de %s para o ano %d devido ao curtailment eólico.",
                            perdaVaf / 1000.0, m.getNome(), anoAtual);
                    
                    alertaService.criarAlertaMunicipio("QUEDA_VAF", "ALTO", msg, m);
                }
            }
        }
    }
}

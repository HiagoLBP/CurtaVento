package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.alerta.AlertaService;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioService;
import com.curtavento.curta_vento.risco.PrevisaoRisco;
import com.curtavento.curta_vento.risco.PrevisaoRiscoRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaService;
import com.curtavento.curta_vento.vaf.ImpactoVaf;
import com.curtavento.curta_vento.vaf.ImpactoVafRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GeracaoAlertasJob {

    private final UsinaService usinaService;
    private final MunicipioService municipioService;
    private final PrevisaoRiscoRepository riscoRepository;
    private final ImpactoVafRepository impactoRepository;
    private final AlertaService alertaService;

    public GeracaoAlertasJob(UsinaService usinaService, MunicipioService municipioService,
                             PrevisaoRiscoRepository riscoRepository, ImpactoVafRepository impactoRepository,
                             AlertaService alertaService) {
        this.usinaService = usinaService;
        this.municipioService = municipioService;
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
        List<Usina> usinas = usinaService.listarTodas();
        for (Usina u : usinas) {
            PrevisaoRisco ultimaPrevisao = riscoRepository.findFirstByUsinaIdOrderByDataReferenciaDesc(u.getId());
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
        List<Municipio> municipios = municipioService.listarTodos();
        for (Municipio m : municipios) {
            List<ImpactoVaf> impactos = impactoRepository.findByMunicipioIdAndAnoReferencia(m.getId(), anoAtual);
            ImpactoVaf impacto = !impactos.isEmpty() ? impactos.get(0) : null;
            
            if (impacto != null && impacto.isFinanceiroDisponivel()
                    && impacto.getImpactoVafEstimado() != null) {
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

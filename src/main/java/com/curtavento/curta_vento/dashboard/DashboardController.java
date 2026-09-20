package com.curtavento.curta_vento.dashboard;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioService;
import com.curtavento.curta_vento.risco.PrevisaoRisco;
import com.curtavento.curta_vento.risco.PrevisaoRiscoService;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaService;
import com.curtavento.curta_vento.vaf.ImpactoVaf;
import com.curtavento.curta_vento.vaf.ImpactoVafRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UsinaService usinaService;
    private final MunicipioService municipioService;
    private final PrevisaoRiscoService riscoService;
    private final ImpactoVafRepository impactoRepository;

    public DashboardController(UsinaService usinaService,
                               MunicipioService municipioService,
                               PrevisaoRiscoService riscoService,
                               ImpactoVafRepository impactoRepository) {
        this.usinaService = usinaService;
        this.municipioService = municipioService;
        this.riscoService = riscoService;
        this.impactoRepository = impactoRepository;
    }

    @GetMapping("/resumo-geral")
    public ResponseEntity<Map<String, Object>> getResumoGeral() {

        // 1. Usinas e Riscos
        List<Map<String, Object>> usinasDTO = new ArrayList<>();
        for (Usina u : usinaService.listarTodas()) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", u.getNome());
            dto.put("operador", u.getOperador());

            List<PrevisaoRisco> previsoes = riscoService.buscarPorUsina(u.getId());
            if (!previsoes.isEmpty()) {
                PrevisaoRisco ultima = previsoes.get(previsoes.size() - 1);
                dto.put("risco", ultima.getNivelRisco());
                dto.put("corte", ultima.getPercentualCorteEstimado() + "%");
                dto.put("vento", "Via API INMET");
            } else {
                dto.put("risco", "BAIXO");
                dto.put("corte", "0%");
                dto.put("vento", "N/D");
            }
            usinasDTO.add(dto);
        }

        // 2. Municípios e Totais do VAF
        List<Map<String, Object>> municipiosDTO = new ArrayList<>();
        int anoAtual = LocalDate.now().getYear();
        double somaEnergiaTotal = 0.0;
        double somaImpactoTotal = 0.0;

        for (Municipio m : municipioService.listarTodos()) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", m.getNome());

            // Busca o impacto calculado de madrugada pelo novo Job
            List<ImpactoVaf> impactos = impactoRepository.findByMunicipioIdAndAnoReferencia(m.getId(), anoAtual);

            if (impactos != null && !impactos.isEmpty()) {
                ImpactoVaf imp = impactos.get(0);

                // Extrai os valores reais (baixos devido ao limite do teste)
                double impactoReal = imp.getImpactoVafEstimado() != null ? imp.getImpactoVafEstimado().doubleValue() : 0.0;
                double energiaReal = imp.getEnergiaTotalCortadaMwh() != null ? imp.getEnergiaTotalCortadaMwh().doubleValue() : 0.0;

                // --- MULTIPLICADOR DE DEMONSTRAÇÃO (MVP) ---
                // Multiplica os dados do teste por 50.000 para simular um ano inteiro de cortes
                double fatorDemo = 50000.0;

                double valorVafMilhoes = (impactoReal * fatorDemo) / 1_000_000.0;
                double energia = energiaReal * fatorDemo;

                dto.put("valor", valorVafMilhoes);
                dto.put("max", 25.0); // Teto do gráfico ajustado para a escala visual
                dto.put("trend", "Calculado");

                somaEnergiaTotal += energia;
                somaImpactoTotal += valorVafMilhoes;
            } else {
                double valorVafBase = m.getVafAnoBase() != null ? m.getVafAnoBase().doubleValue() / 1_000_000.0 : 0.0;
                dto.put("valor", valorVafBase);
                dto.put("max", 50.0);
                dto.put("trend", "Pendente");
            }

            municipiosDTO.add(dto);
        }

        Map<String, Object> resposta = Map.of(
                "usinas", usinasDTO,
                "municipios", municipiosDTO,
                "energiaTotal", somaEnergiaTotal,
                "impactoTotal", somaImpactoTotal
        );

        return ResponseEntity.ok(resposta);
    }
}
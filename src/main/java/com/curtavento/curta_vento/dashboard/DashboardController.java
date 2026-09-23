package com.curtavento.curta_vento.dashboard;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    private final LeituraClimaticaService leituraService;
    private final com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository corteRepository;

    public DashboardController(UsinaService usinaService,
                               MunicipioService municipioService,
                               PrevisaoRiscoService riscoService,
                               ImpactoVafRepository impactoRepository,
                               LeituraClimaticaService leituraService,
                               com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository corteRepository) {
        this.usinaService = usinaService;
        this.municipioService = municipioService;
        this.riscoService = riscoService;
        this.impactoRepository = impactoRepository;
        this.leituraService = leituraService;
        this.corteRepository = corteRepository;
    }

    @GetMapping("/resumo-geral")
    public ResponseEntity<Map<String, Object>> getResumoGeral(@RequestParam(defaultValue = "2026", required = false) int ano) {

        // 1. Usinas e Riscos (Sempre mostra a previsão para as próximas 24h, independente do ano do histórico)
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
            } else {
                dto.put("risco", "BAIXO");
                dto.put("corte", "0%");
            }

            LeituraClimatica ultimaLeitura = leituraService.buscarUltimaLeitura(u.getId());
            if (ultimaLeitura != null) {
                dto.put("vento", ultimaLeitura.getVelocidadeVentoMs() != null ? ultimaLeitura.getVelocidadeVentoMs() + " m/s" : "N/D");
                dto.put("vento24h", ultimaLeitura.getVentoPrevisao24hMs() != null ? ultimaLeitura.getVentoPrevisao24hMs() + " m/s" : "N/D");
                dto.put("vento48h", ultimaLeitura.getVentoPrevisao48hMs() != null ? ultimaLeitura.getVentoPrevisao48hMs() + " m/s" : "N/D");
            } else {
                dto.put("vento", "N/D");
                dto.put("vento24h", "N/D");
                dto.put("vento48h", "N/D");
            }

            java.time.LocalDateTime inicioAno = java.time.LocalDateTime.of(ano, 1, 1, 0, 0);
            java.time.LocalDateTime fimAno = java.time.LocalDateTime.of(ano, 12, 31, 23, 59, 59);
            List<com.curtavento.curta_vento.corte.HistoricoCorteOns> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(u.getId(), inicioAno, fimAno);
            double somaCortesUsina = cortes.stream()
                .filter(c -> c.getEnergiaCortadaMwh() != null)
                .mapToDouble(c -> c.getEnergiaCortadaMwh().doubleValue())
                .sum();
            
            dto.put("energiaCortada", somaCortesUsina);

            usinasDTO.add(dto);
        }

        // 2. Municípios e Totais do VAF baseados no ANO selecionado no Dropdown
        List<Map<String, Object>> municipiosDTO = new ArrayList<>();
        double somaEnergiaTotal = 0.0;
        double somaImpactoTotal = 0.0;
        double somaPerdaReceitaTotal = 0.0;

        for (Municipio m : municipioService.listarTodos()) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", m.getNome());

            // AQUI ESTÁ A MÁGICA: O banco de dados agora filtra pelo ano selecionado no HTML
            List<ImpactoVaf> impactos = impactoRepository.findByMunicipioIdAndAnoReferencia(m.getId(), ano);

            if (impactos != null && !impactos.isEmpty()) {
                ImpactoVaf imp = impactos.get(impactos.size() - 1);
                // Extrai os valores 100% reais direto do banco de dados (sem fator de demonstração)
                double impactoReal = imp.getImpactoVafEstimado() != null ? imp.getImpactoVafEstimado().doubleValue() : 0.0;
                double energiaReal = imp.getEnergiaTotalCortadaMwh() != null ? imp.getEnergiaTotalCortadaMwh().doubleValue() : 0.0;
                double perdaReceitaReal = imp.getPerdaReceitaEstimada() != null ? imp.getPerdaReceitaEstimada().doubleValue() : 0.0;

                // O frontend agora cuida da formatação (milhões ou milhares)
                double valorVafMilhoes = impactoReal;

                dto.put("valor", valorVafMilhoes);
                dto.put("max", 50000000.0); // Ajustei o teto visual para 50 milhões
                dto.put("trend", "Calculado");

                somaEnergiaTotal += energiaReal;
                somaImpactoTotal += valorVafMilhoes;
                somaPerdaReceitaTotal += perdaReceitaReal;
            } else {
                double valorVafBase = m.getVafAnoBase() != null ? m.getVafAnoBase().doubleValue() : 0.0;
                dto.put("valor", valorVafBase);
                dto.put("max", 50000000.0);
                dto.put("trend", "Pendente");
            }

            municipiosDTO.add(dto);
        }

        // Injeção dos dados analisados da subestação BAIGT para o painel de impacto
        Map<String, Double> motivos = new HashMap<>();
        motivos.put("ENE (Energética)", somaEnergiaTotal * 0.55);
        motivos.put("REL (Rede Local)", somaEnergiaTotal * 0.33);
        motivos.put("CNF (Confiabilidade)", somaEnergiaTotal * 0.12);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("usinas", usinasDTO);
        resposta.put("municipios", municipiosDTO);
        resposta.put("energiaTotal", somaEnergiaTotal);
        resposta.put("impactoTotal", somaImpactoTotal);
        resposta.put("perdaReceitaTotal", somaPerdaReceitaTotal);
        resposta.put("taxaDesperdicio", 31.9);
        resposta.put("motivosCorte", motivos);

        return ResponseEntity.ok(resposta);
    }
}
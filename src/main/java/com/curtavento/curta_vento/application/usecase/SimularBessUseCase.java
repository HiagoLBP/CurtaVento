package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsBlocoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimularBessUseCase {

    private final SpringDataUsinaRepository usinaRepository;
    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    public SimularBessUseCase(SpringDataUsinaRepository usinaRepository,
                              SpringDataHistoricoCorteOnsBlocoRepository blocoRepository,
                              SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.usinaRepository = usinaRepository;
        this.blocoRepository = blocoRepository;
        this.corteRepository = corteRepository;
    }

    public Map<String, Object> executar(Long usinaId,
                                        int ano,
                                        double potenciaMw,
                                        double capacidadeMwh,
                                        double eficienciaRtePercentual,
                                        double precoDescargaPontaMwh,
                                        double capexPorKwhRs,
                                        double opexAnualPercentual) {
        UsinaEntity usina = usinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        double potMw = Math.max(0.5, potenciaMw);
        double capMwh = Math.max(1.0, capacidadeMwh);
        double rte = Math.max(50.0, Math.min(99.0, eficienciaRtePercentual)) / 100.0;
        double precoPonta = Math.max(1.0, precoDescargaPontaMwh);
        double capexKwh = Math.max(100.0, capexPorKwhRs);
        double opexPct = Math.max(0.0, opexAnualPercentual) / 100.0;

        LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        Map<LocalDate, Double> cortePorDia = new TreeMap<>();
        Map<LocalDate, Double> armazenavelPorDia = new TreeMap<>();

        List<HistoricoCorteOnsBlocoEntity> blocos = blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(
                usinaId, inicio, fim);

        String fonteDados = "BLOCOS_30MIN_ONS";
        if (blocos != null && !blocos.isEmpty()) {
            double maxBlocoMwh = potMw * 0.5; // energia máxima que a potência do inversor absorve em 30 min
            for (HistoricoCorteOnsBlocoEntity b : blocos) {
                LocalDate dia = b.getInstante().toLocalDate();
                double corteBloco = b.getEnergiaCortadaMwh() != null ? b.getEnergiaCortadaMwh().doubleValue() : 0.0;
                cortePorDia.merge(dia, corteBloco, Double::sum);

                double atualDia = armazenavelPorDia.getOrDefault(dia, 0.0);
                double espacoRestante = Math.max(0.0, capMwh - atualDia);
                double absorvido = Math.min(espacoRestante, Math.min(corteBloco, maxBlocoMwh));
                armazenavelPorDia.put(dia, atualDia + absorvido);
            }
        } else {
            fonteDados = "HISTORICO_DIARIO_ONS";
            List<HistoricoCorteOnsEntity> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(usinaId, inicio, fim);
            for (HistoricoCorteOnsEntity c : cortes) {
                LocalDate dia = c.getDataCorte().toLocalDate();
                double corteDia = c.getEnergiaCortadaMwh() != null ? c.getEnergiaCortadaMwh().doubleValue() : 0.0;
                cortePorDia.merge(dia, corteDia, Double::sum);
                double absorvido = Math.min(capMwh, cortePorDia.get(dia));
                armazenavelPorDia.put(dia, absorvido);
            }
        }

        double[] cortadaMensal = new double[12];
        double[] recuperadaMensal = new double[12];
        double energiaCortadaAnual = 0.0;
        double energiaArmazenadaAnual = 0.0;

        for (Map.Entry<LocalDate, Double> entry : cortePorDia.entrySet()) {
            LocalDate dia = entry.getKey();
            int idxMes = dia.getMonthValue() - 1;
            double corteDia = entry.getValue();
            double armazenadaDia = armazenavelPorDia.getOrDefault(dia, 0.0);

            energiaCortadaAnual += corteDia;
            energiaArmazenadaAnual += armazenadaDia;
            cortadaMensal[idxMes] += corteDia;
            recuperadaMensal[idxMes] += armazenadaDia * rte;
        }

        double energiaEntreguePontaAnual = energiaArmazenadaAnual * rte;
        double aproveitamentoPct = energiaCortadaAnual > 0
                ? (energiaArmazenadaAnual / energiaCortadaAnual) * 100.0 : 0.0;

        double receitaAnualArbitragemRs = energiaEntreguePontaAnual * precoPonta;
        double capexTotalRs = capMwh * 1000.0 * capexKwh;
        double opexAnualRs = capexTotalRs * opexPct;
        double fluxoAnualLiquidoRs = receitaAnualArbitragemRs - opexAnualRs;

        Double paybackAnos = fluxoAnualLiquidoRs > 0 ? arredondar(capexTotalRs / fluxoAnualLiquidoRs) : null;
        double roi10AnosPct = capexTotalRs > 0
                ? (((fluxoAnualLiquidoRs * 10.0) - capexTotalRs) / capexTotalRs) * 100.0 : 0.0;

        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        List<Map<String, Object>> serieMensal = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("mes", nomesMeses[i]);
            m.put("cortadaMwh", arredondar(cortadaMensal[i]));
            m.put("recuperadaMwh", arredondar(recuperadaMensal[i]));
            serieMensal.add(m);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("usinaId", usina.getId());
        resultado.put("usina", usina.getNome());
        resultado.put("ano", ano);
        resultado.put("fonteDados", fonteDados);
        resultado.put("potenciaMw", potMw);
        resultado.put("capacidadeMwh", capMwh);
        resultado.put("eficienciaRtePct", arredondar(rte * 100.0));
        resultado.put("energiaCortadaAnualMwh", arredondar(energiaCortadaAnual));
        resultado.put("energiaArmazenadaAnualMwh", arredondar(energiaArmazenadaAnual));
        resultado.put("energiaEntreguePontaAnualMwh", arredondar(energiaEntreguePontaAnual));
        resultado.put("aproveitamentoCurtailmentPct", arredondar(aproveitamentoPct));
        resultado.put("receitaAnualArbitragemRs", arredondar(receitaAnualArbitragemRs));
        resultado.put("capexTotalRs", arredondar(capexTotalRs));
        resultado.put("opexAnualRs", arredondar(opexAnualRs));
        resultado.put("fluxoAnualLiquidoRs", arredondar(fluxoAnualLiquidoRs));
        resultado.put("paybackAnos", paybackAnos);
        resultado.put("roi10AnosPct", arredondar(roi10AnosPct));
        resultado.put("serieMensal", serieMensal);
        return resultado;
    }

    private double arredondar(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

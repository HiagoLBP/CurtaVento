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
import java.time.LocalDateTime;
import java.util.*;

/**
 * Simulador de Carga Flexivel / Data Center co-locado behind-the-meter em parques eolicos.
 * Fundamentado em EPE (2025) "Expansao da Carga de Data Centers no Mercado Eletrico Brasileiro"
 * e Stenner et al. (2026) "Toward an Energy-Optimized Operation of Data Centers Located in Wind Farms".
 */
@Service
public class SimularCargaFlexivelDataCenterUseCase {

    private final SpringDataUsinaRepository usinaRepository;
    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    public SimularCargaFlexivelDataCenterUseCase(SpringDataUsinaRepository usinaRepository,
                                                 SpringDataHistoricoCorteOnsBlocoRepository blocoRepository,
                                                 SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.usinaRepository = usinaRepository;
        this.blocoRepository = blocoRepository;
        this.corteRepository = corteRepository;
    }

    public Map<String, Object> executar(Long usinaId,
                                        int ano,
                                        double potenciaTiMw,
                                        double pue,
                                        String modoOperacao,
                                        double fatorCargaMinimoPct,
                                        double tarifaRedeMwhRs,
                                        double valorEnergiaAproveitadaMwhRs,
                                        double tustKwMesRs) {
        return executar(usinaId, ano, potenciaTiMw, pue, modoOperacao, fatorCargaMinimoPct,
                tarifaRedeMwhRs, valorEnergiaAproveitadaMwhRs, tustKwMesRs,
                0.0, 100.0, null, 25.0, 0.085);
    }

    public Map<String, Object> executar(Long usinaId,
                                        int ano,
                                        double potenciaTiMw,
                                        double pue,
                                        String modoOperacao,
                                        double fatorCargaMinimoPct,
                                        double tarifaRedeMwhRs,
                                        double valorEnergiaAproveitadaMwhRs,
                                        double tustKwMesRs,
                                        double horizonteBacklogHoras,
                                        double rampaMaxPctHora,
                                        Long usinaSecundariaId,
                                        double percentualFlexEspacial,
                                        double fatorEmissaoRedeTco2Mwh) {
        UsinaEntity usina = usinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        double tiMw = Math.max(0.5, potenciaTiMw);
        double pueEfetivo = Math.max(1.05, Math.min(2.5, pue));
        double potTotalMw = tiMw * pueEfetivo;
        boolean modoHibridoBase = "HIBRIDO_BASE".equalsIgnoreCase(modoOperacao);
        boolean modoTemporalShift = "TEMPORAL_SHIFT".equalsIgnoreCase(modoOperacao);
        double pisoSlaRatio = Math.max(0.0, Math.min(100.0, fatorCargaMinimoPct)) / 100.0;
        double tarifaRede = Math.max(1.0, tarifaRedeMwhRs);
        double valorMwh = Math.max(1.0, valorEnergiaAproveitadaMwhRs);
        double tustKwMes = Math.max(0.0, tustKwMesRs);
        double hMakeup = modoTemporalShift ? Math.max(1.0, Math.min(24.0, horizonteBacklogHoras > 0 ? horizonteBacklogHoras : 12.0))
                : Math.max(0.0, Math.min(24.0, horizonteBacklogHoras));
        double rampaPctBloco = Math.max(5.0, Math.min(200.0, rampaMaxPctHora)) / 100.0;
        double betaEspacial = Math.max(0.0, Math.min(50.0, percentualFlexEspacial)) / 100.0;
        double fatorCo2 = Math.max(0.0, fatorEmissaoRedeTco2Mwh);

        LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        Map<LocalDateTime, Double> corteSecundarioPorInstante = new HashMap<>();
        String nomeUsinaSecundaria = null;
        if (usinaSecundariaId != null && !usinaSecundariaId.equals(usinaId)) {
            Optional<UsinaEntity> secOpt = usinaRepository.findById(usinaSecundariaId);
            if (secOpt.isPresent()) {
                nomeUsinaSecundaria = secOpt.get().getNome();
                List<HistoricoCorteOnsBlocoEntity> blocosSec = blocoRepository
                        .findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(usinaSecundariaId, inicio, fim);
                if (blocosSec != null) {
                    for (HistoricoCorteOnsBlocoEntity bs : blocosSec) {
                        double cSec = bs.getEnergiaCortadaMwh() != null ? bs.getEnergiaCortadaMwh().doubleValue() : 0.0;
                        if (cSec > 0.0) {
                            corteSecundarioPorInstante.merge(bs.getInstante(), cSec, Double::sum);
                        }
                    }
                }
            }
        }

        double[] cortadaMensal = new double[12];
        double[] absorvidaMensal = new double[12];
        double[] redeMensal = new double[12];

        double energiaCortadaAnual = 0.0;
        double energiaCorteAbsorvida = 0.0;
        double energiaDeslocadaTemporal = 0.0;
        double energiaMigradaEspacial = 0.0;
        double energiaRedeComplementar = 0.0;
        double custoRedeComplementarRs = 0.0;
        int blocosLimitadosRampa = 0;

        List<HistoricoCorteOnsBlocoEntity> blocos = blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(
                usinaId, inicio, fim);

        String fonteDados = "BLOCOS_30MIN_ONS";
        if (blocos != null && !blocos.isEmpty()) {
            double maxBlocoMwh = potTotalMw * 0.5;
            double maxBacklogMwh = hMakeup > 0.0 ? maxBlocoMwh * Math.max(0.0, 1.0 - pisoSlaRatio) * (hMakeup / 0.5) : 0.0;
            double backlogMwh = 0.0;
            double maxDeltaBlocoMwh = maxBlocoMwh * rampaPctBloco * 0.5;
            Double absorvidaAnterior = null;

            for (HistoricoCorteOnsBlocoEntity b : blocos) {
                int mesIdx = b.getInstante().getMonthValue() - 1;
                int hora = b.getInstante().getHour();
                double corteBloco = b.getEnergiaCortadaMwh() != null ? b.getEnergiaCortadaMwh().doubleValue() : 0.0;
                double pldBloco = b.getPldMwh() != null ? b.getPldMwh().doubleValue() : tarifaRede;

                // EPE (2025, slide 10): perfil base CCEE varia de ~0.96 pu (madrugada) a ~1.043 pu (14h-15h)
                double fatorHorarioEpe = (hora >= 3 && hora <= 5) ? 0.96 : (hora >= 14 && hora <= 15) ? 1.043 : 1.0;
                double demandaBaseBloco = modoHibridoBase
                        ? Math.min(maxBlocoMwh, maxBlocoMwh * pisoSlaRatio * fatorHorarioEpe)
                        : (maxBlocoMwh * pisoSlaRatio);

                double absorvidaLocal = Math.min(corteBloco, maxBlocoMwh);
                if (rampaMaxPctHora < 100.0 && absorvidaAnterior != null) {
                    double diff = absorvidaLocal - absorvidaAnterior;
                    if (Math.abs(diff) > maxDeltaBlocoMwh) {
                        absorvidaLocal = Math.max(0.0, Math.min(maxBlocoMwh, absorvidaAnterior + Math.signum(diff) * maxDeltaBlocoMwh));
                        blocosLimitadosRampa++;
                    }
                }
                absorvidaAnterior = absorvidaLocal;

                double drenadoBacklog = 0.0;
                if (hMakeup > 0.0 && backlogMwh > 0.0 && corteBloco > absorvidaLocal) {
                    double folgaCurtailment = Math.min(corteBloco - absorvidaLocal, maxBlocoMwh - absorvidaLocal);
                    drenadoBacklog = Math.min(backlogMwh, Math.max(0.0, folgaCurtailment));
                    backlogMwh -= drenadoBacklog;
                    absorvidaLocal += drenadoBacklog;
                    energiaDeslocadaTemporal += drenadoBacklog;
                }

                double deficitBruto = Math.max(0.0, demandaBaseBloco - absorvidaLocal);
                double adiadoTemporal = 0.0;
                if (hMakeup > 0.0 && deficitBruto > 0.0 && backlogMwh < maxBacklogMwh) {
                    adiadoTemporal = Math.min(deficitBruto, maxBacklogMwh - backlogMwh);
                    backlogMwh += adiadoTemporal;
                    deficitBruto -= adiadoTemporal;
                }

                double migradoEspacial = 0.0;
                if (deficitBruto > 0.0 && nomeUsinaSecundaria != null) {
                    double corteSec = corteSecundarioPorInstante.getOrDefault(b.getInstante(), 0.0);
                    double capEspacialBloco = maxBlocoMwh * betaEspacial;
                    migradoEspacial = Math.min(deficitBruto, Math.min(capEspacialBloco, corteSec));
                    deficitBruto -= migradoEspacial;
                    energiaMigradaEspacial += migradoEspacial;
                }

                double absorvidaTotalBloco = absorvidaLocal + migradoEspacial;
                energiaCortadaAnual += corteBloco;
                energiaCorteAbsorvida += absorvidaTotalBloco;
                energiaRedeComplementar += deficitBruto;
                custoRedeComplementarRs += deficitBruto * pldBloco;

                cortadaMensal[mesIdx] += corteBloco;
                absorvidaMensal[mesIdx] += absorvidaTotalBloco;
                redeMensal[mesIdx] += deficitBruto;
            }
            if (backlogMwh > 0.0) {
                energiaRedeComplementar += backlogMwh;
                custoRedeComplementarRs += backlogMwh * tarifaRede;
            }
        } else {
            fonteDados = "HISTORICO_DIARIO_ONS";
            List<HistoricoCorteOnsEntity> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(usinaId, inicio, fim);
            double maxDiaMwh = potTotalMw * 24.0;
            for (HistoricoCorteOnsEntity c : cortes) {
                int idxMes = c.getDataCorte().getMonthValue() - 1;
                double corteDia = c.getEnergiaCortadaMwh() != null ? c.getEnergiaCortadaMwh().doubleValue() : 0.0;
                double absorvidaDia = Math.min(corteDia, maxDiaMwh);
                double demandaBaseDia = maxDiaMwh * pisoSlaRatio;
                double deficitDia = Math.max(0.0, demandaBaseDia - absorvidaDia);

                energiaCortadaAnual += corteDia;
                energiaCorteAbsorvida += absorvidaDia;
                energiaRedeComplementar += deficitDia;
                custoRedeComplementarRs += deficitDia * tarifaRede;

                cortadaMensal[idxMes] += corteDia;
                absorvidaMensal[idxMes] += absorvidaDia;
                redeMensal[idxMes] += deficitDia;
            }
        }

        double energiaTotalConsumida = energiaCorteAbsorvida + energiaRedeComplementar;
        // Metricas do PDF 18 (Tabela B.2): CEU = Curtailment Energy Usage; GEC = Gray Electricity Consumption
        double ceuPct = energiaCortadaAnual > 0 ? (energiaCorteAbsorvida / energiaCortadaAnual) * 100.0 : 0.0;
        double gecPct = energiaTotalConsumida > 0 ? (energiaRedeComplementar / energiaTotalConsumida) * 100.0 : 0.0;

        // Beneficio behind-the-meter: receita/energia recuperada + economia TUST e GPA (REN 1.122/2025: 3 * MUST * TUST)
        double receitaBrutaRecuperadaRs = energiaCorteAbsorvida * valorMwh;
        double economiaTustAnualRs = potTotalMw * 1000.0 * tustKwMes * 12.0;
        double economiaGarantiaGpaRs = 3.0 * potTotalMw * 1000.0 * tustKwMes;
        double fluxoLiquidoAnualRs = receitaBrutaRecuperadaRs - custoRedeComplementarRs;
        double beneficioTotalComTustRs = fluxoLiquidoAnualRs + economiaTustAnualRs;
        double tco2Evitadas = energiaCorteAbsorvida * fatorCo2;

        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        List<Map<String, Object>> serieMensal = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("mes", nomesMeses[i]);
            m.put("cortadaMwh", arredondar(cortadaMensal[i]));
            m.put("absorvidaDcMwh", arredondar(absorvidaMensal[i]));
            m.put("redeComplementarMwh", arredondar(redeMensal[i]));
            serieMensal.add(m);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("usinaId", usina.getId());
        resp.put("usina", usina.getNome());
        resp.put("usinaSecundaria", nomeUsinaSecundaria);
        resp.put("ano", ano);
        resp.put("fonteDados", fonteDados);
        resp.put("modoOperacao", modoTemporalShift ? "TEMPORAL_SHIFT" : (modoHibridoBase ? "HIBRIDO_BASE" : "FLEXIVEL_CURTAILMENT"));
        resp.put("horizonteBacklogHoras", arredondar(hMakeup));
        resp.put("rampaMaxPctHora", arredondar(rampaMaxPctHora));
        resp.put("blocosLimitadosRampa", blocosLimitadosRampa);
        resp.put("potenciaTiMw", arredondar(tiMw));
        resp.put("pue", arredondar(pueEfetivo));
        resp.put("potenciaTotalMw", arredondar(potTotalMw));
        resp.put("energiaCortadaAnualMwh", arredondar(energiaCortadaAnual));
        resp.put("energiaCorteAbsorvidaMwh", arredondar(energiaCorteAbsorvida));
        resp.put("energiaDeslocadaTemporalMwh", arredondar(energiaDeslocadaTemporal));
        resp.put("energiaMigradaEspacialMwh", arredondar(energiaMigradaEspacial));
        resp.put("energiaRedeComplementarMwh", arredondar(energiaRedeComplementar));
        resp.put("ceuPct", arredondar(ceuPct));
        resp.put("gecPct", arredondar(gecPct));
        resp.put("tco2Evitadas", arredondar(tco2Evitadas));
        resp.put("receitaBrutaRecuperadaRs", arredondar(receitaBrutaRecuperadaRs));
        resp.put("custoRedeComplementarRs", arredondar(custoRedeComplementarRs));
        resp.put("fluxoLiquidoAnualRs", arredondar(fluxoLiquidoAnualRs));
        resp.put("beneficioTotalAnualComTustRs", arredondar(beneficioTotalComTustRs));
        resp.put("economiaTustAnualRs", arredondar(economiaTustAnualRs));
        resp.put("economiaGarantiaGpaRs", arredondar(economiaGarantiaGpaRs));
        resp.put("serieMensal", serieMensal);
        return resp;
    }

    private double arredondar(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

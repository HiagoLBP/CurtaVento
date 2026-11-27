package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.PrecoEnergia;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CalcularRessarcimentoAneelUseCase {

    private static final double PERCENTUAL_RECLASSIFICACAO_NT183 = 0.17;

    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    public CalcularRessarcimentoAneelUseCase(SpringDataHistoricoCorteOnsBlocoRepository blocoRepository,
                                             SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.blocoRepository = blocoRepository;
        this.corteRepository = corteRepository;
    }

    private static class AcumuladorUsina {
        final Long usinaId;
        final String nomeUsina;
        double mwhRel;
        double mwhCnf;
        double mwhEne;
        double mwhPar;
        double mwhOutros;
        double horasRel;
        double horasCnf;
        double horasEne;
        double valorRelRs;
        double valorCnfRs;
        double valorEneRs;
        double valorParRs;
        double valorOutrosRs;

        AcumuladorUsina(Long usinaId, String nomeUsina) {
            this.usinaId = usinaId;
            this.nomeUsina = nomeUsina;
        }
    }

    public Map<String, Object> executar(int ano,
                                        Long usinaId,
                                        double franquiaHorasAnual,
                                        boolean aplicarNotaTecnica183,
                                        BigDecimal precoReferenciaMwh) {
        return executar(ano, usinaId, franquiaHorasAnual, aplicarNotaTecnica183, precoReferenciaMwh, 0.0,
                "PROPOSTA_SENADO_REL_CNF", "EOLICA", null);
    }

    public Map<String, Object> executar(int ano,
                                        Long usinaId,
                                        double franquiaHorasAnual,
                                        boolean aplicarNotaTecnica183,
                                        BigDecimal precoReferenciaMwh,
                                        double percentualRateioRen1170) {
        return executar(ano, usinaId, franquiaHorasAnual, aplicarNotaTecnica183, precoReferenciaMwh,
                percentualRateioRen1170, "PROPOSTA_SENADO_REL_CNF", "EOLICA", null);
    }

    public Map<String, Object> executar(int ano,
                                        Long usinaId,
                                        double franquiaHorasAnual,
                                        boolean aplicarNotaTecnica183,
                                        BigDecimal precoReferenciaMwh,
                                        double percentualRateioRen1170,
                                        String modoRegulatorio,
                                        String tipoFonte,
                                        Double dispfMediaPct) {
        double pctRen1170 = Math.max(0.0, Math.min(100.0, percentualRateioRen1170));
        double fatorRateioRen1170 = pctRen1170 / 100.0;
        boolean somenteRelVigente = "REGRA_VIGENTE_REL".equalsIgnoreCase(modoRegulatorio);
        boolean fonteSolar = "SOLAR".equalsIgnoreCase(tipoFonte) || "UFV".equalsIgnoreCase(tipoFonte);
        String modoNormalizado = somenteRelVigente ? "REGRA_VIGENTE_REL" : "PROPOSTA_SENADO_REL_CNF";
        String fonteNormalizada = fonteSolar ? "SOLAR_UFV" : "EOLICA";
        LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        // RO-AO.BR.13: priorizar blocos semi-horários (30 min) para evitar distorção de razão dominante diária
        List<Object[]> linhas = blocoRepository.findAgregadoRegulatorio(inicio, fim, usinaId);
        String fonteDados = "BLOCOS_30MIN_ONS";
        if (linhas == null || linhas.isEmpty()) {
            linhas = corteRepository.findAgregadoRegulatorioDiario(inicio, fim, usinaId);
            fonteDados = "HISTORICO_DIARIO_ONS";
        }

        Map<Long, AcumuladorUsina> porUsinaMap = new LinkedHashMap<>();
        if (linhas != null) {
            for (Object[] row : linhas) {
                Long id = ((Number) row[0]).longValue();
                String nome = (String) row[1];
                String razao = row[2] != null ? row[2].toString().toUpperCase(Locale.ROOT) : "NAO_CLASSIFICADO";
                double mwh = row[3] != null ? ((BigDecimal) row[3]).doubleValue() : 0.0;
                long blocos = row[4] != null ? ((Number) row[4]).longValue() : 0L;
                double horas = blocos * 0.5;
                double valorRs = precoReferenciaMwh != null
                        ? mwh * precoReferenciaMwh.doubleValue()
                        : (row[5] != null ? ((BigDecimal) row[5]).doubleValue() : mwh * PrecoEnergia.PRECO_MWH);

                AcumuladorUsina acc = porUsinaMap.computeIfAbsent(id, k -> new AcumuladorUsina(id, nome));
                switch (razao) {
                    case "REL" -> {
                        acc.mwhRel += mwh;
                        acc.horasRel += horas;
                        acc.valorRelRs += valorRs;
                    }
                    case "CNF" -> {
                        acc.mwhCnf += mwh;
                        acc.horasCnf += horas;
                        acc.valorCnfRs += valorRs;
                    }
                    case "ENE" -> {
                        acc.mwhEne += mwh;
                        acc.horasEne += horas;
                        acc.valorEneRs += valorRs;
                    }
                    case "PAR" -> {
                        // RO-AO.BR.13 item 4.8: Parecer de Acesso é desconsiderado da Geração de Referência Final
                        acc.mwhPar += mwh;
                        acc.valorParRs += valorRs;
                    }
                    default -> {
                        acc.mwhOutros += mwh;
                        acc.valorOutrosRs += valorRs;
                    }
                }
            }
        }

        double totalRelMwh = 0.0;
        double totalCnfMwh = 0.0;
        double totalEneMwh = 0.0;
        double totalParMwh = 0.0;
        double totalOutrosMwh = 0.0;
        double totalRessarcivelBrutoMwh = 0.0;
        double totalRessarcivelLiquidoMwh = 0.0;
        double totalFranquiaAbsorvidaMwh = 0.0;
        double totalNaoRessarcivelMwh = 0.0;
        double totalRessarcivelLiquidoRs = 0.0;
        double totalNaoRessarcivelRs = 0.0;
        double totalFranquiaAbsorvidaRs = 0.0;
        double totalParExcluidoRs = 0.0;
        double totalCompensavelRen1170Mwh = 0.0;
        double totalCompensavelRen1170Rs = 0.0;
        double totalCnfNaoRessarcivelMwh = 0.0;
        double totalCnfNaoRessarcivelRs = 0.0;

        List<Map<String, Object>> listaUsinas = new ArrayList<>();
        // Submodulo 6.5 (Sec. 1.4.5 e 1.5.6): (1 - DISPF_m / 100) * horas_ano; Solar UFV = 50% da franquia eolica
        double horasAno = java.time.Year.isLeap(ano) ? 8784.0 : 8760.0;
        double franquiaBase = (dispfMediaPct != null && dispfMediaPct > 0.0 && dispfMediaPct <= 100.0)
                ? (1.0 - (dispfMediaPct / 100.0)) * horasAno
                : Math.max(0.0, franquiaHorasAnual);
        double franquiaAjustada = fonteSolar ? (franquiaBase * 0.5) : franquiaBase;

        for (AcumuladorUsina acc : porUsinaMap.values()) {
            // REN 1.030/2022 + Submodulo 6.5 (Sec. 2.2-2.3): vigente ressarce apenas REL; proposta Senado (PDF 17) inclui CNF
            double relCnfMwh = somenteRelVigente ? acc.mwhRel : (acc.mwhRel + acc.mwhCnf);
            double relCnfRs = somenteRelVigente ? acc.valorRelRs : (acc.valorRelRs + acc.valorCnfRs);
            double horasElegiveis = somenteRelVigente ? acc.horasRel : (acc.horasRel + acc.horasCnf);
            double cnfNaoRessarcivelMwh = somenteRelVigente ? acc.mwhCnf : 0.0;
            double cnfNaoRessarcivelRs = somenteRelVigente ? acc.valorCnfRs : 0.0;

            double eneMwh = acc.mwhEne;
            double eneRs = acc.valorEneRs;

            if (aplicarNotaTecnica183 && relCnfMwh > 0) {
                double mwhMigrado = relCnfMwh * PERCENTUAL_RECLASSIFICACAO_NT183;
                double rsMigrado = relCnfRs * PERCENTUAL_RECLASSIFICACAO_NT183;
                relCnfMwh -= mwhMigrado;
                relCnfRs -= rsMigrado;
                eneMwh += mwhMigrado;
                eneRs += rsMigrado;
                horasElegiveis *= (1.0 - PERCENTUAL_RECLASSIFICACAO_NT183);
            }

            double fatorPosFranquia = 1.0;
            if (horasElegiveis > 0 && franquiaAjustada > 0) {
                double horasExcedentes = Math.max(0.0, horasElegiveis - franquiaAjustada);
                fatorPosFranquia = horasExcedentes / horasElegiveis;
            } else if (horasElegiveis == 0) {
                fatorPosFranquia = 0.0;
            }

            double ressarcivelLiquidoMwh = relCnfMwh * fatorPosFranquia;
            double ressarcivelLiquidoRs = relCnfRs * fatorPosFranquia;
            double franquiaMwh = relCnfMwh - ressarcivelLiquidoMwh;
            double franquiaRs = relCnfRs - ressarcivelLiquidoRs;

            // REN ANEEL nº 1.170/2026: rateio gradual de ENE com hidrelétricas com EVT (25%, 50%, 75%, 100%)
            double compensavelRen1170Mwh = eneMwh * fatorRateioRen1170;
            double compensavelRen1170Rs = eneRs * fatorRateioRen1170;
            double naoRessarcivelMwh = (eneMwh - compensavelRen1170Mwh) + cnfNaoRessarcivelMwh + acc.mwhOutros;
            double naoRessarcivelRs = (eneRs - compensavelRen1170Rs) + cnfNaoRessarcivelRs + acc.valorOutrosRs;

            totalRelMwh += acc.mwhRel;
            totalCnfMwh += acc.mwhCnf;
            totalEneMwh += eneMwh;
            totalParMwh += acc.mwhPar;
            totalOutrosMwh += acc.mwhOutros;
            totalRessarcivelBrutoMwh += relCnfMwh;
            totalRessarcivelLiquidoMwh += ressarcivelLiquidoMwh;
            totalFranquiaAbsorvidaMwh += franquiaMwh;
            totalNaoRessarcivelMwh += naoRessarcivelMwh;
            totalRessarcivelLiquidoRs += ressarcivelLiquidoRs;
            totalNaoRessarcivelRs += naoRessarcivelRs;
            totalFranquiaAbsorvidaRs += franquiaRs;
            totalParExcluidoRs += acc.valorParRs;
            totalCompensavelRen1170Mwh += compensavelRen1170Mwh;
            totalCompensavelRen1170Rs += compensavelRen1170Rs;
            totalCnfNaoRessarcivelMwh += cnfNaoRessarcivelMwh;
            totalCnfNaoRessarcivelRs += cnfNaoRessarcivelRs;

            Map<String, Object> item = new HashMap<>();
            item.put("usinaId", acc.usinaId);
            item.put("usina", acc.nomeUsina);
            item.put("relMwh", arredondar(acc.mwhRel));
            item.put("cnfMwh", arredondar(acc.mwhCnf));
            item.put("eneMwh", arredondar(eneMwh));
            item.put("parExcluidoMwh", arredondar(acc.mwhPar));
            item.put("outrosMwh", arredondar(acc.mwhOutros));
            item.put("horasElegiveis", arredondar(horasElegiveis));
            item.put("ressarcivelLiquidoMwh", arredondar(ressarcivelLiquidoMwh));
            item.put("ressarcivelLiquidoRs", arredondar(ressarcivelLiquidoRs));
            item.put("compensavelRen1170Mwh", arredondar(compensavelRen1170Mwh));
            item.put("compensavelRen1170Rs", arredondar(compensavelRen1170Rs));
            item.put("franquiaAbsorvidaMwh", arredondar(franquiaMwh));
            item.put("naoRessarcivelMwh", arredondar(naoRessarcivelMwh));
            item.put("naoRessarcivelRs", arredondar(naoRessarcivelRs));
            item.put("totalPerdaRs", arredondar(ressarcivelLiquidoRs + compensavelRen1170Rs + franquiaRs + naoRessarcivelRs));
            listaUsinas.add(item);
        }

        listaUsinas.sort((a, b) -> Double.compare((Double) b.get("totalPerdaRs"), (Double) a.get("totalPerdaRs")));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ano", ano);
        resultado.put("fonteDados", fonteDados);
        resultado.put("modoRegulatorio", modoNormalizado);
        resultado.put("tipoFonte", fonteNormalizada);
        resultado.put("cnfRessarcivel", !somenteRelVigente);
        resultado.put("dispfMediaAplicadaPct", dispfMediaPct != null ? arredondar(dispfMediaPct) : null);
        resultado.put("franquiaHorasAplicada", arredondar(franquiaAjustada));
        resultado.put("notaTecnica183Aplicada", aplicarNotaTecnica183);
        resultado.put("percentualRateioRen1170", pctRen1170);
        resultado.put("relMwh", arredondar(totalRelMwh));
        resultado.put("cnfMwh", arredondar(totalCnfMwh));
        resultado.put("cnfNaoRessarcivelMwh", arredondar(totalCnfNaoRessarcivelMwh));
        resultado.put("cnfNaoRessarcivelRs", arredondar(totalCnfNaoRessarcivelRs));
        resultado.put("eneMwh", arredondar(totalEneMwh));
        resultado.put("parExcluidoMwh", arredondar(totalParMwh));
        resultado.put("parExcluidoRs", arredondar(totalParExcluidoRs));
        resultado.put("outrosMwh", arredondar(totalOutrosMwh));
        resultado.put("ressarcivelBrutoMwh", arredondar(totalRessarcivelBrutoMwh));
        resultado.put("ressarcivelLiquidoMwh", arredondar(totalRessarcivelLiquidoMwh));
        resultado.put("ressarcivelLiquidoRs", arredondar(totalRessarcivelLiquidoRs));
        resultado.put("compensavelRen1170Mwh", arredondar(totalCompensavelRen1170Mwh));
        resultado.put("compensavelRen1170Rs", arredondar(totalCompensavelRen1170Rs));
        resultado.put("franquiaAbsorvidaMwh", arredondar(totalFranquiaAbsorvidaMwh));
        resultado.put("franquiaAbsorvidaRs", arredondar(totalFranquiaAbsorvidaRs));
        resultado.put("naoRessarcivelMwh", arredondar(totalNaoRessarcivelMwh));
        resultado.put("naoRessarcivelRs", arredondar(totalNaoRessarcivelRs));
        resultado.put("porUsina", listaUsinas);
        return resultado;
    }

    private double arredondar(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

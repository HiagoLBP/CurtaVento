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
        double mwhOutros;
        double horasRelCnf;
        double horasEne;
        double valorRelRs;
        double valorCnfRs;
        double valorEneRs;
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
        LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        List<Object[]> linhas;
        String fonteDados;
        if (usinaId != null) {
            linhas = blocoRepository.findAgregadoRegulatorio(inicio, fim, usinaId);
            fonteDados = "BLOCOS_30MIN_ONS";
            if (linhas == null || linhas.isEmpty()) {
                linhas = corteRepository.findAgregadoRegulatorioDiario(inicio, fim, usinaId);
                fonteDados = "HISTORICO_DIARIO_ONS";
            }
        } else {
            linhas = corteRepository.findAgregadoRegulatorioDiario(inicio, fim, null);
            fonteDados = "HISTORICO_DIARIO_ONS";
            if (linhas == null || linhas.isEmpty()) {
                linhas = blocoRepository.findAgregadoRegulatorio(inicio, fim, null);
                fonteDados = "BLOCOS_30MIN_ONS";
            }
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
                        acc.horasRelCnf += horas;
                        acc.valorRelRs += valorRs;
                    }
                    case "CNF" -> {
                        acc.mwhCnf += mwh;
                        acc.horasRelCnf += horas;
                        acc.valorCnfRs += valorRs;
                    }
                    case "ENE" -> {
                        acc.mwhEne += mwh;
                        acc.horasEne += horas;
                        acc.valorEneRs += valorRs;
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
        double totalOutrosMwh = 0.0;
        double totalRessarcivelBrutoMwh = 0.0;
        double totalRessarcivelLiquidoMwh = 0.0;
        double totalFranquiaAbsorvidaMwh = 0.0;
        double totalNaoRessarcivelMwh = 0.0;
        double totalRessarcivelLiquidoRs = 0.0;
        double totalNaoRessarcivelRs = 0.0;
        double totalFranquiaAbsorvidaRs = 0.0;

        List<Map<String, Object>> listaUsinas = new ArrayList<>();
        double franquiaAjustada = Math.max(0.0, franquiaHorasAnual);

        for (AcumuladorUsina acc : porUsinaMap.values()) {
            double relCnfMwh = acc.mwhRel + acc.mwhCnf;
            double relCnfRs = acc.valorRelRs + acc.valorCnfRs;
            double horasElegiveis = acc.horasRelCnf;

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

            double naoRessarcivelMwh = eneMwh + acc.mwhOutros;
            double naoRessarcivelRs = eneRs + acc.valorOutrosRs;

            totalRelMwh += acc.mwhRel;
            totalCnfMwh += acc.mwhCnf;
            totalEneMwh += eneMwh;
            totalOutrosMwh += acc.mwhOutros;
            totalRessarcivelBrutoMwh += relCnfMwh;
            totalRessarcivelLiquidoMwh += ressarcivelLiquidoMwh;
            totalFranquiaAbsorvidaMwh += franquiaMwh;
            totalNaoRessarcivelMwh += naoRessarcivelMwh;
            totalRessarcivelLiquidoRs += ressarcivelLiquidoRs;
            totalNaoRessarcivelRs += naoRessarcivelRs;
            totalFranquiaAbsorvidaRs += franquiaRs;

            Map<String, Object> item = new HashMap<>();
            item.put("usinaId", acc.usinaId);
            item.put("usina", acc.nomeUsina);
            item.put("relMwh", arredondar(acc.mwhRel));
            item.put("cnfMwh", arredondar(acc.mwhCnf));
            item.put("eneMwh", arredondar(eneMwh));
            item.put("outrosMwh", arredondar(acc.mwhOutros));
            item.put("horasElegiveis", arredondar(horasElegiveis));
            item.put("ressarcivelLiquidoMwh", arredondar(ressarcivelLiquidoMwh));
            item.put("ressarcivelLiquidoRs", arredondar(ressarcivelLiquidoRs));
            item.put("franquiaAbsorvidaMwh", arredondar(franquiaMwh));
            item.put("naoRessarcivelMwh", arredondar(naoRessarcivelMwh));
            item.put("naoRessarcivelRs", arredondar(naoRessarcivelRs));
            item.put("totalPerdaRs", arredondar(ressarcivelLiquidoRs + franquiaRs + naoRessarcivelRs));
            listaUsinas.add(item);
        }

        listaUsinas.sort((a, b) -> Double.compare((Double) b.get("totalPerdaRs"), (Double) a.get("totalPerdaRs")));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ano", ano);
        resultado.put("fonteDados", fonteDados);
        resultado.put("franquiaHorasAplicada", franquiaAjustada);
        resultado.put("notaTecnica183Aplicada", aplicarNotaTecnica183);
        resultado.put("relMwh", arredondar(totalRelMwh));
        resultado.put("cnfMwh", arredondar(totalCnfMwh));
        resultado.put("eneMwh", arredondar(totalEneMwh));
        resultado.put("outrosMwh", arredondar(totalOutrosMwh));
        resultado.put("ressarcivelBrutoMwh", arredondar(totalRessarcivelBrutoMwh));
        resultado.put("ressarcivelLiquidoMwh", arredondar(totalRessarcivelLiquidoMwh));
        resultado.put("ressarcivelLiquidoRs", arredondar(totalRessarcivelLiquidoRs));
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

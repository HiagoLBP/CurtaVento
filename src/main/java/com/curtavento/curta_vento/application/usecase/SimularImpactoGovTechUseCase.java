package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.PrecoEnergia;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimularImpactoGovTechUseCase {

    private static final double ALIQUOTA_ICMS = 0.18;
    private static final double COTA_PARTE_MUNICIPAL_ICMS = 0.25; // CF Art. 158, IV (25% aos municípios)
    private static final double PESO_VAF_NA_COTA_PARTE = 0.65; // EC 108/2020, LC 227/2026 e LC Estadual BA 53/2022 (65% via VAF)
    private static final double VAF_TOTAL_BA_PADRAO = 320_000_000_000.0; // Referência estadual VAF Bahia (R$ 320 bi)
    private static final double ICMS_TOTAL_BA_PADRAO = 42_000_000_000.0; // Referência estadual arrecadação ICMS Bahia (R$ 42 bi)
    private static final double FATOR_CAPACIDADE_MEDIO_BA = 0.42;

    private final SpringDataHistoricoCorteOnsRepository corteRepository;
    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    private final SpringDataMunicipioRepository municipioRepository;
    private final SpringDataUsinaRepository usinaRepository;

    public SimularImpactoGovTechUseCase(SpringDataHistoricoCorteOnsRepository corteRepository,
                                        SpringDataHistoricoCorteOnsBlocoRepository blocoRepository,
                                        SpringDataMunicipioRepository municipioRepository,
                                        SpringDataUsinaRepository usinaRepository) {
        this.corteRepository = corteRepository;
        this.blocoRepository = blocoRepository;
        this.municipioRepository = municipioRepository;
        this.usinaRepository = usinaRepository;
    }

    public Map<String, Object> calcularProjecaoPlurianualVaf(int anoBase) {
        return calcularProjecaoPlurianualVaf(anoBase, null, null, null);
    }

    public Map<String, Object> calcularProjecaoPlurianualVaf(int anoBase,
                                                             BigDecimal precoReferenciaMwh,
                                                             BigDecimal vafTotalBaParam,
                                                             BigDecimal icmsTotalBaParam) {
        LocalDateTime inicio = LocalDateTime.of(anoBase, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(anoBase, 12, 31, 23, 59, 59);

        double precoRef = (precoReferenciaMwh != null && precoReferenciaMwh.doubleValue() > 0)
                ? precoReferenciaMwh.doubleValue() : PrecoEnergia.PRECO_MWH;
        double vafTotalBa = (vafTotalBaParam != null && vafTotalBaParam.doubleValue() > 0)
                ? vafTotalBaParam.doubleValue() : VAF_TOTAL_BA_PADRAO;
        double icmsTotalBa = (icmsTotalBaParam != null && icmsTotalBaParam.doubleValue() > 0)
                ? icmsTotalBaParam.doubleValue() : ICMS_TOTAL_BA_PADRAO;

        List<Object[]> resultados = corteRepository.findImpactoPorMunicipio(inicio, fim);
        Map<String, MunicipioEntity> municipiosPorNome = new HashMap<>();
        for (MunicipioEntity m : municipioRepository.findAll()) {
            municipiosPorNome.put(m.getNome(), m);
        }

        List<Map<String, Object>> municipios = new ArrayList<>();
        double totalEnergiaCortadaMwh = 0.0;
        double totalPerdaFaturamentoRs = 0.0;
        double totalImpactoT1Rs = 0.0;
        double totalImpactoT2Rs = 0.0;
        double totalDeltaIcmsEq52Rs = 0.0;
        double totalImpactoT2BienalRs = 0.0;
        double totalImpactoT3BienalRs = 0.0;

        for (Object[] row : resultados) {
            String nomeMunicipio = (String) row[0];
            Long qtdUsinas = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            BigDecimal energiaBd = (BigDecimal) row[2];
            double energiaMwh = energiaBd != null ? energiaBd.doubleValue() : 0.0;

            MunicipioEntity mun = municipiosPorNome.get(nomeMunicipio);
            double ipm = resolverIpm(mun);

            // Equação 5.1 (Pré-TCC): ΔVAF_m = ΔE_cortada,m * P_ref
            double deltaVafRs = energiaMwh * precoRef;
            double icmsEstadualPerdido = deltaVafRs * ALIQUOTA_ICMS;
            double cotaParteBruta = icmsEstadualPerdido * COTA_PARTE_MUNICIPAL_ICMS * PESO_VAF_NA_COTA_PARTE;

            // Equação 5.2 (Pré-TCC): ΔICMS_m = (ΔVAF_m / VAF_total,BA) * (ICMS_total,BA * 0,25 * 0,65)
            double deltaIcmsEq52Rs = (deltaVafRs / vafTotalBa) * (icmsTotalBa * COTA_PARTE_MUNICIPAL_ICMS * PESO_VAF_NA_COTA_PARTE);
            // LC 63/1990 art. 3º § 4º: média bienal divide o impacto de um ano-base t em 50% em t+2 e 50% em t+3
            double impactoT2Bienal = deltaIcmsEq52Rs * 0.50;
            double impactoT3Bienal = deltaIcmsEq52Rs * 0.50;

            double impactoVafReal = deltaVafRs * ipm;
            double impactoT1 = impactoVafReal * 0.50;
            double impactoT2 = impactoVafReal;
            double impactoAcumulado = impactoT1 + impactoT2;

            totalEnergiaCortadaMwh += energiaMwh;
            totalPerdaFaturamentoRs += deltaVafRs;
            totalImpactoT1Rs += impactoT1;
            totalImpactoT2Rs += impactoT2;
            totalDeltaIcmsEq52Rs += deltaIcmsEq52Rs;
            totalImpactoT2BienalRs += impactoT2Bienal;
            totalImpactoT3BienalRs += impactoT3Bienal;

            Map<String, Object> item = new HashMap<>();
            item.put("municipio", nomeMunicipio);
            item.put("codigoIbge", mun != null ? mun.getCodigoIbge() : "N/D");
            item.put("qtdUsinas", qtdUsinas);
            item.put("ipmPercentual", arredondar(ipm * 100.0, 4));
            item.put("energiaCortadaMwh", arredondar(energiaMwh, 2));
            item.put("faturamentoPerdidoRs", arredondar(deltaVafRs, 2));
            item.put("deltaVafRs", arredondar(deltaVafRs, 2));
            item.put("icmsEstadualNaoGeradoRs", arredondar(icmsEstadualPerdido, 2));
            item.put("cotaParteTeoricaRs", arredondar(cotaParteBruta, 2));
            item.put("deltaIcmsTotalEq52Rs", arredondar(deltaIcmsEq52Rs, 2));
            item.put("impactoAnoT2Bienal", arredondar(impactoT2Bienal, 2));
            item.put("impactoAnoT3Bienal", arredondar(impactoT3Bienal, 2));
            item.put("impactoAnoT1", arredondar(impactoT1, 2));
            item.put("impactoAnoT2", arredondar(impactoT2, 2));
            item.put("impactoAcumuladoBienioRs", arredondar(impactoAcumulado, 2));
            municipios.add(item);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("anoBase", anoBase);
        resp.put("anoImpactoT1", anoBase + 1);
        resp.put("anoImpactoT2", anoBase + 2);
        resp.put("anoImpactoT2Bienal", anoBase + 2);
        resp.put("anoImpactoT3Bienal", anoBase + 3);
        resp.put("precoReferenciaMwh", arredondar(precoRef, 2));
        resp.put("vafTotalBa", arredondar(vafTotalBa, 2));
        resp.put("icmsTotalBa", arredondar(icmsTotalBa, 2));
        resp.put("pesoVafCotaParte", PESO_VAF_NA_COTA_PARTE);
        resp.put("totalEnergiaCortadaMwh", arredondar(totalEnergiaCortadaMwh, 2));
        resp.put("totalPerdaFaturamentoRs", arredondar(totalPerdaFaturamentoRs, 2));
        resp.put("totalDeltaVafRs", arredondar(totalPerdaFaturamentoRs, 2));
        resp.put("totalDeltaIcmsEq52Rs", arredondar(totalDeltaIcmsEq52Rs, 2));
        resp.put("totalImpactoT2BienalRs", arredondar(totalImpactoT2BienalRs, 2));
        resp.put("totalImpactoT3BienalRs", arredondar(totalImpactoT3BienalRs, 2));
        resp.put("totalImpactoT1Rs", arredondar(totalImpactoT1Rs, 2));
        resp.put("totalImpactoT2Rs", arredondar(totalImpactoT2Rs, 2));
        resp.put("totalImpactoBienioRs", arredondar(totalImpactoT1Rs + totalImpactoT2Rs, 2));
        resp.put("municipios", municipios);
        return resp;
    }

    public Map<String, Object> auditarArrendamentoRural(Long usinaId,
                                                        int ano,
                                                        double percentualRoyalty,
                                                        int numeroProprietarios) {
        UsinaEntity usina = usinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        double pctRoyalty = Math.max(0.1, Math.min(20.0, percentualRoyalty)) / 100.0;
        int proprietarios = Math.max(1, numeroProprietarios);

        LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        double energiaCortada = 0.0;
        double energiaEstimada = 0.0;
        double energiaVerificada = 0.0;
        boolean estimativaPorCapacidade = false;

        List<Object[]> totaisDiarios = corteRepository.findTotaisPorUsinaPeriodo(usinaId, inicio, fim);
        if (totaisDiarios != null && !totaisDiarios.isEmpty() && totaisDiarios.get(0)[0] != null) {
            Object[] row = totaisDiarios.get(0);
            energiaCortada = ((Number) row[0]).doubleValue();
            energiaEstimada = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            energiaVerificada = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
        } else {
            List<Object[]> totaisBlocos = blocoRepository.findTotaisEnergiaPorUsina(usinaId, inicio, fim);
            if (totaisBlocos != null && !totaisBlocos.isEmpty() && totaisBlocos.get(0)[0] != null) {
                Object[] row = totaisBlocos.get(0);
                energiaCortada = ((Number) row[0]).doubleValue();
                energiaEstimada = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                energiaVerificada = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            }
        }

        if (energiaEstimada <= 0.0) {
            double capMw = usina.getCapacidadeInstaladaMw() != null ? usina.getCapacidadeInstaladaMw().doubleValue() : 30.0;
            energiaEstimada = Math.max(energiaCortada, capMw * 8760.0 * FATOR_CAPACIDADE_MEDIO_BA);
            energiaVerificada = Math.max(0.0, energiaEstimada - energiaCortada);
            estimativaPorCapacidade = true;
        }

        double precoMwh = usina.getPrecoPpaMwh() != null ? usina.getPrecoPpaMwh().doubleValue() : PrecoEnergia.PRECO_MWH;
        double receitaPotencialRs = energiaEstimada * precoMwh;
        double receitaVerificadaRs = energiaVerificada * precoMwh;
        double receitaPerdidaRs = energiaCortada * precoMwh;

        double royaltyPotencialRs = receitaPotencialRs * pctRoyalty;
        double royaltyDevidoVerificadoRs = receitaVerificadaRs * pctRoyalty;
        double royaltyPerdidoCurtailmentRs = receitaPerdidaRs * pctRoyalty;
        double perdaPorProprietarioRs = royaltyPerdidoCurtailmentRs / proprietarios;
        double pctPerdaRenda = royaltyPotencialRs > 0
                ? (royaltyPerdidoCurtailmentRs / royaltyPotencialRs) * 100.0 : 0.0;

        Map<String, Object> resp = new HashMap<>();
        resp.put("usinaId", usina.getId());
        resp.put("usina", usina.getNome());
        resp.put("municipio", usina.getMunicipio() != null ? usina.getMunicipio().getNome() : "N/D");
        resp.put("ano", ano);
        resp.put("percentualRoyalty", arredondar(pctRoyalty * 100.0, 2));
        resp.put("numeroProprietarios", proprietarios);
        resp.put("estimativaPorCapacidade", estimativaPorCapacidade);
        resp.put("energiaEstimadaMwh", arredondar(energiaEstimada, 2));
        resp.put("energiaVerificadaMwh", arredondar(energiaVerificada, 2));
        resp.put("energiaCortadaMwh", arredondar(energiaCortada, 2));
        resp.put("receitaPotencialRs", arredondar(receitaPotencialRs, 2));
        resp.put("receitaVerificadaRs", arredondar(receitaVerificadaRs, 2));
        resp.put("receitaPerdidaRs", arredondar(receitaPerdidaRs, 2));
        resp.put("royaltyPotencialRs", arredondar(royaltyPotencialRs, 2));
        resp.put("royaltyDevidoVerificadoRs", arredondar(royaltyDevidoVerificadoRs, 2));
        resp.put("royaltyPerdidoCurtailmentRs", arredondar(royaltyPerdidoCurtailmentRs, 2));
        resp.put("perdaPorProprietarioRs", arredondar(perdaPorProprietarioRs, 2));
        resp.put("percentualPerdaRendaRural", arredondar(pctPerdaRenda, 2));
        return resp;
    }

    private double resolverIpm(MunicipioEntity mun) {
        if (mun == null) return 0.02;
        BigDecimal bruto = mun.getIpm() != null ? mun.getIpm() : mun.getVafAnoBase();
        if (bruto == null) return 0.02;
        double v = bruto.doubleValue();
        return v > 1.0 ? v / 100.0 : v;
    }

    private double arredondar(double valor, int casas) {
        return BigDecimal.valueOf(valor).setScale(casas, RoundingMode.HALF_UP).doubleValue();
    }
}

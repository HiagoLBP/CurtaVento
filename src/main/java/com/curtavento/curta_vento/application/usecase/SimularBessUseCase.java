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

    private static final Set<String> UFS_PRIORITARIAS_EPE_NT086 = Set.of("22", "23", "24", "25", "26", "29", "31");

    private record MargemSubestacaoNt095(
            String seReferencia,
            double margemBarramentoMw,
            Double margemAreaMw,
            String tipoConexao,
            boolean limitacaoCarregamentoSae,
            String observacaoCcOuSep
    ) {}

    private static final Map<String, MargemSubestacaoNt095> CATALOGO_NT_ONS_0095_2026 = Map.ofEntries(
            Map.entry("GENTIO OURO II 230", new MargemSubestacaoNt095("SE Gentio do Ouro II 230 kV", 0.0, 350.0, "A", true, "Margem 0 MW em 230 kV (usar eixo 500 kV até 800 MW)")),
            Map.entry("GENTIO OURO II 500", new MargemSubestacaoNt095("SE Gentio do Ouro II 500 kV", 800.0, 1100.0, "A", false, "Área GDO+OUR+SDS 500 kV ≤ 1.100 MW")),
            Map.entry("OUROLANDIA II 230", new MargemSubestacaoNt095("SE Ourolândia II 230 kV", 350.0, 350.0, "A", false, "Subárea Centro-Norte 230 kV compartilhada ≤ 350 MW")),
            Map.entry("OUROLANDIA II 500", new MargemSubestacaoNt095("SE Ourolândia II 500 kV", 1500.0, 3670.0, "A", false, "Eixo 500 kV Norte/Centro BA ≤ 1.100–3.670 MW")),
            Map.entry("MORRO CHAPEU II 230", new MargemSubestacaoNt095("SE Morro do Chapéu II 230 kV", 350.0, 350.0, "A", false, "Subárea 230 kV compartilhada ≤ 350 MW")),
            Map.entry("MORRO CHAPEU II 500", new MargemSubestacaoNt095("SE Morro do Chapéu II 500 kV", 1800.0, 1400.0, "A", false, "Subárea MPD ≤ 1.400 MW · Área NE 500 kV ≤ 3.670 MW")),
            Map.entry("SOL DO SERTAO 500", new MargemSubestacaoNt095("SE Sol do Sertão 500 kV", 1890.0, 1100.0, "A", false, "Área GDO+OUR+SDS ≤ 1.100 MW · Área macro ≤ 3.670 MW")),
            Map.entry("FUTURA 500", new MargemSubestacaoNt095("SE Futura 500 kV", 2165.0, 3670.0, "A", false, "Maior folga da BA na NT-095/2026 (≤ 2.165 MW)")),
            Map.entry("IGAPORA III 230", new MargemSubestacaoNt095("SE Igaporã III 230 kV", 2000.0, 900.0, "A", false, "Subárea/Área BJD+IGD+PND ≤ 900 MW (N-1 AT 500/230 BJD)")),
            Map.entry("IGAPORA III 500", new MargemSubestacaoNt095("SE Igaporã III 500 kV", 200.0, 3200.0, "A", false, "Barra 500 kV ≤ 200 MW · Corredor Oeste BA+MG ≤ 3.200 MW")),
            Map.entry("IGAPORA II 230", new MargemSubestacaoNt095("SE Igaporã II 230 kV", 350.0, 900.0, "B", false, "Conexão Tipo B · Subárea BJD+IGD ≤ 900 MW")),
            Map.entry("IGAPORA II 69", new MargemSubestacaoNt095("SE Igaporã II 69 kV", 100.0, 900.0, "A", false, "Subárea IGD/BJD 230/69 kV ≤ 900 MW")),
            Map.entry("PINDAI II 230", new MargemSubestacaoNt095("SE Pindaí II 230 kV", 900.0, 900.0, "A", false, "Área BJD+BJL+IGD+PND ≤ 900 MW")),
            Map.entry("IRECE 230", new MargemSubestacaoNt095("SE Irecê 230 kV", 350.0, 350.0, "A", true, "Área BMC+IRE ≤ 350 MW (observada limitação de carregamento SAE)")),
            Map.entry("SENHOR DO BONFIM II 230", new MargemSubestacaoNt095("SE Senhor do Bonfim II 230 kV", 0.0, 350.0, "B", false, "Vão Tipo B (sem viabilidade física direta na NT-095/2026)")),
            Map.entry("SOBRADINHO 230", new MargemSubestacaoNt095("SE Sobradinho 230 kV", 0.0, 350.0, "B", false, "Vão Tipo B em 230 kV (usar 500 kV até 1.100 MW)")),
            Map.entry("SOBRADINHO 500", new MargemSubestacaoNt095("SE Sobradinho 500 kV", 1100.0, 3670.0, "A", false, "Margem 500 kV ≤ 1.100 MW")),
            Map.entry("OLINDINA 500", new MargemSubestacaoNt095("SE Olindina 500 kV", 0.0, 460.0, "B", false, "Margem 0 MW e Vão Tipo B na NT-095/2026")),
            Map.entry("BRUMADO II 230", new MargemSubestacaoNt095("SE Brumado II 230/138 kV", 180.0, 180.0, "A", true, "BDD 230+138 ≤ 180 MW por afundamento de tensão e carregamento SAE")),
            Map.entry("JEREMOABO 500", new MargemSubestacaoNt095("SE Jeremoabo 500 kV / Leste BA", 175.0, 460.0, "A", true, "Corredor Sapeaçu–Camaçari: Subárea ≤ 175 MW · Área ≤ 460 MW")),
            Map.entry("BOM JESUS DA LAPA 69", new MargemSubestacaoNt095("SE Bom Jesus da Lapa 69 kV", 0.0, 50.0, "A", true, "0 MW por curto-circuito (disjuntor até 30/03/2030)")),
            Map.entry("BARREIRAS 230", new MargemSubestacaoNt095("SE Barreiras / Oeste BA", 50.0, 50.0, "A", true, "Área Oeste BA ≤ 50 MW por sobrecarga no AT Rio das Éguas (carregamento SAE)"))
    );

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
        return executar(usinaId, ano, potenciaMw, capacidadeMwh, eficienciaRtePercentual,
                precoDescargaPontaMwh, capexPorKwhRs, opexAnualPercentual,
                5.0, 95.0, 10.0, 2.0, 10.0, 0.0);
    }

    public Map<String, Object> executar(Long usinaId,
                                        int ano,
                                        double potenciaMw,
                                        double capacidadeMwh,
                                        double eficienciaRtePercentual,
                                        double precoDescargaPontaMwh,
                                        double capexPorKwhRs,
                                        double opexAnualPercentual,
                                        double socMinPct,
                                        double socMaxPct,
                                        double custoDegradacaoMwhRs,
                                        double degradacaoAnualCapacidadePct,
                                        double taxaDescontoAnualPct) {
        return executar(usinaId, ano, potenciaMw, capacidadeMwh, eficienciaRtePercentual,
                precoDescargaPontaMwh, capexPorKwhRs, opexAnualPercentual,
                socMinPct, socMaxPct, custoDegradacaoMwhRs, degradacaoAnualCapacidadePct, taxaDescontoAnualPct, 0.0);
    }

    public Map<String, Object> executar(Long usinaId,
                                        int ano,
                                        double potenciaMw,
                                        double capacidadeMwh,
                                        double eficienciaRtePercentual,
                                        double precoDescargaPontaMwh,
                                        double capexPorKwhRs,
                                        double opexAnualPercentual,
                                        double socMinPct,
                                        double socMaxPct,
                                        double custoDegradacaoMwhRs,
                                        double degradacaoAnualCapacidadePct,
                                        double taxaDescontoAnualPct,
                                        double receitaFixaCapacidadeKwAnoRs) {
        UsinaEntity usina = usinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        double potMw = Math.max(0.5, potenciaMw);
        double capMwh = Math.max(1.0, capacidadeMwh);
        double rte = Math.max(50.0, Math.min(99.0, eficienciaRtePercentual)) / 100.0;
        double etaCh = Math.sqrt(rte);
        double etaDch = Math.sqrt(rte);
        double socMinRatio = Math.max(0.0, Math.min(40.0, socMinPct)) / 100.0;
        double socMaxRatio = Math.max(socMinRatio + 0.10, Math.min(1.0, socMaxPct / 100.0));
        double socMinMwh = capMwh * socMinRatio;
        double socMaxMwh = capMwh * socMaxRatio;

        double precoPonta = Math.max(1.0, precoDescargaPontaMwh);
        double capexKwh = Math.max(100.0, capexPorKwhRs);
        double opexPct = Math.max(0.0, opexAnualPercentual) / 100.0;
        double degCustoMwh = Math.max(0.0, custoDegradacaoMwhRs);
        double degAnualSoh = Math.max(0.0, Math.min(10.0, degradacaoAnualCapacidadePct)) / 100.0;
        double wacc = Math.max(0.0, Math.min(30.0, taxaDescontoAnualPct)) / 100.0;
        double receitaCapKwAno = Math.max(0.0, receitaFixaCapacidadeKwAnoRs);

        LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        double[] cortadaMensal = new double[12];
        double[] recuperadaMensal = new double[12];
        double energiaCortadaAnual = 0.0;
        double energiaArmazenadaAnual = 0.0;
        double energiaEntreguePontaAnual = 0.0;
        double receitaAnualArbitragemRs = 0.0;
        double corteRelCnfMwh = 0.0;
        double corteEneMwh = 0.0;
        int blocosPldRealUsados = 0;

        List<HistoricoCorteOnsBlocoEntity> blocos = blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(
                usinaId, inicio, fim);

        String fonteDados = "BLOCOS_30MIN_ONS";
        if (blocos != null && !blocos.isEmpty()) {
            double maxBlocoMwh = potMw * 0.5;
            double socMwh = socMinMwh;
            int ultimoMesAtivo = 0;

            for (HistoricoCorteOnsBlocoEntity b : blocos) {
                int mesIdx = b.getInstante().getMonthValue() - 1;
                int hora = b.getInstante().getHour();
                ultimoMesAtivo = mesIdx;

                double corteBloco = b.getEnergiaCortadaMwh() != null ? b.getEnergiaCortadaMwh().doubleValue() : 0.0;
                energiaCortadaAnual += corteBloco;
                cortadaMensal[mesIdx] += corteBloco;

                String razao = b.getCodRazaoRestricao() != null ? b.getCodRazaoRestricao().toUpperCase(Locale.ROOT) : "";
                if ("REL".equals(razao) || "CNF".equals(razao)) corteRelCnfMwh += corteBloco;
                else if ("ENE".equals(razao)) corteEneMwh += corteBloco;

                if (corteBloco > 0.0) {
                    // Carga continua com limite de potencia do inversor e SoC maximo (95% padrao)
                    double espacoInterno = Math.max(0.0, socMaxMwh - socMwh);
                    double absorvivelRede = Math.min(corteBloco, Math.min(maxBlocoMwh, espacoInterno / etaCh));
                    socMwh += absorvivelRede * etaCh;
                    energiaArmazenadaAnual += absorvivelRede;
                } else if (socMwh > socMinMwh) {
                    double pldBloco = b.getPldMwh() != null ? b.getPldMwh().doubleValue() : 0.0;
                    boolean horarioPonta = hora >= 18 && hora <= 21;
                    boolean pldAtrativo = pldBloco > 0.0 && pldBloco >= precoPonta * 0.85;

                    if (horarioPonta || pldAtrativo) {
                        double energiaInternaDisp = socMwh - socMinMwh;
                        double entregueRede = Math.min(maxBlocoMwh, energiaInternaDisp * etaDch);
                        socMwh -= entregueRede / etaDch;
                        double precoEfetivo = pldBloco > 0.0 ? pldBloco : precoPonta;
                        if (pldBloco > 0.0) blocosPldRealUsados++;

                        energiaEntreguePontaAnual += entregueRede;
                        recuperadaMensal[mesIdx] += entregueRede;
                        receitaAnualArbitragemRs += entregueRede * precoEfetivo;
                    }
                }
            }

            // Descarrega saldo residual acima do piso operacional no encerramento da janela simulada
            if (socMwh > socMinMwh) {
                double residualEntregue = (socMwh - socMinMwh) * etaDch;
                energiaEntreguePontaAnual += residualEntregue;
                recuperadaMensal[ultimoMesAtivo] += residualEntregue;
                receitaAnualArbitragemRs += residualEntregue * precoPonta;
            }
        } else {
            fonteDados = "HISTORICO_DIARIO_ONS";
            List<HistoricoCorteOnsEntity> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(usinaId, inicio, fim);
            double capacidadeUtilDia = socMaxMwh - socMinMwh;
            for (HistoricoCorteOnsEntity c : cortes) {
                int idxMes = c.getDataCorte().getMonthValue() - 1;
                double corteDia = c.getEnergiaCortadaMwh() != null ? c.getEnergiaCortadaMwh().doubleValue() : 0.0;
                double armazenadaDia = Math.min(capacidadeUtilDia, corteDia);
                double entregueDia = armazenadaDia * rte;

                energiaCortadaAnual += corteDia;
                energiaArmazenadaAnual += armazenadaDia;
                energiaEntreguePontaAnual += entregueDia;
                cortadaMensal[idxMes] += corteDia;
                recuperadaMensal[idxMes] += entregueDia;
                receitaAnualArbitragemRs += entregueDia * precoPonta;
            }
        }

        double aproveitamentoPct = energiaCortadaAnual > 0
                ? (energiaArmazenadaAnual / energiaCortadaAnual) * 100.0 : 0.0;

        double capexTotalRs = capMwh * 1000.0 * capexKwh;
        double opexAnualRs = capexTotalRs * opexPct;
        double custoDegradacaoAnualRs = energiaEntreguePontaAnual * degCustoMwh;
        double receitaCapacidadeAnualRs = potMw * 1000.0 * receitaCapKwAno;
        double receitaTotalAnualRs = receitaAnualArbitragemRs + receitaCapacidadeAnualRs;
        double fluxoAnualLiquidoRs = receitaTotalAnualRs - opexAnualRs - custoDegradacaoAnualRs;

        Double paybackAnos = fluxoAnualLiquidoRs > 0 ? arredondar(capexTotalRs / fluxoAnualLiquidoRs) : null;
        double roi10AnosPct = capexTotalRs > 0
                ? (((fluxoAnualLiquidoRs * 10.0) - capexTotalRs) / capexTotalRs) * 100.0 : 0.0;

        // Fluxo de caixa descontado em 10 anos considerando degradacao anual de capacidade (SOH)
        double vpl10AnosRs = -capexTotalRs;
        double acumuladoDescontado = 0.0;
        Double paybackDescontadoAnos = null;
        for (int t = 1; t <= 10; t++) {
            double fatorSoh = Math.pow(1.0 - degAnualSoh, t - 1);
            double fcAno = ((receitaAnualArbitragemRs - custoDegradacaoAnualRs) * fatorSoh) + receitaCapacidadeAnualRs - opexAnualRs;
            double fcDescontado = fcAno / Math.pow(1.0 + wacc, t);
            vpl10AnosRs += fcDescontado;
            double anteriorAcum = acumuladoDescontado;
            acumuladoDescontado += fcDescontado;
            if (paybackDescontadoAnos == null && acumuladoDescontado >= capexTotalRs && fcDescontado > 0) {
                double fracao = (capexTotalRs - anteriorAcum) / fcDescontado;
                paybackDescontadoAnos = arredondar((t - 1) + fracao);
            }
        }

        double duracaoHoras = capMwh / potMw;
        double capUtilMwh = Math.max(1.0, socMaxMwh - socMinMwh);
        double ciclosEquivalentesAno = energiaArmazenadaAnual / capUtilMwh;
        double perdasRteAnualMwh = Math.max(0.0, energiaArmazenadaAnual - energiaEntreguePontaAnual);

        // Diagnostico locacional EPE NT-086/2025 + margem de escoamento NT-ONS DPL 0095/2026
        String ibge = usina.getMunicipio() != null ? usina.getMunicipio().getCodigoIbge() : null;
        boolean ufPrioritaria = ibge != null && ibge.length() >= 2 && UFS_PRIORITARIAS_EPE_NT086.contains(ibge.substring(0, 2));
        MargemSubestacaoNt095 margemSe = consultarMargemNt095(usina.getSubestacaoConexao());
        String aderenciaLocacional = ufPrioritaria
                ? "ALTA (UF prioritária EPE NT-086/2025 · baixo MISCR / suporte GFM)"
                : "PADRÃO (verificar robustez MISCR na subestação)";
        String statusNt095 = "SEM_MAPEAMENTO_NT095";
        Double margemSeMw = null;
        String obsNt095 = "Subestação não listada no catálogo BA NT-ONS DPL 0095/2026.";

        List<String> alertas = new ArrayList<>();
        if (margemSe != null) {
            margemSeMw = margemSe.margemBarramentoMw();
            obsNt095 = margemSe.observacaoCcOuSep();
            if ("B".equals(margemSe.tipoConexao()) || margemSe.margemBarramentoMw() <= 0.0) {
                statusNt095 = "RESTRITA_TIPO_B_OU_ZERO_MW";
                alertas.add("NT-ONS 0095/2026 (" + margemSe.seReferencia() + "): " + margemSe.observacaoCcOuSep());
            } else if (potMw > margemSe.margemBarramentoMw() || (margemSe.margemAreaMw() != null && potMw > margemSe.margemAreaMw())) {
                statusNt095 = "EXCEDE_MARGEM_SUBESTACAO";
                alertas.add(String.format(Locale.US, "Potência %.1f MW excede margem remanescente de %.0f MW (%s).",
                        potMw, margemSe.margemBarramentoMw(), margemSe.seReferencia()));
            } else if (margemSe.limitacaoCarregamentoSae()) {
                statusNt095 = "CONDICIONADA_CARREGAMENTO_SAE";
                alertas.add("NT-ONS 0095/2026 (" + margemSe.seReferencia() + "): limitação no carregamento pela rede — operar exclusivamente via curtailment behind-the-meter.");
            } else {
                statusNt095 = "COM_FOLGA_ESCOAMENTO";
            }
        }
        double fracaoEletricaPct = energiaCortadaAnual > 0 ? (corteRelCnfMwh / energiaCortadaAnual) * 100.0 : 0.0;
        if (fracaoEletricaPct >= 40.0) {
            alertas.add(String.format(Locale.US, "%.1f%% dos cortes são REL/CNF: descarga na rede pode sofrer restrição de escoamento local; considere Carga Flexível behind-the-meter.", fracaoEletricaPct));
        } else if (alertas.isEmpty()) {
            alertas.add("Perfil predominante energético (ENE/horário): boa janela para time-shifting e descarga na ponta.");
        }
        if (duracaoHoras <= 1.0 && capexKwh < 2200.0) {
            alertas.add("Alerta EPE/NREL: sistemas curtos (<=1h) têm maior custo específico (R$/kWh) pela não diluição de inversor/BOS.");
        }
        String alertaEscoamento = String.join(" | ", alertas);

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
        resultado.put("subestacaoConexao", usina.getSubestacaoConexao());
        resultado.put("ano", ano);
        resultado.put("fonteDados", fonteDados);
        resultado.put("potenciaMw", potMw);
        resultado.put("capacidadeMwh", capMwh);
        resultado.put("duracaoHoras", arredondar(duracaoHoras));
        resultado.put("capacidadeUtilMwh", arredondar(socMaxMwh - socMinMwh));
        resultado.put("ciclosEquivalentesAno", arredondar(ciclosEquivalentesAno));
        resultado.put("perdasRteAnualMwh", arredondar(perdasRteAnualMwh));
        resultado.put("socMinPct", socMinPct);
        resultado.put("socMaxPct", socMaxPct);
        resultado.put("eficienciaRtePct", arredondar(rte * 100.0));
        resultado.put("energiaCortadaAnualMwh", arredondar(energiaCortadaAnual));
        resultado.put("corteRelCnfMwh", arredondar(corteRelCnfMwh));
        resultado.put("corteEneMwh", arredondar(corteEneMwh));
        resultado.put("energiaArmazenadaAnualMwh", arredondar(energiaArmazenadaAnual));
        resultado.put("energiaEntreguePontaAnualMwh", arredondar(energiaEntreguePontaAnual));
        resultado.put("aproveitamentoCurtailmentPct", arredondar(aproveitamentoPct));
        resultado.put("receitaAnualArbitragemRs", arredondar(receitaAnualArbitragemRs));
        resultado.put("receitaCapacidadeAnualRs", arredondar(receitaCapacidadeAnualRs));
        resultado.put("receitaTotalAnualRs", arredondar(receitaTotalAnualRs));
        resultado.put("custoDegradacaoAnualRs", arredondar(custoDegradacaoAnualRs));
        resultado.put("capexTotalRs", arredondar(capexTotalRs));
        resultado.put("opexAnualRs", arredondar(opexAnualRs));
        resultado.put("fluxoAnualLiquidoRs", arredondar(fluxoAnualLiquidoRs));
        resultado.put("paybackAnos", paybackAnos);
        resultado.put("paybackDescontadoAnos", paybackDescontadoAnos);
        resultado.put("vpl10AnosRs", arredondar(vpl10AnosRs));
        resultado.put("roi10AnosPct", arredondar(roi10AnosPct));
        resultado.put("blocosComPldReal", blocosPldRealUsados);
        resultado.put("aderenciaLocacionalEpe", aderenciaLocacional);
        resultado.put("statusConexaoNt095", statusNt095);
        resultado.put("margemSubestacaoNt095Mw", margemSeMw);
        resultado.put("observacaoNt095", obsNt095);
        resultado.put("fracaoCorteEletricoPct", arredondar(fracaoEletricaPct));
        resultado.put("alertaEscoamento", alertaEscoamento);
        resultado.put("serieMensal", serieMensal);
        return resultado;
    }

    private MargemSubestacaoNt095 consultarMargemNt095(String subestacao) {
        if (subestacao == null || subestacao.isBlank()) return null;
        String norm = java.text.Normalizer.normalize(subestacao, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toUpperCase(Locale.ROOT)
                .replace("MORRO CHAPEU2", "MORRO CHAPEU II")
                .replace("SR.BONFIM", "SENHOR DO BONFIM")
                .replace("U.SOBRADINHO", "SOBRADINHO")
                .replaceAll("KVA|KV|\\([AB]\\)|-|_", " ")
                .replaceAll("([A-Z])(\\d)", "$1 $2")
                .replaceAll("\\s+", " ")
                .trim();

        for (Map.Entry<String, MargemSubestacaoNt095> entry : CATALOGO_NT_ONS_0095_2026.entrySet()) {
            if (norm.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private double arredondar(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

package com.curtavento.curta_vento.infrastructure.ons;

import com.curtavento.curta_vento.application.usecase.HistoricoCorteOnsService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsBlocoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PldHorarioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPldHorarioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BackfillOnsService {

    private static final Logger logger = LoggerFactory.getLogger(BackfillOnsService.class);

    private static final String URL_BASE_DETAIL =
            "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_%d_%02d.csv";
    private static final String URL_BASE_CONJUNTO =
            "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_tm/RESTRICAO_COFF_EOLICA_%d_%02d.csv";

    private static final String DELIMITADOR = ";";
    private static final DateTimeFormatter PARSER_INSTANTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HistoricoCorteOnsService corteService;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataPldHorarioRepository pldHorarioRepository;
    private final RestTemplate restTemplate;

    private record ConjuntoRestricaoInfo(String codRazao, String dscRestricao, String pontoConexao) {}

    private static class AgregadoDiario {
        double energiaCortadaMwh;
        double energiaEstimadaMwh;
        double energiaVerificadaMwh;
        double valoradoPldRs;
        double somaVento;
        int blocosComCorte;
        int blocosVento;
        final Map<String, Double> energiaPorRazao = new HashMap<>();
        String dscPrincipal;
        String subestacaoPrincipal;

        void registrarMotivo(String razao, double energiaMwh, String dsc, String subestacao) {
            energiaPorRazao.merge(razao, energiaMwh, Double::sum);
            if (dsc != null && !dsc.isBlank() && dscPrincipal == null) {
                dscPrincipal = dsc;
            }
            if (subestacao != null && !subestacao.isBlank() && subestacaoPrincipal == null) {
                subestacaoPrincipal = subestacao;
            }
        }

        String razaoDominante() {
            return energiaPorRazao.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("NAO_CLASSIFICADO");
        }
    }

    @Autowired
    public BackfillOnsService(HistoricoCorteOnsService corteService,
                              SpringDataUsinaRepository springDataUsinaRepository,
                              SpringDataPldHorarioRepository pldHorarioRepository) {
        this.corteService = corteService;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.pldHorarioRepository = pldHorarioRepository;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(15));
        factory.setReadTimeout(Duration.ofSeconds(120));
        this.restTemplate = new RestTemplate(factory);
    }

    public int baixarEProcessarMes(int ano, int mes) {
        String urlConjunto = String.format(URL_BASE_CONJUNTO, ano, mes);
        String urlDetail = String.format(URL_BASE_DETAIL, ano, mes);
        logger.info("Iniciando backfill ONS {}/{}...", mes, ano);

        Map<String, ConjuntoRestricaoInfo> restricoesConjunto = carregarRestricoesConjunto(urlConjunto);
        Map<String, BigDecimal> pldPorDiaHora = carregarPldMes(ano, mes);

        List<UsinaEntity> todasUsinas = springDataUsinaRepository.findAll();
        Map<Long, UsinaEntity> usinaPorId = new HashMap<>();
        for (UsinaEntity u : todasUsinas) {
            usinaPorId.put(u.getId(), u);
        }

        Map<String, AgregadoDiario> agregadoPorDia = new HashMap<>();
        Map<String, UsinaEntity> cacheUsinaPorNome = new HashMap<>();
        List<HistoricoCorteOnsBlocoEntity> blocosParaSalvar = new ArrayList<>();
        Set<UsinaEntity> usinasEnriquecidas = new HashSet<>();

        restTemplate.execute(urlDetail, HttpMethod.GET, null, response -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {

                String headerLine = reader.readLine();
                if (headerLine == null) return null;

                Map<String, Integer> colunas = mapearColunas(headerLine);
                int idxEstado = colunas.getOrDefault("id_estado", -1);
                int idxUsina = colunas.getOrDefault("nom_usina", -1);
                int idxIdOns = colunas.getOrDefault("id_ons", -1);
                int idxCeg = colunas.getOrDefault("ceg", -1);
                int idxConjuntoNome = colunas.getOrDefault("nom_conjuntousina", -1);
                int idxConjuntoId = colunas.getOrDefault("id_ons_conjuntousina", -1);
                int idxInstante = colunas.getOrDefault("din_instante", -1);
                int idxVento = colunas.getOrDefault("val_ventoverificado", -1);
                int idxEstimada = colunas.getOrDefault("val_geracaoestimada", -1);
                int idxVerificada = colunas.getOrDefault("val_geracaoverificada", -1);
                int idxFlagRestrita = colunas.getOrDefault("flg_geracaorestrita", -1);

                if (idxUsina == -1 || idxEstimada == -1 || idxVerificada == -1) {
                    logger.error("Colunas obrigatórias não encontradas no CSV detail do ONS.");
                    return null;
                }

                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (linha.isBlank()) continue;
                    String[] campos = linha.split(DELIMITADOR, -1);

                    if (idxEstado >= 0 && idxEstado < campos.length) {
                        String uf = limparCampo(campos[idxEstado]);
                        if (!"BA".equalsIgnoreCase(uf)) continue;
                    }

                    String nomeUsinaCsv = limparCampo(campos[idxUsina]);
                    String idOnsConjunto = idxConjuntoId >= 0 && idxConjuntoId < campos.length ? limparCampo(campos[idxConjuntoId]) : "";
                    String nomeConjunto = idxConjuntoNome >= 0 && idxConjuntoNome < campos.length ? limparCampo(campos[idxConjuntoNome]) : "";
                    String idOnsUsina = idxIdOns >= 0 && idxIdOns < campos.length ? limparCampo(campos[idxIdOns]) : "";
                    String ceg = idxCeg >= 0 && idxCeg < campos.length ? limparCampo(campos[idxCeg]) : "";

                    UsinaEntity usina = cacheUsinaPorNome.computeIfAbsent(
                            nomeUsinaCsv + "|" + ceg + "|" + idOnsUsina,
                            k -> buscarUsina(nomeUsinaCsv, ceg, idOnsUsina, todasUsinas));
                    if (usina == null) continue;

                    String instanteStr = idxInstante >= 0 && idxInstante < campos.length ? limparCampo(campos[idxInstante]) : "";
                    LocalDateTime instante = parseInstante(instanteStr);
                    if (instante == null) continue;

                    String chaveConjunto = idOnsConjunto + "|" + instanteStr;
                    ConjuntoRestricaoInfo infoConjunto = restricoesConjunto.get(chaveConjunto);

                    if (enriquecerUsina(usina, idOnsUsina, ceg, nomeConjunto, infoConjunto)) {
                        usinasEnriquecidas.add(usina);
                    }

                    double valEstimadaMw = idxEstimada < campos.length ? parseDecimalBr(campos[idxEstimada]) : 0.0;
                    double valVerificadaMw = idxVerificada < campos.length ? parseDecimalBr(campos[idxVerificada]) : 0.0;
                    double ventoMs = idxVento >= 0 && idxVento < campos.length ? parseDecimalBr(campos[idxVento]) : 0.0;

                    boolean flagRestrita = idxFlagRestrita < 0 || idxFlagRestrita >= campos.length
                            || "1".equals(limparCampo(campos[idxFlagRestrita]))
                            || "TRUE".equalsIgnoreCase(limparCampo(campos[idxFlagRestrita]))
                            || limparCampo(campos[idxFlagRestrita]).isEmpty();

                    double estimadaMwh = Math.max(0.0, valEstimadaMw * 0.5);
                    double verificadaMwh = Math.max(0.0, valVerificadaMw * 0.5);

                    String chaveDia = usina.getId() + "|" + instante.toLocalDate();
                    AgregadoDiario agg = agregadoPorDia.computeIfAbsent(chaveDia, k -> new AgregadoDiario());
                    agg.energiaEstimadaMwh += estimadaMwh;
                    agg.energiaVerificadaMwh += verificadaMwh;
                    if (ventoMs > 0) {
                        agg.somaVento += ventoMs;
                        agg.blocosVento++;
                    }

                    double valCorteMw = valEstimadaMw - valVerificadaMw;
                    if (valCorteMw <= 0.0 || !flagRestrita) continue;

                    double corteMwh = valCorteMw * 0.5;
                    String codRazao = normalizarRazao(infoConjunto != null ? infoConjunto.codRazao() : null);
                    String dscRestricao = infoConjunto != null ? truncar(infoConjunto.dscRestricao(), 500) : null;
                    String subestacao = infoConjunto != null && infoConjunto.pontoConexao() != null && !infoConjunto.pontoConexao().isBlank()
                            ? infoConjunto.pontoConexao()
                            : usina.getSubestacaoConexao();

                    BigDecimal pldMwh = pldPorDiaHora.get(instante.toLocalDate() + "|" + instante.getHour());
                    BigDecimal precoRef = pldMwh != null ? pldMwh
                            : (usina.getPrecoPpaMwh() != null ? usina.getPrecoPpaMwh() : new BigDecimal("200.00"));

                    agg.energiaCortadaMwh += corteMwh;
                    agg.blocosComCorte++;
                    agg.valoradoPldRs += corteMwh * precoRef.doubleValue();
                    agg.registrarMotivo(codRazao, corteMwh, dscRestricao, subestacao);

                    HistoricoCorteOnsBlocoEntity bloco = new HistoricoCorteOnsBlocoEntity();
                    bloco.setUsina(usina);
                    bloco.setInstante(instante);
                    bloco.setGeracaoEstimadaMw(BigDecimal.valueOf(valEstimadaMw).setScale(6, RoundingMode.HALF_UP));
                    bloco.setGeracaoVerificadaMw(BigDecimal.valueOf(valVerificadaMw).setScale(6, RoundingMode.HALF_UP));
                    bloco.setEnergiaCortadaMwh(BigDecimal.valueOf(corteMwh).setScale(6, RoundingMode.HALF_UP));
                    bloco.setMotivoOns(codRazao);
                    bloco.setCodRazaoRestricao(codRazao);
                    bloco.setDscRestricao(dscRestricao);
                    bloco.setSubestacaoConexao(subestacao);
                    if (ventoMs > 0) {
                        bloco.setVentoVerificadoMs(BigDecimal.valueOf(ventoMs).setScale(2, RoundingMode.HALF_UP));
                    }
                    bloco.setPldMwh(pldMwh);
                    blocosParaSalvar.add(bloco);
                }
            }
            return null;
        });

        if (!usinasEnriquecidas.isEmpty()) {
            springDataUsinaRepository.saveAll(usinasEnriquecidas);
        }

        YearMonth ym = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = ym.atDay(1).atStartOfDay();
        LocalDateTime fimMes = ym.atEndOfMonth().atTime(23, 59, 59);
        int blocosGravados = corteService.registrarBlocos(blocosParaSalvar, inicioMes, fimMes);

        List<HistoricoCorteOnsEntity> cortesParaSalvar = new ArrayList<>();
        for (Map.Entry<String, AgregadoDiario> entry : agregadoPorDia.entrySet()) {
            AgregadoDiario agg = entry.getValue();
            if (agg.energiaCortadaMwh <= 0.0) continue;

            String[] partes = entry.getKey().split("\\|");
            Long usinaId = Long.parseLong(partes[0]);
            LocalDate dia = LocalDate.parse(partes[1]);

            UsinaEntity usina = usinaPorId.get(usinaId);
            if (usina == null) continue;

            String razao = agg.razaoDominante();
            HistoricoCorteOnsEntity corte = new HistoricoCorteOnsEntity();
            corte.setUsina(usina);
            corte.setDataCorte(LocalDateTime.of(dia, LocalTime.NOON));
            corte.setDuracaoMinutos(agg.blocosComCorte * 30);
            corte.setEnergiaCortadaMwh(BigDecimal.valueOf(agg.energiaCortadaMwh).setScale(2, RoundingMode.HALF_UP));
            corte.setEnergiaEstimadaMwh(BigDecimal.valueOf(agg.energiaEstimadaMwh).setScale(4, RoundingMode.HALF_UP));
            corte.setEnergiaVerificadaMwh(BigDecimal.valueOf(agg.energiaVerificadaMwh).setScale(4, RoundingMode.HALF_UP));
            corte.setMotivoOns(razao);
            corte.setCodRazaoRestricao(razao);
            corte.setDscRestricao(agg.dscPrincipal);
            corte.setSubestacaoConexao(agg.subestacaoPrincipal != null ? agg.subestacaoPrincipal : usina.getSubestacaoConexao());
            if (agg.blocosVento > 0) {
                corte.setVentoVerificadoMs(BigDecimal.valueOf(agg.somaVento / agg.blocosVento).setScale(2, RoundingMode.HALF_UP));
            }
            corte.setValoradoPldRs(BigDecimal.valueOf(agg.valoradoPldRs).setScale(2, RoundingMode.HALF_UP));

            cortesParaSalvar.add(corte);
        }

        int registrosGravados = corteService.registrarCortes(cortesParaSalvar, inicioMes, fimMes);

        logger.info("Backfill de {}/{} concluído: {} registros diários e {} blocos semi-horários gravados.",
                mes, ano, registrosGravados, blocosGravados);
        return registrosGravados;
    }

    public int baixarEProcessarIntervalo(int anoInicio, int mesInicio, int anoFim, int mesFim) {
        int total = 0;
        int ano = anoInicio;
        int mes = mesInicio;
        while (ano < anoFim || (ano == anoFim && mes <= mesFim)) {
            try {
                total += baixarEProcessarMes(ano, mes);
            } catch (Exception e) {
                logger.error("Falha ao processar {}/{}: {}", mes, ano, e.getMessage());
            }
            mes++;
            if (mes > 12) { mes = 1; ano++; }
        }
        return total;
    }

    private Map<String, ConjuntoRestricaoInfo> carregarRestricoesConjunto(String urlConjunto) {
        Map<String, ConjuntoRestricaoInfo> mapa = new HashMap<>();
        try {
            restTemplate.execute(urlConjunto, HttpMethod.GET, null, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                    String headerLine = reader.readLine();
                    if (headerLine == null) return null;

                    Map<String, Integer> colunas = mapearColunas(headerLine);
                    int idxEstado = colunas.getOrDefault("id_estado", -1);
                    int idxIdOns = colunas.getOrDefault("id_ons", -1);
                    int idxInstante = colunas.getOrDefault("din_instante", -1);
                    int idxRazao = colunas.getOrDefault("cod_razaorestricao", -1);
                    int idxDesc = colunas.getOrDefault("dsc_restricao", -1);
                    int idxPonto = colunas.getOrDefault("nom_pontoconexao", -1);

                    if (idxIdOns == -1 || idxInstante == -1 || idxRazao == -1) {
                        return null;
                    }

                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        if (linha.isBlank()) continue;
                        String[] campos = linha.split(DELIMITADOR, -1);

                        if (idxEstado >= 0 && idxEstado < campos.length && !"BA".equalsIgnoreCase(limparCampo(campos[idxEstado]))) {
                            continue;
                        }
                        String razao = idxRazao < campos.length ? limparCampo(campos[idxRazao]) : "";
                        String ponto = idxPonto >= 0 && idxPonto < campos.length ? limparCampo(campos[idxPonto]) : "";
                        if (razao.isEmpty() && ponto.isEmpty()) continue;

                        String idOns = limparCampo(campos[idxIdOns]);
                        String instante = limparCampo(campos[idxInstante]);
                        String dsc = idxDesc >= 0 && idxDesc < campos.length ? limparCampo(campos[idxDesc]) : "";

                        mapa.put(idOns + "|" + instante, new ConjuntoRestricaoInfo(razao, dsc, ponto));
                    }
                }
                return null;
            });
            logger.info("Dataset de conjuntos ONS carregado: {} registros indexados para BA.", mapa.size());
        } catch (Exception ex) {
            logger.warn("Não foi possível carregar dataset de conjuntos ONS ({}): {}. Seguindo com dados individuais.",
                    urlConjunto, ex.getMessage());
        }
        return mapa;
    }

    private Map<String, BigDecimal> carregarPldMes(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        Map<String, BigDecimal> pldMap = new HashMap<>();
        List<PldHorarioEntity> registros = pldHorarioRepository.findByDiaBetweenAndSubmercado(
                ym.atDay(1), ym.atEndOfMonth(), "NORDESTE");
        for (PldHorarioEntity p : registros) {
            pldMap.putIfAbsent(p.getDia() + "|" + p.getHora(), p.getPldHora());
        }
        return pldMap;
    }

    private boolean enriquecerUsina(UsinaEntity usina, String idOns, String ceg,
                                    String nomeConjunto, ConjuntoRestricaoInfo infoConjunto) {
        boolean alterada = false;
        if (isBlank(usina.getIdOns()) && !isBlank(idOns)) {
            usina.setIdOns(idOns);
            alterada = true;
        }
        if (isBlank(usina.getCeg()) && !isBlank(ceg) && !"-".equals(ceg)) {
            usina.setCeg(ceg);
            alterada = true;
        }
        if (isBlank(usina.getNomeConjuntoOns()) && !isBlank(nomeConjunto)) {
            usina.setNomeConjuntoOns(nomeConjunto);
            alterada = true;
        }
        if (infoConjunto != null && isBlank(usina.getSubestacaoConexao()) && !isBlank(infoConjunto.pontoConexao())) {
            usina.setSubestacaoConexao(infoConjunto.pontoConexao());
            alterada = true;
        }
        return alterada;
    }

    private String normalizarRazao(String codRazao) {
        if (codRazao == null || codRazao.isBlank()) return "NAO_CLASSIFICADO";
        String upper = codRazao.trim().toUpperCase(Locale.ROOT);
        if (upper.equals("REL") || upper.equals("CNF") || upper.equals("ENE") || upper.equals("PAR")) {
            return upper;
        }
        return upper.length() > 30 ? upper.substring(0, 30) : upper;
    }

    private LocalDateTime parseInstante(String instante) {
        if (isBlank(instante)) return null;
        try {
            return LocalDateTime.parse(instante, PARSER_INSTANTE);
        } catch (Exception e) {
            try {
                return LocalDate.parse(instante.substring(0, 10)).atTime(LocalTime.NOON);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private String limparCampo(String valor) {
        return valor == null ? "" : valor.trim().replace("\"", "");
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    private String truncar(String s, int maxLen) {
        if (s == null || s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    private Map<String, Integer> mapearColunas(String headerLine) {
        String[] headers = headerLine.split(DELIMITADOR, -1);
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            idx.put(headers[i].trim().toLowerCase().replace("\"", ""), i);
        }
        return idx;
    }

    private double parseDecimalBr(String valor) {
        if (valor == null || valor.isBlank()) return 0.0;
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String cleanName(String str) {
        if (str == null) return "";
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "");
    }

    private String removerParenteses(String str) {
        if (str == null) return "";
        return str.replaceAll("\\([^)]*\\)", " ").trim();
    }

    private UsinaEntity buscarUsina(String nomeCsv, String cegCsv, String idOnsCsv, List<UsinaEntity> todasUsinas) {
        if (!isBlank(idOnsCsv)) {
            for (UsinaEntity u : todasUsinas) {
                if (!isBlank(u.getIdOns()) && u.getIdOns().equalsIgnoreCase(idOnsCsv)) {
                    return u;
                }
            }
        }
        if (!isBlank(cegCsv) && !"-".equals(cegCsv)) {
            for (UsinaEntity u : todasUsinas) {
                if (!isBlank(u.getCeg()) && u.getCeg().equalsIgnoreCase(cegCsv)) {
                    return u;
                }
            }
        }

        String nomeLimpoCsv = cleanName(nomeCsv);
        if (nomeLimpoCsv.isEmpty()) return null;

        // 1. Match exato completo
        for (UsinaEntity u : todasUsinas) {
            if (cleanName(u.getNome()).equals(nomeLimpoCsv)) {
                return u;
            }
        }

        // 2. Match exato ignorando observações entre parênteses (ex: "Baraúnas XV (Antiga Massaroca I)")
        String nomeSemParentesesCsv = cleanName(removerParenteses(nomeCsv));
        for (UsinaEntity u : todasUsinas) {
            if (cleanName(removerParenteses(u.getNome())).equals(nomeSemParentesesCsv)) {
                return u;
            }
        }

        return null;
    }
}

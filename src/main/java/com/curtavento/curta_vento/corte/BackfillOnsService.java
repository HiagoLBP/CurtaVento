package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;
import java.util.*;

@Service
public class BackfillOnsService {

    private static final String URL_BASE =
            "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_%d_%02d.csv";

    // O delimitador real dos arquivos do ONS é o ponto e vírgula, não a vírgula
    private static final String DELIMITADOR = ";";

    private final HistoricoCorteOnsService corteService;
    private final UsinaRepository usinaRepository;
    private final HistoricoCorteOnsBlocoRepository blocoRepository;

    @Autowired
    public BackfillOnsService(HistoricoCorteOnsService corteService, UsinaRepository usinaRepository,
                              HistoricoCorteOnsBlocoRepository blocoRepository) {
        this.corteService = corteService;
        this.usinaRepository = usinaRepository;
        this.blocoRepository = blocoRepository;
    }

    public int baixarEProcessarMes(int ano, int mes) {
        String url = String.format(URL_BASE, ano, mes);
        System.out.println("Baixando: " + url);

        Map<String, BigDecimal[]> agregadoPorInstante = new HashMap<>(); // Chave: usina|din_instante
        Map<String, Usina> cacheUsinaPorNome = new HashMap<>();
        Map<String, String> motivoPorInstante = new HashMap<>();
        Map<String, Usina> usinasPorNomeNormalizado = new HashMap<>();
        Set<String> nomesAmbiguos = new HashSet<>();
        for (Usina usina : usinaRepository.findAll()) {
            String nomeNormalizado = normalizarNome(usina.getNome());
            if (nomeNormalizado.isBlank()) continue;
            if (usinasPorNomeNormalizado.containsKey(nomeNormalizado)) {
                nomesAmbiguos.add(nomeNormalizado);
            } else {
                usinasPorNomeNormalizado.put(nomeNormalizado, usina);
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.execute(url, HttpMethod.GET, null, response -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), java.nio.charset.StandardCharsets.UTF_8))) {

                String headerLine = reader.readLine();
                if (headerLine == null) return null;
                Map<String, Integer> idx = mapearColunas(headerLine);

                String linha;
                long totalLinhas = 0;
                long linhasCasadas = 0;

                while ((linha = reader.readLine()) != null) {
                    totalLinhas++;
                    String[] col = linha.split(DELIMITADOR, -1);
                    if (col.length < idx.size()) continue;

                    String estado = col[idx.get("id_estado")].trim();
                    if (!"BA".equalsIgnoreCase(estado)) continue;

                    String nomeUsina = valor(col, idx, "nom_usina", "nom_conjuntousina");
                    Integer minutosRestricao = inteiroOpcional(col, idx, "num_minutos_restricao");
                    String limitada = valor(col, idx, "val_geracaolimitada");
                    String flagRestrita = valor(col, idx, "flg_geracaorestrita");
                    if ((minutosRestricao == null || minutosRestricao <= 0)
                            && limitada.isBlank() && !"1".equals(flagRestrita)) continue;

                    linhasCasadas++;

                    Double verificada = decimalOpcional(col, idx, "val_geracao", "val_geracaoverificada");
                    Double referencia = decimalOpcional(col, idx, "val_geracaoreferencia", "val_geracaoestimada");
                    Double gnra = decimalOpcional(col, idx, "val_geracaonaorealizadaapurada");
                    if (verificada == null || referencia == null) {
                        continue;
                    }
                    double energiaMwh = gnra != null
                            ? gnra * (minutosRestricao != null && minutosRestricao > 0
                            ? minutosRestricao / 60.0 : 0.5)
                            : Math.max(0, referencia - verificada) * 0.5;
                    if (energiaMwh <= 0) continue;
                    String instanteStr = col[idx.get("din_instante")].trim();

                    // Agrupamento NÍVEL 1: Por Usina (ID do banco) e por Instante de 30 min
                    Usina usinaDb = cacheUsinaPorNome.computeIfAbsent(nomeUsina,
                            nome -> resolverUsinaPorNome(nome, usinasPorNomeNormalizado, nomesAmbiguos));
                    if (usinaDb == null) continue; // Ignora se não for alvo

                    String chaveInstante = usinaDb.getId() + "|" + instanteStr;
                    BigDecimal[] valoresInstante = agregadoPorInstante.computeIfAbsent(
                            chaveInstante, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
                    valoresInstante[0] = valoresInstante[0].add(BigDecimal.valueOf(referencia));
                    valoresInstante[1] = valoresInstante[1].add(BigDecimal.valueOf(verificada));
                    valoresInstante[2] = valoresInstante[2].add(BigDecimal.valueOf(energiaMwh));
                    motivoPorInstante.putIfAbsent(chaveInstante, motivoOns(col, idx));
                }
            }
            return null;
        });

        // Agrupamento NÍVEL 2: Calcular o corte final por dia com a correção de Jensen
        Map<String, BigDecimal[]> agregadoPorUsinaEDia = new HashMap<>();
        List<HistoricoCorteOnsBloco> blocosParaSalvar = new ArrayList<>();
        Map<Long, Usina> usinasPorId = new HashMap<>();
        for (Usina usina : cacheUsinaPorNome.values()) {
            if (usina != null) usinasPorId.put(usina.getId(), usina);
        }
        LocalDateTime inicioMes = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fimMes = inicioMes.plusMonths(1).minusNanos(1);
        Map<String, HistoricoCorteOnsBloco> blocosExistentes = new HashMap<>();
        for (HistoricoCorteOnsBloco existente : blocoRepository.findByInstanteBetween(inicioMes, fimMes)) {
            blocosExistentes.put(chaveBloco(existente.getUsina().getId(), existente.getInstante()), existente);
        }
        
        for (Map.Entry<String, BigDecimal[]> entry : agregadoPorInstante.entrySet()) {
            String[] partes = entry.getKey().split("\\|");
            Long usinaId = Long.parseLong(partes[0]);
            
            LocalDateTime instante = LocalDateTime.parse(partes[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDate dia = instante.toLocalDate();
            
            BigDecimal sumEstimada = entry.getValue()[0];
            BigDecimal sumVerificada = entry.getValue()[1];
            
            // Aqui aplicamos a regra do ONS (Submódulo 6.5-PR): max(0, somaEstimada - somaVerificada) no conjunto unificado
            BigDecimal corteNoBloco = entry.getValue()[2];

            Usina usina = usinasPorId.get(usinaId);
            if (usina != null) {
                HistoricoCorteOnsBloco bloco = blocosExistentes.get(chaveBloco(usinaId, instante));
                if (bloco == null) {
                    bloco = new HistoricoCorteOnsBloco();
                    bloco.setUsina(usina);
                    bloco.setInstante(instante);
                }
                bloco.setGeracaoEstimadaMw(sumEstimada);
                bloco.setGeracaoVerificadaMw(sumVerificada);
                bloco.setEnergiaCortadaMwh(corteNoBloco);
                bloco.setMotivoOns(motivoPorInstante.getOrDefault(
                        entry.getKey(), "ONS_DETALHAMENTO_USINA_SEM_MOTIVO"));
                blocosParaSalvar.add(bloco);
            }
            
            if (corteNoBloco.signum() > 0) {
                String chaveDia = usinaId + "|" + dia;
                BigDecimal[] acumulado = agregadoPorUsinaEDia.computeIfAbsent(
                        chaveDia, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                acumulado[0] = acumulado[0].add(corteNoBloco);
                acumulado[1] = acumulado[1].add(BigDecimal.ONE); // 1 bloco com corte
            }
        }

        blocoRepository.saveAll(blocosParaSalvar);

        int registrosGravados = 0;
        for (Map.Entry<String, BigDecimal[]> entrada : agregadoPorUsinaEDia.entrySet()) {
            String[] partes = entrada.getKey().split("\\|");
            Long usinaId = Long.parseLong(partes[0]);
            LocalDate dia = LocalDate.parse(partes[1]);
            BigDecimal somaCorteMwh = entrada.getValue()[0];
            int blocos = entrada.getValue()[1].intValueExact();

            Usina usina = usinaRepository.findById(usinaId).orElse(null);
            if (usina == null) continue;

            HistoricoCorteOns corte = new HistoricoCorteOns();
            corte.setUsina(usina);
            corte.setDataCorte(LocalDateTime.of(dia, LocalTime.NOON));
            corte.setDuracaoMinutos(blocos * 30);
            corte.setEnergiaCortadaMwh(somaCorteMwh);
            corte.setMotivoOns("ONS_DETALHAMENTO_USINA_SEM_MOTIVO");

            corteService.registrarCorte(corte);
            registrosGravados++;
        }

        System.out.println("Backfill de " + mes + "/" + ano + " concluído: " + registrosGravados + " registros diários gravados.");
        return registrosGravados;
    }

    private String chaveBloco(Long usinaId, LocalDateTime instante) {
        return usinaId + "|" + instante;
    }

    public int baixarEProcessarIntervalo(int anoInicio, int mesInicio, int anoFim, int mesFim) {
        int total = 0;
        int ano = anoInicio;
        int mes = mesInicio;
        while (ano < anoFim || (ano == anoFim && mes <= mesFim)) {
            try {
                total += baixarEProcessarMes(ano, mes);
            } catch (Exception e) {
                System.err.println("Falha ao processar " + mes + "/" + ano + ": " + e.getMessage());
            }
            mes++;
            if (mes > 12) { mes = 1; ano++; }
        }
        return total;
    }

    private Map<String, Integer> mapearColunas(String headerLine) {
        String[] headers = headerLine.split(DELIMITADOR, -1);
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            idx.put(headers[i].trim().toLowerCase().replace("\"", ""), i);
        }
        return idx;
    }

    private Double parseDecimalBr(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String valor(String[] colunas, Map<String, Integer> indices, String... nomes) {
        for (String nome : nomes) {
            Integer indice = indices.get(nome);
            if (indice != null && indice < colunas.length) {
                return colunas[indice].trim();
            }
        }
        return "";
    }

    private Double decimalOpcional(String[] colunas, Map<String, Integer> indices, String... nomes) {
        return parseDecimalBr(valor(colunas, indices, nomes));
    }

    private Integer inteiroOpcional(String[] colunas, Map<String, Integer> indices, String... nomes) {
        String texto = valor(colunas, indices, nomes);
        if (texto.isBlank()) return null;
        try {
            return Integer.valueOf(texto);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String motivoOns(String[] colunas, Map<String, Integer> indices) {
        String detalhe = valor(colunas, indices, "dsc_restricao");
        String razao = valor(colunas, indices, "cod_razaorestricao");
        String origem = valor(colunas, indices, "cod_origemrestricao");
        StringBuilder motivo = new StringBuilder();
        if (!razao.isBlank()) motivo.append(razao);
        if (!origem.isBlank()) {
            if (motivo.length() > 0) motivo.append(" - ");
            motivo.append(origem);
        }
        if (!detalhe.isBlank()) {
            if (motivo.length() > 0) motivo.append(": ");
            motivo.append(detalhe);
        }
        return motivo.length() == 0 ? "ONS_MOTIVO_NAO_INFORMADO" : motivo.toString();
    }

    // AQUI ESTÁ A LÓGICA CORRIGIDA QUE DIRECIONA PARA OS IDs 1, 2 E 3
    private Usina resolverUsinaPorNome(String nomeOns, Map<String, Usina> usinasPorNomeNormalizado,
                                       Set<String> nomesAmbiguos) {
        String nomeNormalizado = normalizarNome(nomeOns);
        if (nomesAmbiguos.contains(nomeNormalizado)) {
            return null;
        }
        Usina exata = usinasPorNomeNormalizado.get(nomeNormalizado);
        if (exata != null) return exata;

        List<Usina> candidatas = usinasPorNomeNormalizado.entrySet().stream()
                .filter(entry -> !nomesAmbiguos.contains(entry.getKey()))
                .filter(entry -> entry.getKey().contains(nomeNormalizado)
                        || nomeNormalizado.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .distinct()
                .toList();
        return candidatas.size() == 1 ? candidatas.get(0) : null;
    }

    private String normalizarNome(String nome) {
        if (nome == null) return "";
        String semAcentos = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toUpperCase(Locale.ROOT)
                .replaceAll("\\bCONJUNTO\\b|\\bCONJ\\b", "")
                .replaceAll("[^A-Z0-9]", "");
    }
}
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
import java.util.*;

@Service
public class BackfillOnsService {

    private static final String URL_BASE =
            "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_%d_%02d.csv";

    // O delimitador real dos arquivos do ONS é o ponto e vírgula, não a vírgula
    private static final String DELIMITADOR = ";";

    // Os nomes reais como aparecem nos ficheiros do ONS na coluna nom_conjuntousina
    private static final List<String> NOMES_CONJUNTO_ALVO = List.of(
            "Conj. Caetité",
            "Conj. Caetité 123",
            "Conj. Caetité A",
            "Conj. Caetité Norte",
            "Conj. Igaporã II",
            "Conj. Igaporã III 500 kV"
    );

    private final HistoricoCorteOnsService corteService;
    private final UsinaRepository usinaRepository;

    @Autowired
    public BackfillOnsService(HistoricoCorteOnsService corteService, UsinaRepository usinaRepository) {
        this.corteService = corteService;
        this.usinaRepository = usinaRepository;
    }

    public int baixarEProcessarMes(int ano, int mes) {
        String url = String.format(URL_BASE, ano, mes);
        System.out.println("Baixando: " + url);

        Map<String, double[]> agregadoPorInstante = new HashMap<>(); // Chave: nomeUsina|din_instante
        Map<String, Usina> cacheUsinaPorNome = new HashMap<>();

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

                    String nomeUsina = col[idx.get("nom_conjuntousina")].trim();
                    if (!bateComAlvo(nomeUsina)) continue;

                    String flgRestrita = col[idx.get("flg_geracaorestrita")].trim();
                    if (!"1".equals(flgRestrita)) continue;

                    linhasCasadas++;

                    double estimada = parseDecimalBr(col[idx.get("val_geracaoestimada")]);
                    double verificada = parseDecimalBr(col[idx.get("val_geracaoverificada")]);
                    String instanteStr = col[idx.get("din_instante")].trim();

                    // Agrupamento NÍVEL 1: Por Conjunto e por Instante de 30 min
                    String chaveInstante = nomeUsina + "|" + instanteStr;
                    double[] valoresInstante = agregadoPorInstante.computeIfAbsent(chaveInstante, k -> new double[2]);
                    valoresInstante[0] += estimada;
                    valoresInstante[1] += verificada;
                }
            }
            return null;
        });

        // Agrupamento NÍVEL 2: Calcular o corte final por dia com a correção de Jensen
        Map<String, double[]> agregadoPorUsinaEDia = new HashMap<>();
        
        for (Map.Entry<String, double[]> entry : agregadoPorInstante.entrySet()) {
            String[] partes = entry.getKey().split("\\|");
            String nomeUsina = partes[0];
            
            LocalDateTime instante = LocalDateTime.parse(partes[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDate dia = instante.toLocalDate();
            
            double sumEstimada = entry.getValue()[0];
            double sumVerificada = entry.getValue()[1];
            
            // Aqui aplicamos a regra do ONS (Submódulo 6.5-PR): max(0, somaEstimada - somaVerificada) no conjunto
            double corteNoBloco = Math.max(0, sumEstimada - sumVerificada) * 0.5; // MWmed * 0.5h = MWh
            
            if (corteNoBloco > 0) {
                String chaveDia = nomeUsina + "|" + dia;
                double[] acumulado = agregadoPorUsinaEDia.computeIfAbsent(chaveDia, k -> new double[2]);
                acumulado[0] += corteNoBloco;
                acumulado[1] += 1; // 1 bloco com corte
            }
        }

        int registrosGravados = 0;
        for (Map.Entry<String, double[]> entrada : agregadoPorUsinaEDia.entrySet()) {
            String[] partes = entrada.getKey().split("\\|");
            String nomeUsina = partes[0];
            LocalDate dia = LocalDate.parse(partes[1]);
            double somaCorteMwh = entrada.getValue()[0];
            int blocos = (int) entrada.getValue()[1];

            Usina usina = cacheUsinaPorNome.computeIfAbsent(nomeUsina, this::resolverUsinaPorNome);
            if (usina == null) {
                System.err.println("Nenhuma Usina cadastrada localmente casa com '" + nomeUsina
                        + "' - cadastre-a antes de importar, ou ajuste o matching.");
                continue;
            }

            HistoricoCorteOns corte = new HistoricoCorteOns();
            corte.setUsina(usina);
            corte.setDataCorte(LocalDateTime.of(dia, LocalTime.NOON));
            corte.setDuracaoMinutos(blocos * 30);
            corte.setEnergiaCortadaMwh(BigDecimal.valueOf(somaCorteMwh));
            corte.setMotivoOns("ONS_DETALHAMENTO_USINA_SEM_MOTIVO");

            corteService.registrarCorte(corte);
            registrosGravados++;
        }

        System.out.println("Backfill de " + mes + "/" + ano + " concluído: " + registrosGravados + " registros diários gravados.");
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

    private boolean bateComAlvo(String nomeUsina) {
        String upper = nomeUsina.toUpperCase();
        for (String alvo : NOMES_CONJUNTO_ALVO) {
            if (upper.contains(alvo.toUpperCase())) return true;
        }
        return false;
    }

    private double parseDecimalBr(String valor) {
        if (valor == null || valor.isBlank()) return 0.0;
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // AQUI ESTÁ A LÓGICA CORRIGIDA QUE DIRECIONA PARA OS IDs 1, 2 E 3
    private Usina resolverUsinaPorNome(String nomeOns) {
        String upper = nomeOns.toUpperCase();
        
        List<Usina> todasUsinas = usinaRepository.findAll();
        
        if (upper.contains("IGAPORÃ III")) {
            return todasUsinas.stream().filter(u -> u.getNome().contains("III")).findFirst().orElse(null);
        } else if (upper.contains("CAETITÉ 123") || upper.contains("IGAPORÃ II")) {
            return todasUsinas.stream().filter(u -> u.getNome().contains("II") && !u.getNome().contains("III")).findFirst().orElse(null);
        } else {
            return todasUsinas.stream().filter(u -> u.getNome().contains("I") && !u.getNome().contains("II")).findFirst().orElse(null);
        }
    }
}
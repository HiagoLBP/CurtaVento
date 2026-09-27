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

    private static final String DELIMITADOR = ";";

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

        Map<String, double[]> agregadoPorInstante = new HashMap<>(); // Chave: usinaId|din_instante
        Map<String, Usina> cacheUsinaPorNome = new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.execute(url, HttpMethod.GET, null, response -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {

                String headerLine = reader.readLine();
                if (headerLine == null) return null;
                Map<String, Integer> idx = mapearColunas(headerLine);

                String linha;
                long totalLinhas = 0;

                while ((linha = reader.readLine()) != null) {
                    totalLinhas++;
                    String[] col = linha.split(DELIMITADOR, -1);
                    if (col.length < idx.size()) continue;

                    String estado = col[idx.get("id_estado")].trim();
                    if (!"BA".equalsIgnoreCase(estado)) continue;

                    // AGORA LÊ A USINA EXATA, E NÃO O CONJUNTO!
                    String nomeUsina = col[idx.get("nom_usina")].trim();

                    String flgRestrita = col[idx.get("flg_geracaorestrita")].trim();
                    if (!"1".equals(flgRestrita)) continue;

                    double estimada = parseDecimalBr(col[idx.get("val_geracaoestimada")]);
                    double verificada = parseDecimalBr(col[idx.get("val_geracaoverificada")]);
                    String instanteStr = col[idx.get("din_instante")].trim();

                    // Agrupamento NÍVEL 1: Por Usina Exata
                    Usina usinaDb = cacheUsinaPorNome.computeIfAbsent(nomeUsina, this::resolverUsinaPorNome);
                    if (usinaDb == null) continue;

                    String chaveInstante = usinaDb.getId() + "|" + instanteStr;
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
            Long usinaId = Long.parseLong(partes[0]);
            
            LocalDateTime instante = LocalDateTime.parse(partes[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDate dia = instante.toLocalDate();
            
            double sumEstimada = entry.getValue()[0];
            double sumVerificada = entry.getValue()[1];
            
            double corteNoBloco = Math.max(0, sumEstimada - sumVerificada) * 0.5; // MWmed * 0.5h = MWh
            
            if (corteNoBloco > 0) {
                String chaveDia = usinaId + "|" + dia;
                double[] acumulado = agregadoPorUsinaEDia.computeIfAbsent(chaveDia, k -> new double[2]);
                acumulado[0] += corteNoBloco;
                acumulado[1] += 1; // 1 bloco com corte
            }
        }

        int registrosGravados = 0;
        for (Map.Entry<String, double[]> entrada : agregadoPorUsinaEDia.entrySet()) {
            String[] partes = entrada.getKey().split("\\|");
            Long usinaId = Long.parseLong(partes[0]);
            LocalDate dia = LocalDate.parse(partes[1]);
            double somaCorteMwh = entrada.getValue()[0];
            int blocos = (int) entrada.getValue()[1];

            Usina usina = usinaRepository.findById(usinaId).orElse(null);
            if (usina == null) continue;

            HistoricoCorteOns corte = new HistoricoCorteOns();
            corte.setUsina(usina);
            corte.setDataCorte(LocalDateTime.of(dia, LocalTime.NOON));
            corte.setDuracaoMinutos(blocos * 30);
            corte.setEnergiaCortadaMwh(BigDecimal.valueOf(somaCorteMwh));
            corte.setMotivoOns("ONS_USINA_INDIVIDUAL");

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

    private double parseDecimalBr(String valor) {
        if (valor == null || valor.isBlank()) return 0.0;
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private List<Usina> cacheTodasUsinas = null;

    private String removerAcentos(String str) {
        if (str == null) return "";
        return java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private Usina resolverUsinaPorNome(String nomeOns) {
        if (cacheTodasUsinas == null) {
            cacheTodasUsinas = usinaRepository.findAll();
        }
        
        String cleanOns = removerAcentos(nomeOns.toUpperCase())
                .replace("EOLICA", "")
                .replace("CENTRAL", "")
                .replace("GERADORA", "")
                
                .replace("KV", "")
                .trim();
        
        String[] palavrasOns = cleanOns.split("\\s+");
        
        Usina melhorUsina = null;
        int maxScore = 0;
        
        for (Usina u : cacheTodasUsinas) {
            String cleanDb = removerAcentos(u.getNome().toUpperCase());
            String cleanOp = removerAcentos(u.getOperador() != null ? u.getOperador().toUpperCase() : "");
            
            int score = 0;
            for (String p : palavrasOns) {
                if (p.length() <= 2) {
                    // permite algarismos romanos ex: I, II, III, IV se forem exatos
                    if (!p.matches("I|II|III|IV|V|VI|VII|VIII|IX|X|[0-9]+")) continue;
                }
                if (p.equals("DE") || p.equals("DA") || p.equals("DO") || p.equals("DOS") || p.equals("DAS")) continue;
                
                // Correspondência parcial ganha pontos básicos
                if (cleanDb.contains(p)) score += 2;
                
                // Correspondência exata da palavra (usando regex \b) ganha mega bonus
                if (cleanDb.matches(".*\\b" + p + "\\b.*")) score += 10;
                if (cleanOp.matches(".*\\b" + p + "\\b.*")) score += 5;
            }
            
            if (score > maxScore && score > 0) {
                maxScore = score;
                melhorUsina = u;
            }
        }
        
        return melhorUsina;
    }
}

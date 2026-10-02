package com.curtavento.curta_vento.infrastructure.ons;

import com.curtavento.curta_vento.application.usecase.HistoricoCorteOnsService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
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
    private final SpringDataUsinaRepository springDataUsinaRepository;

    @Autowired
    public BackfillOnsService(HistoricoCorteOnsService corteService, SpringDataUsinaRepository springDataUsinaRepository) {
        this.corteService = corteService;
        this.springDataUsinaRepository = springDataUsinaRepository;
    }

    public int baixarEProcessarMes(int ano, int mes) {
        String url = String.format(URL_BASE, ano, mes);
        System.out.println("Baixando: " + url);

        Map<String, double[]> agregadoPorInstante = new HashMap<>();
        Map<String, UsinaEntity> cacheUsinaPorNome = new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.execute(url, HttpMethod.GET, null, response -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {

                String headerLine = reader.readLine();
                if (headerLine == null) return null;

                Map<String, Integer> colunas = mapearColunas(headerLine);
                int idxEstado = colunas.getOrDefault("id_estado", -1);
                int idxUsina = colunas.getOrDefault("nom_usina", -1);
                int idxInstante = colunas.getOrDefault("din_instante", -1);
                int idxGeracao = colunas.getOrDefault("val_geracao", -1);
                int idxCorte = colunas.getOrDefault("val_geracaolimitada", -1);

                if (idxUsina == -1 || idxCorte == -1) {
                    System.err.println("Colunas obrigatórias não encontradas no CSV do ONS.");
                    return null;
                }

                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (linha.isBlank()) continue;
                    String[] campos = linha.split(DELIMITADOR, -1);

                    if (idxEstado >= 0 && idxEstado < campos.length) {
                        String uf = campos[idxEstado].trim().replace("\"", "");
                        if (!"BA".equalsIgnoreCase(uf)) continue;
                    }

                    String nomeUsinaCsv = campos[idxUsina].trim().replace("\"", "");
                    UsinaEntity usina = cacheUsinaPorNome.computeIfAbsent(nomeUsinaCsv, this::buscarUsinaPorNome);
                    if (usina == null) continue;

                    String instante = idxInstante >= 0 && idxInstante < campos.length
                            ? campos[idxInstante].trim().replace("\"", "") : "";

                    double valCorte = idxCorte < campos.length ? parseDecimalBr(campos[idxCorte]) : 0.0;
                    double valGeracao = (idxGeracao >= 0 && idxGeracao < campos.length)
                            ? parseDecimalBr(campos[idxGeracao]) : 0.0;

                    if (valCorte <= 0.0) continue;

                    String chaveInstante = usina.getId() + "|" + instante;
                    agregadoPorInstante.merge(chaveInstante,
                            new double[]{valCorte, valGeracao},
                            (a, b) -> new double[]{a[0] + b[0], a[1] + b[1]});
                }
            }
            return null;
        });

        System.out.println("Processamento concluído. Agregando por dia para salvar no banco...");

        Map<String, double[]> agregadoPorDia = new HashMap<>();
        DateTimeFormatter parserInstante = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Map.Entry<String, double[]> entry : agregadoPorInstante.entrySet()) {
            String[] partes = entry.getKey().split("\\|");
            Long usinaId = Long.parseLong(partes[0]);
            String instanteStr = partes[1];

            LocalDate data;
            try {
                data = LocalDateTime.parse(instanteStr, parserInstante).toLocalDate();
            } catch (Exception e) {
                try {
                    data = LocalDate.parse(instanteStr.substring(0, 10));
                } catch (Exception e2) {
                    continue;
                }
            }

            double cortePotenciaMw = entry.getValue()[0];
            double corteEnergiaMwh = cortePotenciaMw * 0.5;

            String chaveDia = usinaId + "|" + data;
            agregadoPorDia.merge(chaveDia,
                    new double[]{corteEnergiaMwh, 1.0},
                    (a, b) -> new double[]{a[0] + b[0], a[1] + b[1]});
        }

        int registrosGravados = 0;
        for (Map.Entry<String, double[]> entry : agregadoPorDia.entrySet()) {
            String[] partes = entry.getKey().split("\\|");
            Long usinaId = Long.parseLong(partes[0]);
            LocalDate dia = LocalDate.parse(partes[1]);
            double somaCorteMwh = entry.getValue()[0];
            int blocos = (int) entry.getValue()[1];

            UsinaEntity usina = springDataUsinaRepository.findById(usinaId).orElse(null);
            if (usina == null) continue;

            HistoricoCorteOnsEntity corte = new HistoricoCorteOnsEntity();
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

    private List<UsinaEntity> cacheTodasUsinas = null;
    private String cleanName(String str) {
        if (str == null) return "";
        return java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD)
                .replaceAll("\\\\p{M}", "")
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "");
    }

    private UsinaEntity buscarUsinaPorNome(String nomeCsv) {
        if (cacheTodasUsinas == null) {
            cacheTodasUsinas = springDataUsinaRepository.findAll();
        }
        String nomeLimpoCsv = cleanName(nomeCsv);
        if (nomeLimpoCsv.isEmpty()) return null;

        for (UsinaEntity u : cacheTodasUsinas) {
            String nomeLimpoDb = cleanName(u.getNome());
            if (nomeLimpoDb.equals(nomeLimpoCsv)) return u;
            if (nomeLimpoDb.startsWith(nomeLimpoCsv) || nomeLimpoCsv.startsWith(nomeLimpoDb)) return u;
        }
        return null;
    }
}
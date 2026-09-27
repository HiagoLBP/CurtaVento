package com.curtavento.curta_vento.geracao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class GeracaoOnsHorariaService {

    private static final String FONTE = "ONS_GERACAO_HORARIA";
    private static final DateTimeFormatter[] FORMATOS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ROOT),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ROOT)
    };

    private final GeracaoOnsHorariaRepository repository;

    public GeracaoOnsHorariaService(GeracaoOnsHorariaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImportResult importar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo de geração horária do ONS é obrigatório.");
        }

        int lidas = 0;
        int importadas = 0;
        int rejeitadas = 0;
        Map<String, Integer> colunas = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            boolean cabecalho = true;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) {
                    mapearColunas(linha, colunas);
                    cabecalho = false;
                    continue;
                }
                lidas++;
                try {
                    GeracaoOnsHoraria registro = converter(linha.split(";", -1), colunas);
                    GeracaoOnsHoraria existente = repository
                            .findByIdOnsAndInstanteAndFonte(registro.getIdOns(), registro.getInstante(), FONTE)
                            .orElse(null);
                    if (existente == null) {
                        repository.save(registro);
                    } else {
                        existente.setNomeUsinaOns(registro.getNomeUsinaOns());
                        existente.setGeracaoMwh(registro.getGeracaoMwh());
                        repository.save(existente);
                    }
                    importadas++;
                } catch (RuntimeException ex) {
                    rejeitadas++;
                }
            }
        }
        return new ImportResult(lidas, importadas, rejeitadas);
    }

    private GeracaoOnsHoraria converter(String[] campos, Map<String, Integer> colunas) {
        String idOns = texto(campos, colunas, "ID_ONS");
        String nome = texto(campos, colunas, "NOM_USINA");
        LocalDateTime instante = data(texto(campos, colunas, "DIN_INSTANTE"));
        BigDecimal geracao = decimal(texto(campos, colunas, "VAL_GERACAO"));
        if (geracao.signum() < 0) {
            throw new IllegalArgumentException("Geração negativa.");
        }

        GeracaoOnsHoraria registro = new GeracaoOnsHoraria();
        registro.setIdOns(idOns);
        registro.setNomeUsinaOns(nome);
        registro.setInstante(instante);
        registro.setGeracaoMwh(geracao);
        registro.setFonte(FONTE);
        return registro;
    }

    private void mapearColunas(String linha, Map<String, Integer> colunas) {
        String[] cabecalho = linha.replace("\uFEFF", "").split(";", -1);
        for (int i = 0; i < cabecalho.length; i++) {
            colunas.put(cabecalho[i].trim().toUpperCase(Locale.ROOT), i);
        }
        for (String obrigatoria : new String[]{"ID_ONS", "NOM_USINA", "DIN_INSTANTE", "VAL_GERACAO"}) {
            if (!colunas.containsKey(obrigatoria)) {
                throw new IllegalArgumentException("Coluna obrigatória ausente: " + obrigatoria);
            }
        }
    }

    private String texto(String[] campos, Map<String, Integer> colunas, String coluna) {
        String valor = campos[colunas.get(coluna)].trim();
        if (valor.isBlank()) throw new IllegalArgumentException("Campo vazio: " + coluna);
        return valor;
    }

    private LocalDateTime data(String valor) {
        for (DateTimeFormatter formato : FORMATOS) {
            try {
                return LocalDateTime.parse(valor, formato);
            } catch (RuntimeException ignored) {
            }
        }
        throw new IllegalArgumentException("Instante inválido: " + valor);
    }

    private BigDecimal decimal(String valor) {
        String normalizado = valor.trim();
        if (normalizado.contains(",") && normalizado.contains(".")) {
            normalizado = normalizado.replace(".", "").replace(",", ".");
        } else if (normalizado.contains(",")) {
            normalizado = normalizado.replace(",", ".");
        }
        return new BigDecimal(normalizado);
    }

    
    @Transactional
    public ImportResult importarViaHttp(int ano, int mes) throws IOException {
        String urlString = String.format("https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/geracao_usina_2_ho/GERACAO_USINA-2_%d_%02d.csv", ano, mes);
        System.out.println("Baixando geração horária do ONS: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000); // 15s
        conn.setReadTimeout(120000); // 120s
        conn.setRequestMethod("GET");
        
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Erro ao acessar URL do ONS. Status HTTP: " + status);
        }

        int lidas = 0;
        int importadas = 0;
        int rejeitadas = 0;
        Map<String, Integer> colunas = new HashMap<>();

        try (InputStream is = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String linha;
            boolean cabecalho = true;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) {
                    mapearColunas(linha, colunas);
                    cabecalho = false;
                    continue;
                }
                
                // Filtrar apenas o estado da Bahia (BA) para não entupir o banco de dados atoa
                String[] campos = linha.split(";", -1);
                Integer idxEstado = colunas.get("ID_ESTADO");
                if (idxEstado != null && campos.length > idxEstado) {
                    if (!"BA".equalsIgnoreCase(campos[idxEstado].trim())) {
                        continue;
                    }
                }
                
                lidas++;
                try {
                    GeracaoOnsHoraria registro = converter(campos, colunas);
                    GeracaoOnsHoraria existente = repository
                            .findByIdOnsAndInstanteAndFonte(registro.getIdOns(), registro.getInstante(), FONTE)
                            .orElse(null);
                    if (existente == null) {
                        repository.save(registro);
                    } else {
                        existente.setNomeUsinaOns(registro.getNomeUsinaOns());
                        existente.setGeracaoMwh(registro.getGeracaoMwh());
                        repository.save(existente);
                    }
                    importadas++;
                } catch (RuntimeException ex) {
                    rejeitadas++;
                }
                
                // Flush para log opcional se demorar muito
                if (lidas % 10000 == 0) {
                    System.out.println("Processadas " + lidas + " linhas da BA...");
                }
            }
        }
        
        System.out.println("Importação automática concluída: " + importadas + " importadas, " + rejeitadas + " rejeitadas.");
        return new ImportResult(lidas, importadas, rejeitadas);
    }

    public record ImportResult(int lidas, int importadas, int rejeitadas) {}
}

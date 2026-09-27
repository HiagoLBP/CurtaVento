import os
import re

filepath = r"C:\Users\Hiago\.copilot\repos\copilot-worktrees\curta-vento\hiagolbp-fantastic-doodle\src\main\java\com\curtavento\curta_vento\geracao\GeracaoOnsHorariaService.java"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add imports for URL and InputStream
imports_to_add = """
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
"""

content = content.replace("import org.springframework.web.multipart.MultipartFile;", "import org.springframework.web.multipart.MultipartFile;" + imports_to_add)

# Add the new method
new_method = """
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
"""

content = content.replace("public record ImportResult(int lidas, int importadas, int rejeitadas) {}", new_method + "\n    public record ImportResult(int lidas, int importadas, int rejeitadas) {}")

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.corte.HistoricoCorteOns;
import com.curtavento.curta_vento.corte.HistoricoCorteOnsService;
import com.curtavento.curta_vento.usina.Usina;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class IngestaoCortesOnsJob {

    private final HistoricoCorteOnsService corteService;

    public IngestaoCortesOnsJob(HistoricoCorteOnsService corteService) {
        this.corteService = corteService;
    }

    @Scheduled(fixedRate = 300000)
    public void buscarDadosONS() {
        System.out.println("JOB ATIVO: A ler o CSV oficial do ONS...");
        int linhasSalvas = 0;

        try {
            ClassPathResource resource = new ClassPathResource("dados/RESTRICAO_COFF_EOLICA_2026_09.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));

            String linha;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Pula o cabeçalho automaticamente
            reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] colunas = linha.split(";", -1);

                // 1. Garante que é uma linha de dados válida
                if (colunas.length < 24) continue;

                // 2. Extrai o nome da usina (Coluna 4 no CSV do ONS)
                String nomUsina = colunas[4];

                // 3. O FILTRO DO ALTO SERTÃO: Se a usina não for da região, ignora a linha e passa à próxima
                if (!nomUsina.contains("Caetité") && !nomUsina.contains("Alto Sertão") && !nomUsina.contains("Igaporã") && !nomUsina.contains("Guanambi")) {
                    continue;
                }

                HistoricoCorteOns corte = new HistoricoCorteOns();

                // 4. Roteamento inteligente para os IDs reais do seu banco de dados
                Long usinaId = 1L; // Padrão: Alto Sertão I (ex: "Conj. Caetité")

                if (nomUsina.contains("Caetité 123") || nomUsina.contains("Alto Sertão II")) {
                    usinaId = 2L;
                } else if (nomUsina.contains("Alto Sertão III") || nomUsina.contains("Igaporã")) {
                    usinaId = 3L;
                }

                Usina usina = new Usina();
                usina.setId(usinaId);
                corte.setUsina(usina);

                // Coluna 7: Data
                corte.setDataCorte(LocalDateTime.parse(colunas[7], formatter));

                // Coluna 19: Energia Cortada (GNRa) - Substitui a vírgula brasileira por ponto
                String energiaStr = colunas[19].replace(",", ".");
                corte.setEnergiaCortadaMwh(energiaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(energiaStr));

                // Coluna 23: Minutos
                String duracaoStr = colunas[23];
                corte.setDuracaoMinutos(duracaoStr.isEmpty() ? 0 : Integer.parseInt(duracaoStr));

                // Coluna 15: Motivo
                corte.setMotivoOns(colunas[15].isEmpty() ? "Sem justificativa" : colunas[15]);

                corteService.registrarCorte(corte);
                linhasSalvas++;
            }
            reader.close();

            // O momento da verdade no seu terminal:
            System.out.println("✅ SUCESSO ABSOLUTO: " + linhasSalvas + " eventos reais do ONS guardados no banco!");

        } catch (Exception e) {
            System.err.println("Erro ao processar o CSV: " + e.getMessage());
        }
    }
}
package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.corte.HistoricoCorteOns;
import com.curtavento.curta_vento.corte.HistoricoCorteOnsService;
import com.curtavento.curta_vento.usina.Usina;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class IngestaoCortesOnsJob {

    private final HistoricoCorteOnsService corteService;
    private final RestTemplate restTemplate;

    public IngestaoCortesOnsJob(HistoricoCorteOnsService corteService) {
        this.corteService = corteService;
        this.restTemplate = new RestTemplate();
    }

    // Cron configurado para rodar todos os dias às 02:00 da manhã, conforme o documento
    @Scheduled(cron = "0 0 2 * * ?")
    public void buscarDadosONS() {
        System.out.println("JOB ATIVO: A procurar dados de curtailment no portal do ONS...");

        try {
            // URL de exemplo da API CKAN do Portal de Dados Abertos do ONS
            // (Na prática, a query do CKAN exige os parâmetros de resource_id)
            String url = "https://dados.ons.org.br/api/3/action/datastore_search?resource_id=SEU_RESOURCE_ID&limit=5";

            Map<?, ?> respostaJson = restTemplate.getForObject(url, Map.class);

            if (respostaJson != null && respostaJson.containsKey("result")) {
                Map<?, ?> result = (Map<?, ?>) respostaJson.get("result");
                java.util.List<Map<?, ?>> records = (java.util.List<Map<?, ?>>) result.get("records");

                for (Map<?, ?> record : records) {
                    HistoricoCorteOns corte = new HistoricoCorteOns();

                    Usina usina = new Usina();
                    usina.setId(1L); // Fixo para o MVP do Alto Sertão I
                    corte.setUsina(usina);

                    // Mapeamento dos dados do ONS
                    if (record.get("val_energia_cortada_mwh") != null) {
                        corte.setEnergiaCortadaMwh(new BigDecimal(record.get("val_energia_cortada_mwh").toString()));
                    }
                    if (record.get("val_duracao_minutos") != null) {
                        corte.setDuracaoMinutos(Integer.parseInt(record.get("val_duracao_minutos").toString()));
                    }
                    if (record.get("nom_motivo_corte") != null) {
                        corte.setMotivoOns(record.get("nom_motivo_corte").toString());
                    }

                    corte.setDataCorte(LocalDateTime.now().minusDays(1)); // Referente ao dia anterior

                    corteService.registrarCorte(corte);
                }
                System.out.println("Sucesso: Lote de curtailment do ONS gravado na base de dados.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao comunicar com o ONS: " + e.getMessage());
        }
    }
}
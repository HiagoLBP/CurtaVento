package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaService;
import com.curtavento.curta_vento.usina.Usina;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class IngestaoDadosClimaticosJob {

    private final LeituraClimaticaService leituraService;
    private final RestTemplate restTemplate;

    public IngestaoDadosClimaticosJob(LeituraClimaticaService leituraService) {
        this.leituraService = leituraService;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 60000)
    public void buscarDadosINMET() {
        System.out.println("JOB ATIVO: A procurar dados reais no INMET...");

        try {
            String hoje = LocalDate.now().toString();
            String url = "https://apitempo.inmet.gov.br/estacao/" + hoje + "/" + hoje + "/A408";

            Map<?, ?>[] respostaJson = restTemplate.getForObject(url, Map[].class);

            if (respostaJson != null && respostaJson.length > 0) {

                Map<?, ?> ultimaLeitura = respostaJson[respostaJson.length - 1];

                LeituraClimatica leitura = new LeituraClimatica();

                Usina usina = new Usina();
                usina.setId(1L);
                leitura.setUsina(usina);

                // Procuramos a chave "VEN_VEL" no dicionário
                Object ventoObj = ultimaLeitura.get("VEN_VEL");
                if (ventoObj != null && !ventoObj.toString().equals("null")) {
                    leitura.setVelocidadeVentoMs(new BigDecimal(ventoObj.toString()));
                }

                // Procuramos a chave "TEM_INS"
                Object tempObj = ultimaLeitura.get("TEM_INS");
                if (tempObj != null && !tempObj.toString().equals("null")) {
                    leitura.setTemperaturaC(new BigDecimal(tempObj.toString()));
                }

                leitura.setFonte("INMET_API");
                leitura.setDataHora(LocalDateTime.now());

                leituraService.salvar(leitura);
                System.out.println("Sucesso: Leitura do INMET gravada na base de dados.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao comunicar com o INMET: " + e.getMessage());
        }
    }
}
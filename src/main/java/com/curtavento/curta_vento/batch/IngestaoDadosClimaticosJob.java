package com.curtavento.curta_vento.batch;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaService;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IngestaoDadosClimaticosJob {

    private static final Logger logger = LoggerFactory.getLogger(IngestaoDadosClimaticosJob.class);

    private final LeituraClimaticaService leituraService;
    private final UsinaRepository usinaRepository;
    private final RestTemplate restTemplate;

    public IngestaoDadosClimaticosJob(LeituraClimaticaService leituraService, UsinaRepository usinaRepository) {
        this.leituraService = leituraService;
        this.usinaRepository = usinaRepository;
        this.restTemplate = new RestTemplate();
    }

    // Para testes: roda 4 segundos após iniciar a aplicação, e depois 1x por dia.
    @Scheduled(initialDelay = 4000, fixedDelay = 86400000)
    public void buscarDadosINMET() {
        logger.info("JOB ATIVO: A procurar dados climáticos...");

        BigDecimal velocidadeVentoInmet = null;
        BigDecimal temperaturaInmet = null;
        boolean inmetSucesso = false;

        try {
            Map<String, BigDecimal> dadosInmet = fetchInmetData();
            velocidadeVentoInmet = dadosInmet.get("vento");
            temperaturaInmet = dadosInmet.get("temperatura");
            inmetSucesso = true;
        } catch (Exception e) {
            logger.warn("Falha no INMET após tentativas. Utilizando fallback (Open-Meteo ou Cache). Erro: {}", e.getMessage());
        }

        List<Usina> usinas = usinaRepository.findAll();
        for (Usina usina : usinas) {
            BigDecimal vento = null;
            BigDecimal temp = null;
            String fonte = null;

            if (inmetSucesso && velocidadeVentoInmet != null) {
                vento = velocidadeVentoInmet;
                temp = temperaturaInmet;
                fonte = "API INMET (A426)";
            } else {
                try {
                    Map<String, BigDecimal> dadosOpenMeteo = fetchOpenMeteoData(usina);
                    vento = dadosOpenMeteo.get("vento");
                    temp = dadosOpenMeteo.get("temperatura");
                    fonte = "API Open-Meteo";
                } catch (Exception ex) {
                    logger.error("Falha no Open-Meteo para a usina ID {}. Buscando no cache. Erro: {}", usina.getId(), ex.getMessage());
                }

                if (vento == null) {
                    LeituraClimatica ultima = leituraService.buscarUltimaLeitura(usina.getId());
                    if (ultima != null) {
                        vento = ultima.getVelocidadeVentoMs();
                        temp = ultima.getTemperaturaC();
                        fonte = "CACHE_ANTIGO";
                    } else {
                        logger.error("Nenhuma leitura anterior encontrada para usina ID {}. Usando valores zerados.", usina.getId());
                        vento = BigDecimal.ZERO;
                        temp = BigDecimal.ZERO;
                        fonte = "DEFAULT_ZERADO";
                    }
                }
            }

            LeituraClimatica leitura = new LeituraClimatica();
            leitura.setUsina(usina);
            leitura.setVelocidadeVentoMs(vento);
            leitura.setTemperaturaC(temp != null ? temp : BigDecimal.ZERO);
            leitura.setFonte(fonte);
            leitura.setDataHora(LocalDateTime.now());

            leituraService.salvar(leitura);
            logger.info("Clima atualizado: Usina {} - Vento: {} - Fonte: {}", usina.getId(), vento, fonte);
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public Map<String, BigDecimal> fetchInmetData() {
        logger.info("Tentando buscar dados no INMET...");
        String hoje = LocalDate.now().toString();
        String ontem = LocalDate.now().minusDays(1).toString();
        String url = "https://apitempo.inmet.gov.br/estacao/" + ontem + "/" + hoje + "/A426";

        Map<?, ?>[] respostaJson = restTemplate.getForObject(url, Map[].class);

        BigDecimal velocidadeVento = null;
        BigDecimal temperatura = null;

        if (respostaJson != null && respostaJson.length > 0) {
            for (int i = respostaJson.length - 1; i >= 0; i--) {
                Map<?, ?> leitura = respostaJson[i];
                Object ventoObj = leitura.get("VEN_VEL");

                if (ventoObj != null && !ventoObj.toString().equals("null")) {
                    velocidadeVento = new BigDecimal(ventoObj.toString());

                    Object tempObj = leitura.get("TEM_INS");
                    if (tempObj != null && !tempObj.toString().equals("null")) {
                        temperatura = new BigDecimal(tempObj.toString());
                    }
                    break;
                }
            }
        }

        if (velocidadeVento == null) {
            throw new RuntimeException("Dados do INMET retornaram vazios ou nulos.");
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("vento", velocidadeVento);
        result.put("temperatura", temperatura != null ? temperatura : BigDecimal.ZERO);
        return result;
    }

    private Map<String, BigDecimal> fetchOpenMeteoData(Usina usina) {
        if (usina.getLatitude() == null || usina.getLongitude() == null) {
            throw new RuntimeException("Usina sem coordenadas para buscar no Open-Meteo.");
        }

        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m,wind_speed_10m&wind_speed_unit=ms",
                usina.getLatitude(), usina.getLongitude());

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("current")) {
            Map<?, ?> current = (Map<?, ?>) response.get("current");
            
            Object windObj = current.get("wind_speed_10m");
            Object tempObj = current.get("temperature_2m");
            
            BigDecimal vento = windObj != null ? new BigDecimal(windObj.toString()) : null;
            BigDecimal temp = tempObj != null ? new BigDecimal(tempObj.toString()) : null;
            
            if (vento != null) {
                Map<String, BigDecimal> result = new HashMap<>();
                result.put("vento", vento);
                result.put("temperatura", temp != null ? temp : BigDecimal.ZERO);
                return result;
            }
        }
        throw new RuntimeException("Dados do Open-Meteo inválidos.");
    }
}
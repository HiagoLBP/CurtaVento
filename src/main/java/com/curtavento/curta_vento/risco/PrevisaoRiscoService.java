package com.curtavento.curta_vento.risco;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.usina.Usina;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrevisaoRiscoService {

    private final PrevisaoRiscoRepository repository;

    public PrevisaoRiscoService(PrevisaoRiscoRepository repository) {
        this.repository = repository;
    }

    public List<PrevisaoRisco> buscarPorUsina(Long usinaId) {
        return repository.findByUsinaId(usinaId);
    }

    public PrevisaoRisco calcularRisco(Usina usina, LeituraClimatica ultimaLeitura, int horizonteHoras) {
        LocalDate hoje = LocalDate.now();
        PrevisaoRisco previsao = repository.findFirstByUsinaIdAndDataReferencia(usina.getId(), hoje);
        
        if (previsao == null) {
            previsao = new PrevisaoRisco();
            previsao.setUsina(usina);
            previsao.setDataReferencia(hoje);
        }
        previsao.setHorizonteHoras(horizonteHoras);
        previsao.setCriadoEm(LocalDateTime.now());

        int scoreRisco = 0;
        StringBuilder justificativa = new StringBuilder();

        if (ultimaLeitura != null && ultimaLeitura.getVelocidadeVentoMs() != null) {
            double vento = ultimaLeitura.getVelocidadeVentoMs().doubleValue();

            if (vento > 12.0) {
                scoreRisco += 50;
                justificativa.append("Vento previsto elevado (").append(vento).append(" m/s) indica saturação da rede. ");
            } else if (vento > 8.0) {
                scoreRisco += 25;
                justificativa.append("Vento moderado/alto. ");
            } else {
                justificativa.append("Vento dentro da normalidade operacional. ");
            }
        } else {
            justificativa.append("Sem dados de vento recentes para avaliar. ");
        }

        scoreRisco += 20;
        justificativa.append("Fator sazonal histórico aplicado (simplificado).");

        if (scoreRisco > 100) scoreRisco = 100;

        if (scoreRisco <= 29) {
            previsao.setNivelRisco(NivelRisco.BAIXO);
            previsao.setPercentualCorteEstimado(new BigDecimal("0.0"));
        } else if (scoreRisco <= 59) {
            previsao.setNivelRisco(NivelRisco.MEDIO);
            previsao.setPercentualCorteEstimado(new BigDecimal("15.0"));
        } else if (scoreRisco <= 79) {
            previsao.setNivelRisco(NivelRisco.ALTO);
            previsao.setPercentualCorteEstimado(new BigDecimal("40.0"));
        } else {
            previsao.setNivelRisco(NivelRisco.CRITICO);
            previsao.setPercentualCorteEstimado(new BigDecimal("80.0"));
        }

        previsao.setFatoresJustificativa(justificativa.toString());

        return repository.save(previsao);
    }
}
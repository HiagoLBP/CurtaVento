package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.NivelRisco;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrevisaoRiscoService {

    private final SpringDataPrevisaoRiscoRepository riscoRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;
    private final Clock clock;

    @Autowired
    public PrevisaoRiscoService(SpringDataPrevisaoRiscoRepository riscoRepository,
                                SpringDataHistoricoCorteOnsRepository corteRepository) {
        this(riscoRepository, corteRepository, Clock.systemDefaultZone());
    }

    public PrevisaoRiscoService(SpringDataPrevisaoRiscoRepository riscoRepository,
                                SpringDataHistoricoCorteOnsRepository corteRepository,
                                Clock clock) {
        this.riscoRepository = riscoRepository;
        this.corteRepository = corteRepository;
        this.clock = clock;
    }

    public List<PrevisaoRiscoEntity> buscarPorUsina(Long usinaId) {
        return riscoRepository.findByUsinaId(usinaId);
    }

    public PrevisaoRiscoEntity calcularRisco(UsinaEntity usina, LeituraClimaticaEntity ultimaLeitura, int horizonteHoras) {
        LocalDate hoje = LocalDate.now(clock);
        LocalDateTime agora = LocalDateTime.now(clock);
        PrevisaoRiscoEntity previsao = riscoRepository.findFirstByUsinaIdAndDataReferencia(usina.getId(), hoje);

        if (previsao == null) {
            previsao = new PrevisaoRiscoEntity();
            previsao.setUsina(usina);
            previsao.setDataReferencia(hoje);
        }
        previsao.setHorizonteHoras(horizonteHoras);
        previsao.setCriadoEm(agora);

        int scoreRisco = 0;
        StringBuilder justificativa = new StringBuilder();

        if (ultimaLeitura != null && ultimaLeitura.getVentoPrevisao24hMs() != null) {
            double vento = ultimaLeitura.getVentoPrevisao24hMs().doubleValue();

            // Calibrado para vento a 80m de altura
            if (vento >= 10.0) {
                scoreRisco += 40;
                justificativa.append("Vento a 80m previsto elevado (>= 10 m/s), alta geração agrava risco de restrição (Peso: 40). ");
            } else if (vento >= 7.0) {
                scoreRisco += 20;
                justificativa.append("Vento a 80m moderado (>= 7 m/s) (Peso: 20). ");
            } else {
                justificativa.append("Vento a 80m baixo, menor probabilidade de corte por excesso de geração. ");
            }
        } else {
            justificativa.append("Sem dados de vento recentes para compor o score. ");
        }

        // Fator de Tendência Recente (Últimos 15 dias)
        LocalDateTime inicioTendencia = agora.minusDays(15);
        List<HistoricoCorteOnsEntity> cortesRecentes = corteRepository.findByUsinaIdAndDataCorteBetween(
                usina.getId(), inicioTendencia, agora);

        long diasComCorte = cortesRecentes.stream()
                .map(c -> c.getDataCorte().toLocalDate())
                .distinct()
                .count();

        if (diasComCorte > 7) {
            scoreRisco += 30;
            justificativa.append("Tendência alta: cortes registrados em mais de 7 dos últimos 15 dias (Peso: 30). ");
        } else if (diasComCorte > 2) {
            scoreRisco += 15;
            justificativa.append("Tendência média: alguns cortes nos últimos 15 dias (Peso: 15). ");
        }

        // Fator sazonal (Simplificado para +30 em meses de Safra dos Ventos: Julho a Novembro)
        int mesAtual = hoje.getMonthValue();
        if (mesAtual >= 7 && mesAtual <= 11) {
            scoreRisco += 30;
            justificativa.append("Fator Sazonal: Período histórico de 'safra dos ventos' no NE (Peso: 30). ");
        } else {
            scoreRisco += 10;
            justificativa.append("Fator Sazonal: Fora do pico de geração do NE (Peso: 10). ");
        }

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

        return riscoRepository.save(previsao);
    }
}

package com.curtavento.curta_vento.risco;

import com.curtavento.curta_vento.application.usecase.PrevisaoRiscoService;
import com.curtavento.curta_vento.domain.model.NivelRisco;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrevisaoRiscoServiceTest {

    private static final ZoneId ZONA_BR = ZoneId.of("America/Sao_Paulo");
    private static final Clock CLOCK_SAFRA = Clock.fixed(Instant.parse("2026-08-15T12:00:00Z"), ZONA_BR);
    private static final Clock CLOCK_FORA_SAFRA = Clock.fixed(Instant.parse("2026-03-15T12:00:00Z"), ZONA_BR);

    @Mock
    private SpringDataPrevisaoRiscoRepository repository;

    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    private UsinaEntity usinaMock;
    private LeituraClimaticaEntity leituraMock;

    @BeforeEach
    void setUp() {
        usinaMock = new UsinaEntity();
        usinaMock.setId(10L);

        leituraMock = new LeituraClimaticaEntity();
        leituraMock.setId(1L);

        when(repository.findFirstByUsinaIdAndDataReferencia(eq(10L), any(LocalDate.class))).thenReturn(null);
        when(repository.save(any(PrevisaoRiscoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveCalcularRiscoCriticoParaVentoForteComTendenciaDeCortesNaSafra() {
        PrevisaoRiscoService service = new PrevisaoRiscoService(repository, corteRepository, CLOCK_SAFRA);
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("12.5")); // +40

        List<HistoricoCorteOnsEntity> cortes = List.of(
                criarCorte(LocalDateTime.of(2026, 8, 10, 12, 0)),
                criarCorte(LocalDateTime.of(2026, 8, 11, 12, 0)),
                criarCorte(LocalDateTime.of(2026, 8, 12, 12, 0))
        ); // 3 dias distintos (+15) + safra (+30) = 85
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(cortes);

        PrevisaoRiscoEntity previsao = service.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals(NivelRisco.CRITICO, previsao.getNivelRisco());
        assertEquals(0, new BigDecimal("80.0").compareTo(previsao.getPercentualCorteEstimado()));
    }

    @Test
    void deveCalcularRiscoAltoParaVentoMuitoForteSemCortesRecentesNaSafra() {
        PrevisaoRiscoService service = new PrevisaoRiscoService(repository, corteRepository, CLOCK_SAFRA);
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("12.5")); // +40, safra +30 = 70
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        PrevisaoRiscoEntity previsao = service.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals(NivelRisco.ALTO, previsao.getNivelRisco());
        assertEquals(0, new BigDecimal("40.0").compareTo(previsao.getPercentualCorteEstimado()));
    }

    @Test
    void deveCalcularRiscoMedioParaVentoModeradoNaSafra() {
        PrevisaoRiscoService service = new PrevisaoRiscoService(repository, corteRepository, CLOCK_SAFRA);
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("8.5")); // +20, safra +30 = 50
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        PrevisaoRiscoEntity previsao = service.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals(NivelRisco.MEDIO, previsao.getNivelRisco());
        assertEquals(0, new BigDecimal("15.0").compareTo(previsao.getPercentualCorteEstimado()));
    }

    @Test
    void deveCalcularRiscoBaixoParaVentoFracoForaDaSafra() {
        PrevisaoRiscoService service = new PrevisaoRiscoService(repository, corteRepository, CLOCK_FORA_SAFRA);
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("4.0")); // +0, fora safra +10 = 10
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        PrevisaoRiscoEntity previsao = service.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals(NivelRisco.BAIXO, previsao.getNivelRisco());
        assertEquals(0, new BigDecimal("0.0").compareTo(previsao.getPercentualCorteEstimado()));
    }

    private HistoricoCorteOnsEntity criarCorte(LocalDateTime data) {
        HistoricoCorteOnsEntity corte = new HistoricoCorteOnsEntity();
        corte.setDataCorte(data);
        return corte;
    }
}

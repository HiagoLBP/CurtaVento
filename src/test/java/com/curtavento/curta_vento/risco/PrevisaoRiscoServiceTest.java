package com.curtavento.curta_vento.risco;

import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.NivelRisco;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import com.curtavento.curta_vento.application.usecase.PrevisaoRiscoService;

import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrevisaoRiscoServiceTest {

    @Mock
    private SpringDataPrevisaoRiscoRepository repository;

    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private PrevisaoRiscoService previsaoRiscoService;

    private UsinaEntity usinaMock;
    private LeituraClimaticaEntity leituraMock;

    @BeforeEach
    void setUp() {
        usinaMock = new UsinaEntity();
        usinaMock.setId(10L);

        leituraMock = new LeituraClimaticaEntity();
        leituraMock.setId(1L);
    }

    @Test
    void deveCalcularRiscoCriticoParaVentoMuitoForte() {
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("12.5")); // >= 10 m/s => 40 pontos
        
        when(repository.findFirstByUsinaIdAndDataReferencia(eq(10L), any(LocalDate.class))).thenReturn(null);
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new java.util.ArrayList<>());

        PrevisaoRiscoEntity previsao = previsaoRiscoService.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals("CRITICO", previsao.getNivelRisco().name());
    }

    @Test
    void deveCalcularRiscoAlertaParaVentoForte() {
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("8.5")); // >= 7 m/s => 20 pontos
        
        when(repository.findFirstByUsinaIdAndDataReferencia(eq(10L), any(LocalDate.class))).thenReturn(null);
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new java.util.ArrayList<>());

        PrevisaoRiscoEntity previsao = previsaoRiscoService.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals("ALERTA", previsao.getNivelRisco().name()); // Assuming score > 0 but not CRITICO
    }

    @Test
    void deveCalcularRiscoBaixoParaVentoNormal() {
        leituraMock.setVentoPrevisao24hMs(new BigDecimal("4.0")); // < 7 m/s => 0 pontos
        
        when(repository.findFirstByUsinaIdAndDataReferencia(eq(10L), any(LocalDate.class))).thenReturn(null);
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new java.util.ArrayList<>());

        PrevisaoRiscoEntity previsao = previsaoRiscoService.calcularRisco(usinaMock, leituraMock, 24);

        assertNotNull(previsao);
        assertEquals("BAIXO", previsao.getNivelRisco().name()); 
    }
}

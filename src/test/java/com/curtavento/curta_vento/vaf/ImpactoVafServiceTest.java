package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.application.usecase.ImpactoVafService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpactoVafServiceTest {

    @Mock
    private SpringDataImpactoVafRepository impactoRepository;

    @Mock
    private SpringDataMunicipioRepository municipioRepository;

    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private ImpactoVafService impactoVafService;

    private MunicipioEntity municipioMock;

    @BeforeEach
    void setUp() {
        municipioMock = new MunicipioEntity();
        municipioMock.setId(1L);
        municipioMock.setNome("Guanambi");
        // IPM de 5% (0.05)
        municipioMock.setVafAnoBase(new BigDecimal("0.05"));
    }

    @Test
    void deveCalcularImpactoComSucessoParaMunicipioValido() {
        when(municipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));
        when(corteRepository.sumEnergiaCortadaPorMunicipioEAno(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("150.0"));
        when(impactoRepository.findByMunicipioIdAndAnoReferencia(1L, 2026)).thenReturn(new ArrayList<>());
        when(impactoRepository.save(any(ImpactoVafEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ImpactoVafEntity impacto = impactoVafService.calcularImpacto(1L, 2026);

        assertEquals(0, new BigDecimal("150.0").compareTo(impacto.getEnergiaTotalCortadaMwh()));
        assertEquals(0, new BigDecimal("30000.0").compareTo(impacto.getPerdaReceitaEstimada()));
        assertEquals(0, new BigDecimal("1500.0").compareTo(impacto.getImpactoVafEstimado()));
        verify(impactoRepository, times(1)).save(any(ImpactoVafEntity.class));
    }

    @Test
    void deveAplicarFallbackSeIpmForNulo() {
        municipioMock.setVafAnoBase(null);
        when(municipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));
        when(corteRepository.sumEnergiaCortadaPorMunicipioEAno(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("100.0"));
        when(impactoRepository.save(any(ImpactoVafEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ImpactoVafEntity impacto = impactoVafService.calcularImpacto(1L, 2026);

        assertEquals(0, new BigDecimal("400.0").compareTo(impacto.getImpactoVafEstimado()));
    }

    @Test
    void deveNormalizarIpmPercentualMaiorQueUmComArredondamentoSeguro() {
        municipioMock.setVafAnoBase(new BigDecimal("2.5"));
        when(municipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));
        when(corteRepository.sumEnergiaCortadaPorMunicipioEAno(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("100.0"));
        when(impactoRepository.save(any(ImpactoVafEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ImpactoVafEntity impacto = impactoVafService.calcularImpacto(1L, 2026);

        // Perda = 100 * 200 = 20000; 2.5% = 0.025 => 20000 * 0.025 = 500
        assertEquals(0, new BigDecimal("500.0").compareTo(impacto.getImpactoVafEstimado()));
    }

    @Test
    void deveLancarExcecaoSeMunicipioNaoExistir() {
        when(municipioRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> impactoVafService.calcularImpacto(999L, 2026));

        assertTrue(exception.getMessage().contains("999"));
    }
}

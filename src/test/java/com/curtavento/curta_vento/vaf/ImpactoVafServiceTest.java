package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.application.usecase.ImpactoVafService;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpactoVafServiceTest {

    @Mock
    private SpringDataImpactoVafRepository impactoRepository;

    @Mock
    private SpringDataMunicipioRepository SpringDataMunicipioRepository;

    @Mock
    private SpringDataUsinaRepository usinaRepository;

    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private ImpactoVafService impactoVafService;

    private MunicipioEntity municipioMock;
    private UsinaEntity usinaMock;

    @BeforeEach
    void setUp() {
        municipioMock = new MunicipioEntity();
        municipioMock.setId(1L);
        municipioMock.setNome("Guanambi");
        // IPM de 5% (0.05)
        municipioMock.setVafAnoBase(new BigDecimal("0.05"));

        usinaMock = new UsinaEntity();
        usinaMock.setId(10L);
        usinaMock.setNome("UsinaEntity Eolica 1");
        usinaMock.setMunicipio(municipioMock);
    }

    @Test
    void deveCalcularImpactoComSucessoParaMunicipioValido() {
        // Arrange
        when(SpringDataMunicipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));

        // Mock the cut history sum
        when(corteRepository.sumEnergiaCortadaPorMunicipioEAno(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("150.0"));
                
        when(impactoRepository.findByMunicipioIdAndAnoReferencia(1L, 2026))
                .thenReturn(new ArrayList<>());
                
        when(impactoRepository.save(any(ImpactoVafEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ImpactoVafEntity impacto = impactoVafService.calcularImpacto(1L, 2026);

        // Assert
        // Energia Total = 150 MWh
        assertEquals(0, new BigDecimal("150.0").compareTo(impacto.getEnergiaTotalCortadaMwh()));
        
        // Perda Receita = 150 MWh * 200.0 (PrecoEnergia.PRECO_MWH) = 30000
        assertEquals(0, new BigDecimal("30000.0").compareTo(impacto.getPerdaReceitaEstimada()));
        
        // Impacto Final = 30000 * 0.05 (IPM) = 1500
        assertEquals(0, new BigDecimal("1500.0").compareTo(impacto.getImpactoVafEstimado()));
        
        verify(impactoRepository, times(1)).save(any(ImpactoVafEntity.class));
    }

    @Test
    void deveAplicarFallbackSeIpmForNulo() {
        // Arrange
        municipioMock.setVafAnoBase(null); // Vai usar o fallback de 0.02
        when(SpringDataMunicipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));

        // Mock the cut history sum
        when(corteRepository.sumEnergiaCortadaPorMunicipioEAno(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("100.0"));

        when(impactoRepository.save(any(ImpactoVafEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ImpactoVafEntity impacto = impactoVafService.calcularImpacto(1L, 2026);

        // Assert
        // Perda = 100 * 200 = 20000
        // Arrecadacao = 20000 * 0.02 = 400
        assertEquals(0, new BigDecimal("400.0").compareTo(impacto.getImpactoVafEstimado()));
    }

    @Test
    void deveLancarExcecaoSeMunicipioNaoExistir() {
        // Arrange
        when(SpringDataMunicipioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            impactoVafService.calcularImpacto(999L, 2026);
        });
        
        assertTrue(exception.getMessage().contains("999"));
    }
}


package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.corte.HistoricoCorteOns;
import com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
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
    private ImpactoVafRepository impactoRepository;

    @Mock
    private MunicipioRepository municipioRepository;

    @Mock
    private UsinaRepository usinaRepository;

    @Mock
    private HistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private ImpactoVafService impactoVafService;

    private Municipio municipioMock;
    private Usina usinaMock;

    @BeforeEach
    void setUp() {
        municipioMock = new Municipio();
        municipioMock.setId(1L);
        municipioMock.setNome("Guanambi");
        // IPM de 5% (0.05)
        municipioMock.setVafAnoBase(new BigDecimal("0.05"));

        usinaMock = new Usina();
        usinaMock.setId(10L);
        usinaMock.setNome("Usina Eolica 1");
        usinaMock.setMunicipio(municipioMock);
    }

    @Test
    void deveCalcularImpactoComSucessoParaMunicipioValido() {
        // Arrange
        when(municipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));
        when(usinaRepository.findAll()).thenReturn(Collections.singletonList(usinaMock));

        HistoricoCorteOns corte1 = new HistoricoCorteOns();
        corte1.setEnergiaCortadaMwh(new BigDecimal("50.0")); // 50 MWh
        
        HistoricoCorteOns corte2 = new HistoricoCorteOns();
        corte2.setEnergiaCortadaMwh(new BigDecimal("100.0")); // 100 MWh
        
        List<HistoricoCorteOns> cortes = List.of(corte1, corte2);
        
        // Mock the cut history search between Jan 1st and Dec 31st
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(cortes);
                
        when(impactoRepository.findByMunicipioIdAndAnoReferencia(1L, 2026))
                .thenReturn(new ArrayList<>());
                
        when(impactoRepository.save(any(ImpactoVaf.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ImpactoVaf impacto = impactoVafService.calcularImpacto(1L, 2026);

        // Assert
        // Energia Total = 150 MWh
        assertEquals(0, new BigDecimal("150.0").compareTo(impacto.getEnergiaTotalCortadaMwh()));
        
        // Perda Receita = 150 MWh * 200.0 (PrecoEnergia.PRECO_MWH) = 30000
        // No BD de VAF não tem o campo de perdaReceita direto no modelo salvo, mas o serviço calcula e salva faturamentoPerdidoRs
        assertEquals(0, new BigDecimal("30000.0").compareTo(impacto.getPerdaReceitaEstimada()));
        
        // Impacto Final = 30000 * 0.05 (IPM) = 1500
        assertEquals(0, new BigDecimal("1500.0").compareTo(impacto.getImpactoVafEstimado()));
        
        verify(impactoRepository, times(1)).save(any(ImpactoVaf.class));
    }

    @Test
    void deveAplicarFallbackSeIpmForNulo() {
        // Arrange
        municipioMock.setVafAnoBase(null); // Vai usar o fallback de 0.02
        when(municipioRepository.findById(1L)).thenReturn(Optional.of(municipioMock));
        when(usinaRepository.findAll()).thenReturn(Collections.singletonList(usinaMock));

        HistoricoCorteOns corte = new HistoricoCorteOns();
        corte.setEnergiaCortadaMwh(new BigDecimal("100.0")); // 100 MWh
        when(corteRepository.findByUsinaIdAndDataCorteBetween(any(), any(), any())).thenReturn(List.of(corte));
        when(impactoRepository.save(any(ImpactoVaf.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ImpactoVaf impacto = impactoVafService.calcularImpacto(1L, 2026);

        // Assert
        // Perda = 100 * 200 = 20000
        // Arrecadacao = 20000 * 0.02 = 400
        assertEquals(0, new BigDecimal("400.0").compareTo(impacto.getImpactoVafEstimado()));
    }

    @Test
    void deveLancarExcecaoSeMunicipioNaoExistir() {
        // Arrange
        when(municipioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            impactoVafService.calcularImpacto(999L, 2026);
        });
        
        assertTrue(exception.getMessage().contains("999"));
    }
}

package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
    }

    @Test
    void deveSalvarImpactoVaf() {
        Municipio m = new Municipio();
        m.setId(1L);
        m.setNome("Macaubas");

        ImpactoVaf impacto = new ImpactoVaf();
        impacto.setMunicipio(m);
        impacto.setAno(2026);
        impacto.setEnergiaCortadaMwh(new BigDecimal("100.5"));

        when(impactoRepository.save(any(ImpactoVaf.class))).thenAnswer(i -> i.getArguments()[0]);

        ImpactoVaf salvo = impactoRepository.save(impacto);

        assertNotNull(salvo);
        verify(impactoRepository, times(1)).save(impacto);
    }
}

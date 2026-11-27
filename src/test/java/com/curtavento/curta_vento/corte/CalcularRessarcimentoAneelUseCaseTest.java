package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.application.usecase.CalcularRessarcimentoAneelUseCase;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularRessarcimentoAneelUseCaseTest {

    @Mock
    private SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;

    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private CalcularRessarcimentoAneelUseCase useCase;

    @Test
    void deveSepararCortesRessarciveisEDescontarFranquiaENotaTecnica183() {
        // Usina 1: 200 blocos REL (100h, 1000 MWh) e 100 blocos ENE (50h, 500 MWh)
        when(blocoRepository.findAgregadoRegulatorio(any(), any(), isNull())).thenReturn(List.of(
                new Object[]{1L, "Usina Teste", "REL", new BigDecimal("1000.0"), 200L, new BigDecimal("200000.0")},
                new Object[]{1L, "Usina Teste", "ENE", new BigDecimal("500.0"), 100L, new BigDecimal("100000.0")}
        ));

        // Sem NT 183, franquia de 50h sobre 100h elegíveis => 50% ressarcível (500 MWh)
        Map<String, Object> semNt183 = useCase.executar(2026, null, 50.0, false, null);
        assertEquals(500.0, (Double) semNt183.get("ressarcivelLiquidoMwh"), 0.01);
        assertEquals(100000.0, (Double) semNt183.get("ressarcivelLiquidoRs"), 0.01);
        assertEquals(500.0, (Double) semNt183.get("franquiaAbsorvidaMwh"), 0.01);
        assertEquals(500.0, (Double) semNt183.get("naoRessarcivelMwh"), 0.01);

        // Com NT 183 (-17% de REL/CNF migrado para ENE) e franquia 0h => 830 MWh ressarcível, 670 MWh ENE
        Map<String, Object> comNt183 = useCase.executar(2026, null, 0.0, true, new BigDecimal("200.0"));
        assertEquals(830.0, (Double) comNt183.get("ressarcivelLiquidoMwh"), 0.01);
        assertEquals(670.0, (Double) comNt183.get("naoRessarcivelMwh"), 0.01);
        assertEquals(166000.0, (Double) comNt183.get("ressarcivelLiquidoRs"), 0.01);

        // Com REN 1.170/2026 (Ciclo 2 = 50% de rateio ENE com hidrelétricas EVT) sobre 500 MWh ENE => 250 MWh compensável
        Map<String, Object> comRen1170 = useCase.executar(2026, null, 0.0, false, new BigDecimal("200.0"), 50.0);
        assertEquals(250.0, (Double) comRen1170.get("compensavelRen1170Mwh"), 0.01);
        assertEquals(50000.0, (Double) comRen1170.get("compensavelRen1170Rs"), 0.01);
        assertEquals(250.0, (Double) comRen1170.get("naoRessarcivelMwh"), 0.01);
    }

    @Test
    void deveExcluirCortesParDoCalculoDeRessarcimentoEDaPerda() {
        when(blocoRepository.findAgregadoRegulatorio(any(), any(), isNull())).thenReturn(List.of(
                new Object[]{1L, "Usina Teste", "REL", new BigDecimal("100.0"), 20L, new BigDecimal("20000.0")},
                new Object[]{1L, "Usina Teste", "PAR", new BigDecimal("300.0"), 60L, new BigDecimal("60000.0")}
        ));

        Map<String, Object> res = useCase.executar(2026, null, 0.0, false, new BigDecimal("200.0"), 0.0);
        assertEquals(100.0, (Double) res.get("ressarcivelLiquidoMwh"), 0.01);
        assertEquals(0.0, (Double) res.get("naoRessarcivelMwh"), 0.01);
        assertEquals(300.0, (Double) res.get("parExcluidoMwh"), 0.01);
        assertEquals(60000.0, (Double) res.get("parExcluidoRs"), 0.01);
    }

    @Test
    void deveSepararRegraVigenteSomenteRelEAplicarFranquiaSolar50PctSubmodulo65() {
        // 100h REL (500 MWh), 50h CNF (250 MWh)
        when(blocoRepository.findAgregadoRegulatorio(any(), any(), isNull())).thenReturn(List.of(
                new Object[]{1L, "Usina FV Teste", "REL", new BigDecimal("500.0"), 200L, new BigDecimal("100000.0")},
                new Object[]{1L, "Usina FV Teste", "CNF", new BigDecimal("250.0"), 100L, new BigDecimal("50000.0")}
        ));

        // Regra vigente (REN 1.030): CNF nao e ressarcivel; Solar UFV usa 50% da franquia DISPF (1 - 0.99)*8760*0.5 = 43.8h
        Map<String, Object> vigenteSolar = useCase.executar(
                2026, null, 0.0, false, new BigDecimal("200.0"), 0.0,
                "REGRA_VIGENTE_REL", "SOLAR", 99.0);

        assertEquals(43.8, (Double) vigenteSolar.get("franquiaHorasAplicada"), 0.01);
        assertEquals(250.0, (Double) vigenteSolar.get("cnfNaoRessarcivelMwh"), 0.01);
        assertEquals(250.0, (Double) vigenteSolar.get("naoRessarcivelMwh"), 0.01);
        // REL = 500 MWh em 100h, franquia = 43.8h => 56.2% ressarcivel = 281.0 MWh
        assertEquals(281.0, (Double) vigenteSolar.get("ressarcivelLiquidoMwh"), 0.1);
    }
}

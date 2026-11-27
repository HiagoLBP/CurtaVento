package com.curtavento.curta_vento.simulador;

import com.curtavento.curta_vento.application.usecase.SimularCargaFlexivelDataCenterUseCase;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsBlocoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimularCargaFlexivelDataCenterUseCaseTest {

    @Mock
    private SpringDataUsinaRepository usinaRepository;
    @Mock
    private SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private SimularCargaFlexivelDataCenterUseCase useCase;

    @Test
    void deveCalcularCeuGecEEconomiaTustGpaEmCargaFlexivel() {
        UsinaEntity usina = new UsinaEntity();
        usina.setId(10L);
        usina.setNome("Parque Satoshi Igaporã");

        // DC 10 MW TI * PUE 1.2 = 12 MW total => 6 MWh por bloco de 30min
        // Bloco 1: corte 10 MWh -> DC absorve 6 MWh
        HistoricoCorteOnsBlocoEntity b1 = new HistoricoCorteOnsBlocoEntity();
        b1.setInstante(LocalDateTime.of(2026, 9, 10, 14, 0));
        b1.setEnergiaCortadaMwh(new BigDecimal("10.0"));

        // Bloco 2: 14h30 (pico termico EPE 1.043 pu) sem corte, piso SLA 50% = 6 * 0.50 * 1.043 = 3.129 MWh -> R$ 625.80
        HistoricoCorteOnsBlocoEntity b2 = new HistoricoCorteOnsBlocoEntity();
        b2.setInstante(LocalDateTime.of(2026, 9, 10, 14, 30));
        b2.setEnergiaCortadaMwh(new BigDecimal("0.0"));
        b2.setPldMwh(new BigDecimal("200.0"));

        when(usinaRepository.findById(10L)).thenReturn(Optional.of(usina));
        when(blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(eq(10L), any(), any()))
                .thenReturn(List.of(b1, b2));

        Map<String, Object> res = useCase.executar(
                10L, 2026, 10.0, 1.2, "HIBRIDO_BASE", 50.0, 220.0, 300.0, 10.0);

        assertEquals(12.0, (Double) res.get("potenciaTotalMw"), 0.01);
        assertEquals(6.0, (Double) res.get("energiaCorteAbsorvidaMwh"), 0.01);
        assertEquals(60.0, (Double) res.get("ceuPct"), 0.01); // 6 / 10
        assertEquals(3.13, (Double) res.get("energiaRedeComplementarMwh"), 0.01);
        // GEC = 3.129 / (6 + 3.129) = 34.28%
        assertEquals(34.28, (Double) res.get("gecPct"), 0.05);
        // Receita bruta = 6 * 300 = 1800; custo rede = 3.129 * 200 = 625.80 => liquido = 1174.20
        assertEquals(1174.20, (Double) res.get("fluxoLiquidoAnualRs"), 0.05);
        // Economia TUST anual = 12 MW * 1000 * 10 * 12 = 1.440.000; GPA = 3 * 12 * 1000 * 10 = 360.000
        assertEquals(1440000.0, (Double) res.get("economiaTustAnualRs"), 0.01);
        assertEquals(360000.0, (Double) res.get("economiaGarantiaGpaRs"), 0.01);
        assertTrue(res.containsKey("serieMensal"));
    }

    @Test
    void deveExecutarBacklogTemporalEMigracaoEspacialEntreUsinas() {
        UsinaEntity u1 = new UsinaEntity();
        u1.setId(20L);
        u1.setNome("Usina Principal");

        UsinaEntity u2 = new UsinaEntity();
        u2.setId(21L);
        u2.setNome("Usina Secundaria");

        // 10 MW TI * PUE 1.05 = 10.5 MW => 5.25 MWh/bloco; piso 50% = 2.625 MWh
        // Bloco 1 (12:00): primaria sem corte -> backlog guarda 2.625 MWh de demanda flexivel
        // Bloco 2 (12:30): primaria corta 12 MWh -> absorve 5.25 MWh locais (não precisa rede!)
        // Bloco 3 (13:00): primaria sem corte e backlog já cheio (ou sec corta 5 MWh) -> migra espacialmente
        HistoricoCorteOnsBlocoEntity p1 = new HistoricoCorteOnsBlocoEntity();
        p1.setInstante(LocalDateTime.of(2026, 9, 10, 12, 0));
        p1.setEnergiaCortadaMwh(new BigDecimal("0.0"));

        HistoricoCorteOnsBlocoEntity p2 = new HistoricoCorteOnsBlocoEntity();
        p2.setInstante(LocalDateTime.of(2026, 9, 10, 12, 30));
        p2.setEnergiaCortadaMwh(new BigDecimal("12.0"));

        HistoricoCorteOnsBlocoEntity s1 = new HistoricoCorteOnsBlocoEntity();
        s1.setInstante(LocalDateTime.of(2026, 9, 10, 12, 0));
        s1.setEnergiaCortadaMwh(new BigDecimal("4.0"));

        when(usinaRepository.findById(20L)).thenReturn(Optional.of(u1));
        when(usinaRepository.findById(21L)).thenReturn(Optional.of(u2));
        when(blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(eq(20L), any(), any()))
                .thenReturn(List.of(p1, p2));
        when(blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(eq(21L), any(), any()))
                .thenReturn(List.of(s1));

        Map<String, Object> res = useCase.executar(
                20L, 2026, 10.0, 1.05, "TEMPORAL_SHIFT", 50.0, 200.0, 300.0, 10.0,
                4.0, 100.0, 21L, 30.0, 0.085);

        assertEquals("TEMPORAL_SHIFT", res.get("modoOperacao"));
        assertTrue((Double) res.get("tco2Evitadas") > 0.0);
        assertEquals("Usina Secundaria", res.get("usinaSecundaria"));
    }
}

package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.application.usecase.SimularImpactoGovTechUseCase;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimularImpactoGovTechUseCaseTest {

    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;
    @Mock
    private SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    @Mock
    private SpringDataMunicipioRepository municipioRepository;
    @Mock
    private SpringDataUsinaRepository usinaRepository;

    @InjectMocks
    private SimularImpactoGovTechUseCase useCase;

    @Test
    void deveProjetarImpactoVafNosExerciciosT1ET2() {
        MunicipioEntity mun = new MunicipioEntity();
        mun.setId(1L);
        mun.setNome("Caetité");
        mun.setCodigoIbge("2905206");
        mun.setIpm(new BigDecimal("0.05")); // 5%

        when(corteRepository.findImpactoPorMunicipio(any(), any()))
                .thenReturn(Collections.singletonList(new Object[]{"Caetité", 4L, new BigDecimal("100.0")}));
        when(municipioRepository.findAll()).thenReturn(List.of(mun));

        Map<String, Object> proj = useCase.calcularProjecaoPlurianualVaf(2026);

        // 100 MWh * 200 = R$ 20.000; 5% = R$ 1.000 => t+1 = R$ 500, t+2 = R$ 1.000
        assertEquals(2027, proj.get("anoImpactoT1"));
        assertEquals(2028, proj.get("anoImpactoT2"));
        assertEquals(500.0, (Double) proj.get("totalImpactoT1Rs"), 0.01);
        assertEquals(1000.0, (Double) proj.get("totalImpactoT2Rs"), 0.01);
        assertEquals(1500.0, (Double) proj.get("totalImpactoBienioRs"), 0.01);
    }

    @Test
    void deveAuditarRoyaltiesDeArrendamentoRuralComBaseOficialOns() {
        MunicipioEntity mun = new MunicipioEntity();
        mun.setNome("Igaporã");

        UsinaEntity usina = new UsinaEntity();
        usina.setId(7L);
        usina.setNome("Usina Alto Sertão");
        usina.setMunicipio(mun);
        usina.setPrecoPpaMwh(new BigDecimal("200.00"));

        when(usinaRepository.findById(7L)).thenReturn(Optional.of(usina));
        when(blocoRepository.findTotaisEnergiaPorUsina(eq(7L), any(), any()))
                .thenReturn(Collections.singletonList(new Object[]{
                        new BigDecimal("1000.0"), new BigDecimal("5000.0"), new BigDecimal("4000.0")
                }));

        Map<String, Object> audit = useCase.auditarArrendamentoRural(7L, 2026, 2.0, 4);

        // Verificada: 4000 * 200 = 800.000 => 2% = R$ 16.000
        // Cortada: 1000 * 200 = 200.000 => 2% = R$ 4.000 (R$ 1.000 por proprietário)
        assertFalse((Boolean) audit.get("estimativaPorCapacidade"));
        assertEquals(16000.0, (Double) audit.get("royaltyDevidoVerificadoRs"), 0.01);
        assertEquals(4000.0, (Double) audit.get("royaltyPerdidoCurtailmentRs"), 0.01);
        assertEquals(1000.0, (Double) audit.get("perdaPorProprietarioRs"), 0.01);
        assertEquals(20.0, (Double) audit.get("percentualPerdaRendaRural"), 0.01);
    }
}

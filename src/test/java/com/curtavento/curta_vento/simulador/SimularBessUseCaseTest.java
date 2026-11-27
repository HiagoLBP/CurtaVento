package com.curtavento.curta_vento.simulador;

import com.curtavento.curta_vento.application.usecase.SimularBessUseCase;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsBlocoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimularBessUseCaseTest {

    @Mock
    private SpringDataUsinaRepository usinaRepository;
    @Mock
    private SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;

    @InjectMocks
    private SimularBessUseCase useCase;

    @Test
    void deveCalcularArbitragemBessComLimiteDeCapacidadeERte() {
        UsinaEntity usina = new UsinaEntity();
        usina.setId(5L);
        usina.setNome("Usina BESS Teste");

        // Bateria 10 MW / 20 MWh; em um dia houve 2 blocos cortando 15 MWh cada (total 30 MWh, mas cada bloco de 30min só absorve 10*0.5 = 5 MWh -> 10 MWh total no dia!)
        HistoricoCorteOnsBlocoEntity b1 = new HistoricoCorteOnsBlocoEntity();
        b1.setInstante(LocalDateTime.of(2026, 4, 10, 12, 0));
        b1.setEnergiaCortadaMwh(new BigDecimal("15.0"));

        HistoricoCorteOnsBlocoEntity b2 = new HistoricoCorteOnsBlocoEntity();
        b2.setInstante(LocalDateTime.of(2026, 4, 10, 12, 30));
        b2.setEnergiaCortadaMwh(new BigDecimal("15.0"));

        when(usinaRepository.findById(5L)).thenReturn(Optional.of(usina));
        when(blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(eq(5L), any(), any()))
                .thenReturn(List.of(b1, b2));

        Map<String, Object> res = useCase.executar(5L, 2026, 10.0, 20.0, 90.0, 400.0, 1500.0, 2.0);

        assertEquals(30.0, (Double) res.get("energiaCortadaAnualMwh"), 0.01);
        assertEquals(10.0, (Double) res.get("energiaArmazenadaAnualMwh"), 0.01);
        // 10 MWh * 90% RTE = 9 MWh entregues na ponta
        assertEquals(9.0, (Double) res.get("energiaEntreguePontaAnualMwh"), 0.01);
        // 9 MWh * R$ 400 = R$ 3600
        assertEquals(3600.0, (Double) res.get("receitaAnualArbitragemRs"), 0.01);
        assertEquals(18.0, (Double) res.get("capacidadeUtilMwh"), 0.01);
        assertNotNull(res.get("vpl10AnosRs"));
        assertNotNull(res.get("serieMensal"));
    }

    @Test
    void deveEmitirAlertaDeEscoamentoESinalLocacionalEpeNt086() {
        MunicipioEntity mun = new MunicipioEntity();
        mun.setNome("Igaporã");
        mun.setCodigoIbge("2913408");

        UsinaEntity usina = new UsinaEntity();
        usina.setId(8L);
        usina.setNome("Alto Sertão III");
        usina.setMunicipio(mun);

        HistoricoCorteOnsBlocoEntity b1 = new HistoricoCorteOnsBlocoEntity();
        b1.setInstante(LocalDateTime.of(2026, 8, 12, 14, 0));
        b1.setEnergiaCortadaMwh(new BigDecimal("4.0"));
        b1.setCodRazaoRestricao("REL");

        HistoricoCorteOnsBlocoEntity b2 = new HistoricoCorteOnsBlocoEntity();
        b2.setInstante(LocalDateTime.of(2026, 8, 12, 19, 0));
        b2.setEnergiaCortadaMwh(new BigDecimal("0.0"));
        b2.setPldMwh(new BigDecimal("450.0"));

        when(usinaRepository.findById(8L)).thenReturn(Optional.of(usina));
        when(blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(eq(8L), any(), any()))
                .thenReturn(List.of(b1, b2));

        Map<String, Object> res = useCase.executar(8L, 2026, 10.0, 20.0, 88.0, 350.0, 1500.0, 2.0);
        assertEquals(1, res.get("blocosComPldReal"));
        assertEquals(100.0, (Double) res.get("fracaoCorteEletricoPct"), 0.01);
        org.junit.jupiter.api.Assertions.assertTrue(
                String.valueOf(res.get("aderenciaLocacionalEpe")).contains("ALTA"));
        org.junit.jupiter.api.Assertions.assertTrue(
                String.valueOf(res.get("alertaEscoamento")).contains("REL/CNF"));
    }

    @Test
    void deveValidarMargemSubestacaoNt095EEmpilharReceitaCapacidadeLrcap() {
        UsinaEntity usinaRestrita = new UsinaEntity();
        usinaRestrita.setId(12L);
        usinaRestrita.setNome("Parque Gentio 230");
        usinaRestrita.setSubestacaoConexao("GENTIO OURO II230kVA");

        HistoricoCorteOnsBlocoEntity b1 = new HistoricoCorteOnsBlocoEntity();
        b1.setInstante(LocalDateTime.of(2026, 6, 15, 13, 0));
        b1.setEnergiaCortadaMwh(new BigDecimal("4.0"));

        when(usinaRepository.findById(12L)).thenReturn(Optional.of(usinaRestrita));
        when(blocoRepository.findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(eq(12L), any(), any()))
                .thenReturn(List.of(b1));

        // 10 MW * 1000 * R$ 250/kW-ano = R$ 2.500.000/ano de receita fixa LRCAP
        Map<String, Object> res = useCase.executar(
                12L, 2026, 10.0, 20.0, 88.0, 350.0, 1500.0, 2.0,
                5.0, 95.0, 10.0, 2.0, 10.0, 250.0);

        assertEquals("RESTRITA_TIPO_B_OU_ZERO_MW", res.get("statusConexaoNt095"));
        assertEquals(0.0, (Double) res.get("margemSubestacaoNt095Mw"), 0.01);
        assertEquals(2500000.0, (Double) res.get("receitaCapacidadeAnualRs"), 0.01);
        assertEquals(2.0, (Double) res.get("duracaoHoras"), 0.01);
    }
}

package com.curtavento.curta_vento.dashboard;

import com.curtavento.curta_vento.application.usecase.ObterResumoDashboardUseCase;
import com.curtavento.curta_vento.domain.model.NivelRisco;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPrevisaoRiscoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObterResumoDashboardUseCaseTest {

    @Mock
    private SpringDataUsinaRepository usinaRepository;
    @Mock
    private SpringDataImpactoVafRepository impactoRepository;
    @Mock
    private SpringDataLeituraClimaticaRepository leituraRepository;
    @Mock
    private SpringDataPrevisaoRiscoRepository riscoRepository;
    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;
    @Mock
    private SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    @Mock
    private SpringDataMunicipioRepository municipioRepository;

    @InjectMocks
    private ObterResumoDashboardUseCase useCase;

    @Test
    @SuppressWarnings("unchecked")
    void deveOrdenarUsinasPorPrejuizoEAplicarLimite() {
        MunicipioEntity mun = new MunicipioEntity();
        mun.setId(1L);
        mun.setNome("Caetité");

        UsinaEntity u1 = new UsinaEntity();
        u1.setId(1L);
        u1.setNome("Usina Menor");
        u1.setOperador("Op");
        u1.setMunicipio(mun);

        UsinaEntity u2 = new UsinaEntity();
        u2.setId(2L);
        u2.setNome("Usina Maior");
        u2.setOperador("Op");
        u2.setMunicipio(mun);

        LeituraClimaticaEntity leitura = new LeituraClimaticaEntity();
        leitura.setUsina(u2);
        leitura.setVelocidadeVentoMs(new BigDecimal("12.0"));
        leitura.setVentoPrevisao24hMs(new BigDecimal("12.5"));
        leitura.setVentoPrevisao48hMs(new BigDecimal("8.0"));
        leitura.setDataHora(LocalDateTime.of(2026, 8, 15, 10, 0));

        PrevisaoRiscoEntity risco = new PrevisaoRiscoEntity();
        risco.setUsina(u2);
        risco.setNivelRisco(NivelRisco.ALTO);
        risco.setPercentualCorteEstimado(new BigDecimal("40.0"));

        ImpactoVafEntity impacto = new ImpactoVafEntity();
        impacto.setMunicipio(mun);
        impacto.setEnergiaTotalCortadaMwh(new BigDecimal("300.0"));
        impacto.setPerdaReceitaEstimada(new BigDecimal("60000.0"));
        impacto.setImpactoVafEstimado(new BigDecimal("3000.0"));

        when(corteRepository.findRankingCortesAno(any(), any())).thenReturn(List.of(
                new Object[]{"Usina Menor", new BigDecimal("50.0")},
                new Object[]{"Usina Maior", new BigDecimal("250.0")}
        ));
        when(leituraRepository.findUltimasLeiturasGeral()).thenReturn(List.of(leitura));
        when(riscoRepository.findUltimosRiscosGeral()).thenReturn(List.of(risco));
        when(usinaRepository.findAll()).thenReturn(List.of(u1, u2));
        when(impactoRepository.findByAnoReferencia(2026)).thenReturn(List.of(impacto));
        when(corteRepository.findMotivosCorte(any(), any())).thenReturn(Collections.emptyList());

        Map<String, Object> resumo = useCase.executar(2026, 1);

        List<Map<String, Object>> usinas = (List<Map<String, Object>>) resumo.get("usinas");
        assertEquals(1, usinas.size());
        assertEquals("Usina Maior", usinas.get(0).get("nome"));
        assertEquals("CRITICO", usinas.get(0).get("risco24h"));
        assertEquals("ALERTA", usinas.get(0).get("janelaManutencao"));
        assertEquals(300.0, (Double) resumo.get("energiaTotal"));
        assertEquals(3000.0, (Double) resumo.get("impactoTotal"));
    }
}

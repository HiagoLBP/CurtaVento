package com.curtavento.curta_vento.risco;

import com.curtavento.curta_vento.application.usecase.PrevisaoCurvaRisco48hUseCase;
import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient;
import com.curtavento.curta_vento.infrastructure.client.OpenMeteoClient.PontoHorarioClimatico;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrevisaoCurvaRisco48hUseCaseTest {

    @Mock
    private SpringDataUsinaRepository usinaRepository;
    @Mock
    private SpringDataLeituraClimaticaRepository leituraRepository;
    @Mock
    private SpringDataHistoricoCorteOnsRepository corteRepository;
    @Mock
    private OpenMeteoClient openMeteoClient;

    @Test
    @SuppressWarnings("unchecked")
    void deveIdentificarCurvaDoPatoEJanelaOtimaDeManutencaoNr35() {
        Clock clockFixo = Clock.fixed(Instant.parse("2026-08-16T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        PrevisaoCurvaRisco48hUseCase useCase = new PrevisaoCurvaRisco48hUseCase(
                usinaRepository, leituraRepository, corteRepository, openMeteoClient, clockFixo);

        MunicipioEntity mun = new MunicipioEntity();
        mun.setNome("Guanambi");

        UsinaEntity usina = new UsinaEntity();
        usina.setId(1L);
        usina.setNome("Usina Curva Pato");
        usina.setOperador("Operador");
        usina.setMunicipio(mun);
        usina.setCapacidadeInstaladaMw(new BigDecimal("40.0"));

        // 2026-08-16 é domingo!
        // Ponto 1: 12h00 domingo, vento 10.5 m/s (<=11.1 NR-35) e radiação 800 W/m2 => Risco CRITICO + Janela Ótima O&M
        PontoHorarioClimatico p1 = new PontoHorarioClimatico(
                LocalDateTime.of(2026, 8, 16, 12, 0),
                new BigDecimal("10.5"), new BigDecimal("12.0"),
                new BigDecimal("800.0"), new BigDecimal("29.0"));

        // Ponto 2: 20h00 domingo (ponta, sem sol), vento 16.0 m/s => Bloqueado NR-35 e não é janela ótima
        PontoHorarioClimatico p2 = new PontoHorarioClimatico(
                LocalDateTime.of(2026, 8, 16, 20, 0),
                new BigDecimal("16.0"), new BigDecimal("19.0"),
                new BigDecimal("0.0"), new BigDecimal("22.0"));

        when(usinaRepository.findById(1L)).thenReturn(Optional.of(usina));
        when(corteRepository.findByUsinaIdAndDataCorteBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(openMeteoClient.fetchHourly48hForecast(usina)).thenReturn(List.of(p1, p2));

        Map<String, Object> resultado = useCase.executar(1L);
        List<Map<String, Object>> curva = (List<Map<String, Object>>) resultado.get("curvaHoraria");

        assertEquals(2, curva.size());
        assertEquals("CRITICO", curva.get(0).get("nivelRisco"));
        assertEquals("ACESSO_CORDA_PAS", curva.get(0).get("categoriaOm"));
        assertTrue((Boolean) curva.get(0).get("janelaOtimaOm"));

        assertEquals("BLOQUEADO", curva.get(1).get("categoriaOm"));
        assertFalse((Boolean) curva.get(1).get("janelaOtimaOm"));
    }
}

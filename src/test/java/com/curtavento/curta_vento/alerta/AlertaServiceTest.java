package com.curtavento.curta_vento.alerta;

import com.curtavento.curta_vento.infrastructure.persistence.entity.AlertaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataAlertaRepository;
import com.curtavento.curta_vento.application.usecase.AlertaService;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private SpringDataAlertaRepository alertaRepository;

    @InjectMocks
    private AlertaService alertaService;

    private UsinaEntity usina;

    @BeforeEach
    void setUp() {
        usina = new UsinaEntity();
        usina.setId(1L);
    }

    @Test
    void deveCriarNovoAlertaSeNaoExistirNoMesmoDia() {
        when(alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(1L)).thenReturn(new ArrayList<>());
        when(alertaRepository.save(any(AlertaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        AlertaEntity alerta = alertaService.criarAlertaUsina("VENTO_FORTE", "ALTO", "Rajadas > 15m/s", usina);

        assertNotNull(alerta);
        assertEquals("VENTO_FORTE", alerta.getTipo());
        assertEquals("ALTO", alerta.getNivelSeveridade());
        assertFalse(alerta.getLido());
        verify(alertaRepository, times(1)).save(any(AlertaEntity.class));
    }

    @Test
    void deveAtualizarAlertaSeJaExistirNoMesmoDia() {
        AlertaEntity alertaExistente = new AlertaEntity();
        alertaExistente.setId(10L);
        alertaExistente.setTipo("VENTO_FORTE");
        alertaExistente.setNivelSeveridade("MEDIO");
        alertaExistente.setLido(true);
        alertaExistente.setDataCriacao(LocalDateTime.now());
        alertaExistente.setUsina(usina);

        ArrayList<AlertaEntity> existentes = new ArrayList<>();
        existentes.add(alertaExistente);

        when(alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(1L)).thenReturn(existentes);
        when(alertaRepository.save(any(AlertaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        AlertaEntity alertaResult = alertaService.criarAlertaUsina("VENTO_FORTE", "CRITICO", "Rajadas > 25m/s", usina);

        assertEquals(10L, alertaResult.getId());
        assertEquals("CRITICO", alertaResult.getNivelSeveridade());
        assertFalse(alertaResult.getLido()); // Volta a ser não lido pois piorou/atualizou
        verify(alertaRepository, times(1)).save(any(AlertaEntity.class));
    }

    @Test
    void naoDeveMarcarComoLidoSeUsuarioForDeOutraUsina() {
        AlertaEntity alertaExistente = new AlertaEntity();
        alertaExistente.setId(10L);
        alertaExistente.setUsina(usina); // UsinaEntity ID 1

        UsuarioEntity usuarioLogado = new UsuarioEntity();
        usuarioLogado.setPerfil("GESTOR_USINA");
        UsinaEntity outraUsina = new UsinaEntity();
        outraUsina.setId(2L);
        usuarioLogado.setUsina(outraUsina); // Tenta ler alerta da UsinaEntity 1

        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alertaExistente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertaService.marcarComoLido(10L, usuarioLogado);
        });

        assertEquals("Acesso negado a este alerta", exception.getMessage());
    }
}

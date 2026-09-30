package com.curtavento.curta_vento.alerta;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usuario.Usuario;
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
    private AlertaRepository alertaRepository;

    @InjectMocks
    private AlertaService alertaService;

    private Usina usina;

    @BeforeEach
    void setUp() {
        usina = new Usina();
        usina.setId(1L);
    }

    @Test
    void deveCriarNovoAlertaSeNaoExistirNoMesmoDia() {
        when(alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(1L)).thenReturn(new ArrayList<>());
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(i -> i.getArguments()[0]);

        Alerta alerta = alertaService.criarAlertaUsina("VENTO_FORTE", "ALTO", "Rajadas > 15m/s", usina);

        assertNotNull(alerta);
        assertEquals("VENTO_FORTE", alerta.getTipo());
        assertEquals("ALTO", alerta.getNivelSeveridade());
        assertFalse(alerta.getLido());
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void deveAtualizarAlertaSeJaExistirNoMesmoDia() {
        Alerta alertaExistente = new Alerta();
        alertaExistente.setId(10L);
        alertaExistente.setTipo("VENTO_FORTE");
        alertaExistente.setNivelSeveridade("MEDIO");
        alertaExistente.setLido(true);
        alertaExistente.setDataCriacao(LocalDateTime.now());
        alertaExistente.setUsina(usina);

        ArrayList<Alerta> existentes = new ArrayList<>();
        existentes.add(alertaExistente);

        when(alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(1L)).thenReturn(existentes);
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(i -> i.getArguments()[0]);

        Alerta alertaResult = alertaService.criarAlertaUsina("VENTO_FORTE", "CRITICO", "Rajadas > 25m/s", usina);

        assertEquals(10L, alertaResult.getId());
        assertEquals("CRITICO", alertaResult.getNivelSeveridade());
        assertFalse(alertaResult.getLido()); // Volta a ser não lido pois piorou/atualizou
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void naoDeveMarcarComoLidoSeUsuarioForDeOutraUsina() {
        Alerta alertaExistente = new Alerta();
        alertaExistente.setId(10L);
        alertaExistente.setUsina(usina); // Usina ID 1

        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setPerfil("GESTOR_USINA");
        Usina outraUsina = new Usina();
        outraUsina.setId(2L);
        usuarioLogado.setUsina(outraUsina); // Tenta ler alerta da Usina 1

        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alertaExistente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertaService.marcarComoLido(10L, usuarioLogado);
        });

        assertEquals("Acesso negado a este alerta", exception.getMessage());
    }
}

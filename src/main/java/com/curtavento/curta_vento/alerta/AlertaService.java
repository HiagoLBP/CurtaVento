package com.curtavento.curta_vento.alerta;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public Alerta criarAlertaUsina(String tipo, String nivel, String mensagem, Usina usina) {
        List<Alerta> existentes = alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(usina.getId());
        if (!existentes.isEmpty()) {
            Alerta ultimo = existentes.get(0);
            if (ultimo.getTipo().equals(tipo) && ultimo.getDataCriacao().toLocalDate().equals(java.time.LocalDate.now())) {
                ultimo.setNivelSeveridade(nivel);
                ultimo.setMensagem(mensagem);
                ultimo.setLido(false);
                return alertaRepository.save(ultimo);
            }
        }

        Alerta alerta = new Alerta();
        alerta.setTipo(tipo);
        alerta.setNivelSeveridade(nivel);
        alerta.setMensagem(mensagem);
        alerta.setUsina(usina);
        alerta.setDataCriacao(LocalDateTime.now());
        alerta.setLido(false);
        return alertaRepository.save(alerta);
    }

    public Alerta criarAlertaMunicipio(String tipo, String nivel, String mensagem, Municipio municipio) {
        List<Alerta> existentes = alertaRepository.findByMunicipioIdOrderByDataCriacaoDesc(municipio.getId());
        if (!existentes.isEmpty()) {
            Alerta ultimo = existentes.get(0);
            if (ultimo.getTipo().equals(tipo) && ultimo.getDataCriacao().toLocalDate().equals(java.time.LocalDate.now())) {
                ultimo.setNivelSeveridade(nivel);
                ultimo.setMensagem(mensagem);
                ultimo.setLido(false);
                return alertaRepository.save(ultimo);
            }
        }

        Alerta alerta = new Alerta();
        alerta.setTipo(tipo);
        alerta.setNivelSeveridade(nivel);
        alerta.setMensagem(mensagem);
        alerta.setMunicipio(municipio);
        alerta.setDataCriacao(LocalDateTime.now());
        alerta.setLido(false);
        return alertaRepository.save(alerta);
    }

    public List<Alerta> listarAlertasParaUsuario(Usuario usuarioLogado) {
        if ("ADMIN".equals(usuarioLogado.getPerfil())) {
            return alertaRepository.findAllByOrderByDataCriacaoDesc();
        } else if ("GESTOR_USINA".equals(usuarioLogado.getPerfil()) && usuarioLogado.getUsina() != null) {
            return alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(usuarioLogado.getUsina().getId());
        } else if ("PREFEITURA".equals(usuarioLogado.getPerfil()) && usuarioLogado.getMunicipio() != null) {
            return alertaRepository.findByMunicipioIdOrderByDataCriacaoDesc(usuarioLogado.getMunicipio().getId());
        }
        return List.of(); // Retorna vazio se não se encaixar (ex: GESTOR sem usina atrelada)
    }

    public void marcarComoLido(Long alertaId, Usuario usuarioLogado) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));

        // Regra simples de autorização
        if ("GESTOR_USINA".equals(usuarioLogado.getPerfil())) {
            if (alerta.getUsina() == null || !alerta.getUsina().getId().equals(usuarioLogado.getUsina().getId())) {
                throw new RuntimeException("Acesso negado a este alerta");
            }
        } else if ("PREFEITURA".equals(usuarioLogado.getPerfil())) {
            if (alerta.getMunicipio() == null || !alerta.getMunicipio().getId().equals(usuarioLogado.getMunicipio().getId())) {
                throw new RuntimeException("Acesso negado a este alerta");
            }
        }

        alerta.setLido(true);
        alertaRepository.save(alerta);
    }
}

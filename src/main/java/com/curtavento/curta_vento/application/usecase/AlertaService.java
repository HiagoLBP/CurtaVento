package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.infrastructure.persistence.entity.AlertaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataAlertaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaService {

    private final SpringDataAlertaRepository alertaRepository;

    public AlertaService(SpringDataAlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public AlertaEntity criarAlertaUsina(String tipo, String nivel, String mensagem, UsinaEntity usina) {
        List<AlertaEntity> existentes = alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(usina.getId());
        if (!existentes.isEmpty()) {
            AlertaEntity ultimo = existentes.get(0);
            if (ultimo.getTipo().equals(tipo) && ultimo.getDataCriacao().toLocalDate().equals(java.time.LocalDate.now())) {
                ultimo.setNivelSeveridade(nivel);
                ultimo.setMensagem(mensagem);
                ultimo.setLido(false);
                return alertaRepository.save(ultimo);
            }
        }

        AlertaEntity alerta = new AlertaEntity();
        alerta.setTipo(tipo);
        alerta.setNivelSeveridade(nivel);
        alerta.setMensagem(mensagem);
        alerta.setUsina(usina);
        alerta.setDataCriacao(LocalDateTime.now());
        alerta.setLido(false);
        return alertaRepository.save(alerta);
    }

    public AlertaEntity criarAlertaMunicipio(String tipo, String nivel, String mensagem, MunicipioEntity municipio) {
        List<AlertaEntity> existentes = alertaRepository.findByMunicipioIdOrderByDataCriacaoDesc(municipio.getId());
        if (!existentes.isEmpty()) {
            AlertaEntity ultimo = existentes.get(0);
            if (ultimo.getTipo().equals(tipo) && ultimo.getDataCriacao().toLocalDate().equals(java.time.LocalDate.now())) {
                ultimo.setNivelSeveridade(nivel);
                ultimo.setMensagem(mensagem);
                ultimo.setLido(false);
                return alertaRepository.save(ultimo);
            }
        }

        AlertaEntity alerta = new AlertaEntity();
        alerta.setTipo(tipo);
        alerta.setNivelSeveridade(nivel);
        alerta.setMensagem(mensagem);
        alerta.setMunicipio(municipio);
        alerta.setDataCriacao(LocalDateTime.now());
        alerta.setLido(false);
        return alertaRepository.save(alerta);
    }

    public List<AlertaEntity> listarAlertasParaUsuario(UsuarioEntity usuarioLogado) {
        if ("ADMIN".equals(usuarioLogado.getPerfil())) {
            return alertaRepository.findAllByOrderByDataCriacaoDesc();
        } else if ("GESTOR_USINA".equals(usuarioLogado.getPerfil()) && usuarioLogado.getUsina() != null) {
            return alertaRepository.findByUsinaIdOrderByDataCriacaoDesc(usuarioLogado.getUsina().getId());
        } else if ("PREFEITURA".equals(usuarioLogado.getPerfil()) && usuarioLogado.getMunicipio() != null) {
            return alertaRepository.findByMunicipioIdOrderByDataCriacaoDesc(usuarioLogado.getMunicipio().getId());
        }
        return List.of();
    }

    public void marcarComoLido(Long alertaId, UsuarioEntity usuarioLogado) {
        AlertaEntity alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta n�o encontrado"));

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
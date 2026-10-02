package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.AlertaService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.AlertaEntity;
import com.curtavento.curta_vento.interfaces.http.dto.AlertaDTO;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public ResponseEntity<List<AlertaDTO>> listarAlertas(@AuthenticationPrincipal UsuarioEntity usuarioLogado) {
        List<AlertaEntity> alertas = alertaService.listarAlertasParaUsuario(usuarioLogado);
        List<AlertaDTO> dtos = alertas.stream().map(AlertaDTO::de).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/marcar-lido")
    public ResponseEntity<Void> marcarLido(@PathVariable Long id, @AuthenticationPrincipal UsuarioEntity usuarioLogado) {
        alertaService.marcarComoLido(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
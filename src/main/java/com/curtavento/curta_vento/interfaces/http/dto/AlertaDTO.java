package com.curtavento.curta_vento.interfaces.http.dto;

import com.curtavento.curta_vento.infrastructure.persistence.entity.AlertaEntity;
import java.time.LocalDateTime;

public record AlertaDTO(
        Long id,
        String tipo,
        String mensagem,
        String nivelSeveridade,
        String nomeOrigem,
        LocalDateTime dataCriacao,
        Boolean lido
) {
    public static AlertaDTO de(AlertaEntity alerta) {
        String origem = "";
        if (alerta.getUsina() != null) origem = alerta.getUsina().getNome();
        else if (alerta.getMunicipio() != null) origem = alerta.getMunicipio().getNome();
        
        return new AlertaDTO(
                alerta.getId(),
                alerta.getTipo(),
                alerta.getMensagem(),
                alerta.getNivelSeveridade(),
                origem,
                alerta.getDataCriacao(),
                alerta.getLido()
        );
    }
}
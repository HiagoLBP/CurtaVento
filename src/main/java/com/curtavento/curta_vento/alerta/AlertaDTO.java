package com.curtavento.curta_vento.alerta;

import java.time.LocalDateTime;

public record AlertaDTO(
        Long id,
        String tipo,
        String mensagem,
        String nivelSeveridade,
        String nomeOrigem, // Nome da usina ou do município
        LocalDateTime dataCriacao,
        Boolean lido
) {
    public static AlertaDTO de(Alerta alerta) {
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

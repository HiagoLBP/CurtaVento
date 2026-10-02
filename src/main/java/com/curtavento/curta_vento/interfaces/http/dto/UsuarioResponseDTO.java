package com.curtavento.curta_vento.interfaces.http.dto;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;

public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String perfil;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(UsuarioEntity usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.perfil = usuario.getPerfil();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPerfil() { return perfil; }
}
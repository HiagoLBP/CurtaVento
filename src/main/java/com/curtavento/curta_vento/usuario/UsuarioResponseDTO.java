package com.curtavento.curta_vento.usuario;

public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String perfil;

    // Construtor
    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.perfil = usuario.getPerfil();
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPerfil() { return perfil; }
}

package com.curtavento.curta_vento.domain.model;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String perfil;
    private Usina usina;
    private Municipio municipio;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
}
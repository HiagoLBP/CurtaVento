package com.curtavento.curta_vento.domain.model;

import java.time.LocalDateTime;

public class Alerta {
    private Long id;
    private String tipo;
    private String mensagem;
    private String nivelSeveridade;
    private Usina usina;
    private Municipio municipio;
    private LocalDateTime dataCriacao;
    private Boolean lido = false;

    public Alerta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getNivelSeveridade() { return nivelSeveridade; }
    public void setNivelSeveridade(String nivelSeveridade) { this.nivelSeveridade = nivelSeveridade; }
    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Boolean getLido() { return lido; }
    public void setLido(Boolean lido) { this.lido = lido; }
}
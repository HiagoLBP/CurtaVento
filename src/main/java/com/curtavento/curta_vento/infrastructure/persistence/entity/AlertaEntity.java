package com.curtavento.curta_vento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
public class AlertaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 1000)
    private String mensagem;

    @Column(name = "nivel_severidade", nullable = false)
    private String nivelSeveridade;

    @ManyToOne
    @JoinColumn(name = "usina_id")
    private UsinaEntity usina;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private MunicipioEntity municipio;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "lido", nullable = false)
    private Boolean lido = false;

    public AlertaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getNivelSeveridade() { return nivelSeveridade; }
    public void setNivelSeveridade(String nivelSeveridade) { this.nivelSeveridade = nivelSeveridade; }
    public UsinaEntity getUsina() { return usina; }
    public void setUsina(UsinaEntity usina) { this.usina = usina; }
    public MunicipioEntity getMunicipio() { return municipio; }
    public void setMunicipio(MunicipioEntity municipio) { this.municipio = municipio; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Boolean getLido() { return lido; }
    public void setLido(Boolean lido) { this.lido = lido; }
}
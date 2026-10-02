package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Usina {
    private Long id;
    private String nome;
    private String operador;
    private Municipio municipio;
    private BigDecimal capacidadeInstaladaMw;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate dataInicioOperacao;

    public Usina() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
    public BigDecimal getCapacidadeInstaladaMw() { return capacidadeInstaladaMw; }
    public void setCapacidadeInstaladaMw(BigDecimal capacidadeInstaladaMw) { this.capacidadeInstaladaMw = capacidadeInstaladaMw; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public LocalDate getDataInicioOperacao() { return dataInicioOperacao; }
    public void setDataInicioOperacao(LocalDate dataInicioOperacao) { this.dataInicioOperacao = dataInicioOperacao; }
}

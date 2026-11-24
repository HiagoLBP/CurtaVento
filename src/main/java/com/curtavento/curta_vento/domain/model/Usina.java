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
    private String idOns;
    private String ceg;
    private String nomeConjuntoOns;
    private String subestacaoConexao;
    private BigDecimal precoPpaMwh;

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
    public String getIdOns() { return idOns; }
    public void setIdOns(String idOns) { this.idOns = idOns; }
    public String getCeg() { return ceg; }
    public void setCeg(String ceg) { this.ceg = ceg; }
    public String getNomeConjuntoOns() { return nomeConjuntoOns; }
    public void setNomeConjuntoOns(String nomeConjuntoOns) { this.nomeConjuntoOns = nomeConjuntoOns; }
    public String getSubestacaoConexao() { return subestacaoConexao; }
    public void setSubestacaoConexao(String subestacaoConexao) { this.subestacaoConexao = subestacaoConexao; }
    public BigDecimal getPrecoPpaMwh() { return precoPpaMwh; }
    public void setPrecoPpaMwh(BigDecimal precoPpaMwh) { this.precoPpaMwh = precoPpaMwh; }
}

package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PldHorario {
    private Long id;
    private Integer mesReferencia;
    private String submercado;
    private Integer periodoComercializacao;
    private LocalDate dia;
    private Integer hora;
    private BigDecimal pldHora;

    public PldHorario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(Integer mesReferencia) { this.mesReferencia = mesReferencia; }
    public String getSubmercado() { return submercado; }
    public void setSubmercado(String submercado) { this.submercado = submercado; }
    public Integer getPeriodoComercializacao() { return periodoComercializacao; }
    public void setPeriodoComercializacao(Integer periodoComercializacao) { this.periodoComercializacao = periodoComercializacao; }
    public LocalDate getDia() { return dia; }
    public void setDia(LocalDate dia) { this.dia = dia; }
    public Integer getHora() { return hora; }
    public void setHora(Integer hora) { this.hora = hora; }
    public BigDecimal getPldHora() { return pldHora; }
    public void setPldHora(BigDecimal pldHora) { this.pldHora = pldHora; }
}

package com.curtavento.curta_vento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pld_horario", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dia", "hora", "submercado", "periodo_comercializacao"})
})
public class PldHorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes_referencia", nullable = false)
    private Integer mesReferencia;

    @Column(nullable = false, length = 50)
    private String submercado;

    @Column(name = "periodo_comercializacao", nullable = false)
    private Integer periodoComercializacao;

    @Column(nullable = false)
    private LocalDate dia;

    @Column(nullable = false)
    private Integer hora;

    @Column(name = "pld_hora", nullable = false, precision = 12, scale = 2)
    private BigDecimal pldHora;

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

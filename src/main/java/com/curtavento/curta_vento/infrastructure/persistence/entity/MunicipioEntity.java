package com.curtavento.curta_vento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "municipio")
public class MunicipioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "codigo_ibge", nullable = false, unique = true)
    private String codigoIbge;

    @Column(name = "ipm", precision = 10, scale = 7)
    private BigDecimal ipm;

    @Column(name = "vaf_ano_base")
    private BigDecimal vafAnoBase;

    @Column(name = "ano_base")
    private Integer anoBase;

    public MunicipioEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigoIbge() { return codigoIbge; }
    public void setCodigoIbge(String codigoIbge) { this.codigoIbge = codigoIbge; }
    public BigDecimal getIpm() { return ipm; }
    public void setIpm(BigDecimal ipm) { this.ipm = ipm; }
    public BigDecimal getVafAnoBase() { return vafAnoBase; }
    public void setVafAnoBase(BigDecimal vafAnoBase) { this.vafAnoBase = vafAnoBase; }
    public Integer getAnoBase() { return anoBase; }
    public void setAnoBase(Integer anoBase) { this.anoBase = anoBase; }
}

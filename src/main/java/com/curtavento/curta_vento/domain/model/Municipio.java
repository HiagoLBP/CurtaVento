package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;

public class Municipio {
    private Long id;
    private String nome;
    private String codigoIbge;
    private BigDecimal ipm;
    private BigDecimal vafAnoBase;
    private Integer anoBase;

    public Municipio() {}

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

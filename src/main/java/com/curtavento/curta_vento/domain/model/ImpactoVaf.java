package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ImpactoVaf {
    private Long id;
    private Municipio municipio;
    private Integer anoReferencia;
    private BigDecimal energiaTotalCortadaMwh;
    private BigDecimal perdaReceitaEstimada;
    private BigDecimal impactoVafEstimado;
    private LocalDateTime dataCalculo;
    private boolean financeiroDisponivel;

    public ImpactoVaf() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
    public Integer getAnoReferencia() { return anoReferencia; }
    public void setAnoReferencia(Integer anoReferencia) { this.anoReferencia = anoReferencia; }
    public BigDecimal getEnergiaTotalCortadaMwh() { return energiaTotalCortadaMwh; }
    public void setEnergiaTotalCortadaMwh(BigDecimal energiaTotalCortadaMwh) { this.energiaTotalCortadaMwh = energiaTotalCortadaMwh; }
    public BigDecimal getPerdaReceitaEstimada() { return perdaReceitaEstimada; }
    public void setPerdaReceitaEstimada(BigDecimal perdaReceitaEstimada) { this.perdaReceitaEstimada = perdaReceitaEstimada; }
    public BigDecimal getImpactoVafEstimado() { return impactoVafEstimado; }
    public void setImpactoVafEstimado(BigDecimal impactoVafEstimado) { this.impactoVafEstimado = impactoVafEstimado; }
    public LocalDateTime getDataCalculo() { return dataCalculo; }
    public void setDataCalculo(LocalDateTime dataCalculo) { this.dataCalculo = dataCalculo; }
    public boolean isFinanceiroDisponivel() { return financeiroDisponivel; }
    public void setFinanceiroDisponivel(boolean financeiroDisponivel) { this.financeiroDisponivel = financeiroDisponivel; }
}
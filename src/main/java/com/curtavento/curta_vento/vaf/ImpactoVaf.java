package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.municipio.Municipio;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "impacto_vaf")
public class ImpactoVaf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @Column(name = "energia_total_cortada_mwh", nullable = false)
    private BigDecimal energiaTotalCortadaMwh;

    @Column(name = "perda_receita_estimada", nullable = false)
    private BigDecimal perdaReceitaEstimada;

    @Column(name = "impacto_vaf_estimado", nullable = false)
    private BigDecimal impactoVafEstimado;

    @Column(name = "data_calculo", nullable = false)
    private LocalDateTime dataCalculo;

    public ImpactoVaf() {
    }

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
}
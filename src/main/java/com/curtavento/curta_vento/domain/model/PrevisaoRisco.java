package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrevisaoRisco {
    private Long id;
    private Usina usina;
    private LocalDate dataReferencia;
    private Integer horizonteHoras;
    private NivelRisco nivelRisco;
    private BigDecimal percentualCorteEstimado;
    private String fatoresJustificativa;
    private LocalDateTime criadoEm;

    public PrevisaoRisco() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }
    public LocalDate getDataReferencia() { return dataReferencia; }
    public void setDataReferencia(LocalDate dataReferencia) { this.dataReferencia = dataReferencia; }
    public Integer getHorizonteHoras() { return horizonteHoras; }
    public void setHorizonteHoras(Integer horizonteHoras) { this.horizonteHoras = horizonteHoras; }
    public NivelRisco getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(NivelRisco nivelRisco) { this.nivelRisco = nivelRisco; }
    public BigDecimal getPercentualCorteEstimado() { return percentualCorteEstimado; }
    public void setPercentualCorteEstimado(BigDecimal percentualCorteEstimado) { this.percentualCorteEstimado = percentualCorteEstimado; }
    public String getFatoresJustificativa() { return fatoresJustificativa; }
    public void setFatoresJustificativa(String fatoresJustificativa) { this.fatoresJustificativa = fatoresJustificativa; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
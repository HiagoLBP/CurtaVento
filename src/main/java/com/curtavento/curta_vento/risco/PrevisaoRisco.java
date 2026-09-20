package com.curtavento.curta_vento.risco;

import com.curtavento.curta_vento.usina.Usina;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "previsao_risco")
public class PrevisaoRisco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usina_id", nullable = false)
    private Usina usina;

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Column(name = "horizonte_horas", nullable = false)
    private Integer horizonteHoras;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_risco", nullable = false)
    private NivelRisco nivelRisco;

    @Column(name = "percentual_corte_estimado")
    private BigDecimal percentualCorteEstimado;

    @Column(name = "fatores_justificativa", columnDefinition = "TEXT")
    private String fatoresJustificativa;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public PrevisaoRisco() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usina getUsina() {
        return usina;
    }

    public void setUsina(Usina usina) {
        this.usina = usina;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public Integer getHorizonteHoras() {
        return horizonteHoras;
    }

    public void setHorizonteHoras(Integer horizonteHoras) {
        this.horizonteHoras = horizonteHoras;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public BigDecimal getPercentualCorteEstimado() {
        return percentualCorteEstimado;
    }

    public void setPercentualCorteEstimado(BigDecimal percentualCorteEstimado) {
        this.percentualCorteEstimado = percentualCorteEstimado;
    }

    public String getFatoresJustificativa() {
        return fatoresJustificativa;
    }

    public void setFatoresJustificativa(String fatoresJustificativa) {
        this.fatoresJustificativa = fatoresJustificativa;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
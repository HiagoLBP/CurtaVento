package com.curtavento.curta_vento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_corte_ons")
public class HistoricoCorteOnsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usina_id", nullable = false)
    private UsinaEntity usina;

    @Column(name = "data_corte", nullable = false)
    private LocalDateTime dataCorte;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "energia_cortada_mwh")
    private BigDecimal energiaCortadaMwh;

    @Column(name = "motivo_ons")
    private String motivoOns;

    @Column(name = "energia_estimada_mwh", precision = 14, scale = 4)
    private BigDecimal energiaEstimadaMwh;

    @Column(name = "energia_verificada_mwh", precision = 14, scale = 4)
    private BigDecimal energiaVerificadaMwh;

    @Column(name = "cod_razao_restricao", length = 30)
    private String codRazaoRestricao;

    @Column(name = "dsc_restricao", length = 500)
    private String dscRestricao;

    @Column(name = "subestacao_conexao", length = 150)
    private String subestacaoConexao;

    @Column(name = "vento_verificado_ms", precision = 8, scale = 2)
    private BigDecimal ventoVerificadoMs;

    @Column(name = "valorado_pld_rs", precision = 16, scale = 2)
    private BigDecimal valoradoPldRs;

    public HistoricoCorteOnsEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UsinaEntity getUsina() { return usina; }
    public void setUsina(UsinaEntity usina) { this.usina = usina; }
    public LocalDateTime getDataCorte() { return dataCorte; }
    public void setDataCorte(LocalDateTime dataCorte) { this.dataCorte = dataCorte; }
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public BigDecimal getEnergiaCortadaMwh() { return energiaCortadaMwh; }
    public void setEnergiaCortadaMwh(BigDecimal energiaCortadaMwh) { this.energiaCortadaMwh = energiaCortadaMwh; }
    public String getMotivoOns() { return motivoOns; }
    public void setMotivoOns(String motivoOns) { this.motivoOns = motivoOns; }
    public BigDecimal getEnergiaEstimadaMwh() { return energiaEstimadaMwh; }
    public void setEnergiaEstimadaMwh(BigDecimal energiaEstimadaMwh) { this.energiaEstimadaMwh = energiaEstimadaMwh; }
    public BigDecimal getEnergiaVerificadaMwh() { return energiaVerificadaMwh; }
    public void setEnergiaVerificadaMwh(BigDecimal energiaVerificadaMwh) { this.energiaVerificadaMwh = energiaVerificadaMwh; }
    public String getCodRazaoRestricao() { return codRazaoRestricao; }
    public void setCodRazaoRestricao(String codRazaoRestricao) { this.codRazaoRestricao = codRazaoRestricao; }
    public String getDscRestricao() { return dscRestricao; }
    public void setDscRestricao(String dscRestricao) { this.dscRestricao = dscRestricao; }
    public String getSubestacaoConexao() { return subestacaoConexao; }
    public void setSubestacaoConexao(String subestacaoConexao) { this.subestacaoConexao = subestacaoConexao; }
    public BigDecimal getVentoVerificadoMs() { return ventoVerificadoMs; }
    public void setVentoVerificadoMs(BigDecimal ventoVerificadoMs) { this.ventoVerificadoMs = ventoVerificadoMs; }
    public BigDecimal getValoradoPldRs() { return valoradoPldRs; }
    public void setValoradoPldRs(BigDecimal valoradoPldRs) { this.valoradoPldRs = valoradoPldRs; }
}

package com.curtavento.curta_vento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_corte_ons_bloco", uniqueConstraints = {
        @UniqueConstraint(name = "uk_corte_ons_bloco_usina_instante", columnNames = {"usina_id", "instante"})
})
public class HistoricoCorteOnsBlocoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usina_id", nullable = false)
    private UsinaEntity usina;

    @Column(nullable = false)
    private LocalDateTime instante;

    @Column(name = "geracao_estimada_mw", nullable = false, precision = 14, scale = 6)
    private BigDecimal geracaoEstimadaMw;

    @Column(name = "geracao_verificada_mw", nullable = false, precision = 14, scale = 6)
    private BigDecimal geracaoVerificadaMw;

    @Column(name = "energia_cortada_mwh", nullable = false, precision = 14, scale = 6)
    private BigDecimal energiaCortadaMwh;

    @Column(name = "motivo_ons")
    private String motivoOns;

    @Column(name = "cod_razao_restricao", length = 30)
    private String codRazaoRestricao;

    @Column(name = "dsc_restricao", length = 500)
    private String dscRestricao;

    @Column(name = "subestacao_conexao", length = 150)
    private String subestacaoConexao;

    @Column(name = "vento_verificado_ms", precision = 8, scale = 2)
    private BigDecimal ventoVerificadoMs;

    @Column(name = "pld_mwh", precision = 12, scale = 2)
    private BigDecimal pldMwh;

    public HistoricoCorteOnsBlocoEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UsinaEntity getUsina() { return usina; }
    public void setUsina(UsinaEntity usina) { this.usina = usina; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public BigDecimal getGeracaoEstimadaMw() { return geracaoEstimadaMw; }
    public void setGeracaoEstimadaMw(BigDecimal geracaoEstimadaMw) { this.geracaoEstimadaMw = geracaoEstimadaMw; }
    public BigDecimal getGeracaoVerificadaMw() { return geracaoVerificadaMw; }
    public void setGeracaoVerificadaMw(BigDecimal geracaoVerificadaMw) { this.geracaoVerificadaMw = geracaoVerificadaMw; }
    public BigDecimal getEnergiaCortadaMwh() { return energiaCortadaMwh; }
    public void setEnergiaCortadaMwh(BigDecimal energiaCortadaMwh) { this.energiaCortadaMwh = energiaCortadaMwh; }
    public String getMotivoOns() { return motivoOns; }
    public void setMotivoOns(String motivoOns) { this.motivoOns = motivoOns; }
    public String getCodRazaoRestricao() { return codRazaoRestricao; }
    public void setCodRazaoRestricao(String codRazaoRestricao) { this.codRazaoRestricao = codRazaoRestricao; }
    public String getDscRestricao() { return dscRestricao; }
    public void setDscRestricao(String dscRestricao) { this.dscRestricao = dscRestricao; }
    public String getSubestacaoConexao() { return subestacaoConexao; }
    public void setSubestacaoConexao(String subestacaoConexao) { this.subestacaoConexao = subestacaoConexao; }
    public BigDecimal getVentoVerificadoMs() { return ventoVerificadoMs; }
    public void setVentoVerificadoMs(BigDecimal ventoVerificadoMs) { this.ventoVerificadoMs = ventoVerificadoMs; }
    public BigDecimal getPldMwh() { return pldMwh; }
    public void setPldMwh(BigDecimal pldMwh) { this.pldMwh = pldMwh; }
}

package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.usina.Usina;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_corte_ons_bloco",
        uniqueConstraints = @UniqueConstraint(name = "uk_corte_ons_bloco_usina_instante",
                columnNames = {"usina_id", "instante"}))
public class HistoricoCorteOnsBloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usina_id", nullable = false)
    private Usina usina;

    @Column(nullable = false)
    private LocalDateTime instante;

    @Column(name = "geracao_estimada_mw", nullable = false, precision = 14, scale = 6)
    private BigDecimal geracaoEstimadaMw;

    @Column(name = "geracao_verificada_mw", nullable = false, precision = 14, scale = 6)
    private BigDecimal geracaoVerificadaMw;

    @Column(name = "energia_cortada_mwh", nullable = false, precision = 14, scale = 6)
    private BigDecimal energiaCortadaMwh;

    @Column(name = "motivo_ons", length = 255)
    private String motivoOns;

    public Long getId() { return id; }
    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public BigDecimal getGeracaoEstimadaMw() { return geracaoEstimadaMw; }
    public void setGeracaoEstimadaMw(BigDecimal valor) { this.geracaoEstimadaMw = valor; }
    public BigDecimal getGeracaoVerificadaMw() { return geracaoVerificadaMw; }
    public void setGeracaoVerificadaMw(BigDecimal valor) { this.geracaoVerificadaMw = valor; }
    public BigDecimal getEnergiaCortadaMwh() { return energiaCortadaMwh; }
    public void setEnergiaCortadaMwh(BigDecimal valor) { this.energiaCortadaMwh = valor; }
    public String getMotivoOns() { return motivoOns; }
    public void setMotivoOns(String motivoOns) { this.motivoOns = motivoOns; }
}

package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.usina.Usina;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_corte_ons")
public class HistoricoCorteOns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usina_id", nullable = false)
    private Usina usina;

    @Column(name = "data_corte", nullable = false)
    private LocalDateTime dataCorte;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "energia_cortada_mwh")
    private BigDecimal energiaCortadaMwh;

    @Column(name = "motivo_ons")
    private String motivoOns;

    public HistoricoCorteOns() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }

    public LocalDateTime getDataCorte() { return dataCorte; }
    public void setDataCorte(LocalDateTime dataCorte) { this.dataCorte = dataCorte; }

    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public BigDecimal getEnergiaCortadaMwh() { return energiaCortadaMwh; }
    public void setEnergiaCortadaMwh(BigDecimal energiaCortadaMwh) { this.energiaCortadaMwh = energiaCortadaMwh; }

    public String getMotivoOns() { return motivoOns; }
    public void setMotivoOns(String motivoOns) { this.motivoOns = motivoOns; }
}
package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistoricoCorteOns {
    private Long id;
    private Usina usina;
    private LocalDateTime dataCorte;
    private Integer duracaoMinutos;
    private BigDecimal energiaCortadaMwh;
    private String motivoOns;

    public HistoricoCorteOns() {}

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
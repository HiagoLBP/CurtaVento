package com.curtavento.curta_vento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LeituraClimatica {
    private Long id;
    private Usina usina;
    private LocalDateTime dataHora;
    private BigDecimal velocidadeVentoMs;
    private BigDecimal ventoPrevisao24hMs;
    private BigDecimal ventoPrevisao48hMs;
    private BigDecimal direcaoVentoGraus;
    private BigDecimal temperaturaC;
    private String fonte;

    public LeituraClimatica() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public BigDecimal getVelocidadeVentoMs() { return velocidadeVentoMs; }
    public void setVelocidadeVentoMs(BigDecimal velocidadeVentoMs) { this.velocidadeVentoMs = velocidadeVentoMs; }
    public BigDecimal getVentoPrevisao24hMs() { return ventoPrevisao24hMs; }
    public void setVentoPrevisao24hMs(BigDecimal ventoPrevisao24hMs) { this.ventoPrevisao24hMs = ventoPrevisao24hMs; }
    public BigDecimal getVentoPrevisao48hMs() { return ventoPrevisao48hMs; }
    public void setVentoPrevisao48hMs(BigDecimal ventoPrevisao48hMs) { this.ventoPrevisao48hMs = ventoPrevisao48hMs; }
    public BigDecimal getDirecaoVentoGraus() { return direcaoVentoGraus; }
    public void setDirecaoVentoGraus(BigDecimal direcaoVentoGraus) { this.direcaoVentoGraus = direcaoVentoGraus; }
    public BigDecimal getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(BigDecimal temperaturaC) { this.temperaturaC = temperaturaC; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
}
package com.curtavento.curta_vento.clima;

import com.curtavento.curta_vento.usina.Usina;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leitura_climatica")
public class LeituraClimatica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento: Várias leituras climáticas pertencem a uma única Usina
    @ManyToOne
    @JoinColumn(name = "usina_id", nullable = false)
    private Usina usina;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "velocidade_vento_ms")
    private BigDecimal velocidadeVentoMs;

    @Column(name = "direcao_vento_graus")
    private BigDecimal direcaoVentoGraus;

    @Column(name = "temperatura_c")
    private BigDecimal temperaturaC;

    private String fonte;

    public LeituraClimatica() {
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public BigDecimal getVelocidadeVentoMs() {
        return velocidadeVentoMs;
    }

    public void setVelocidadeVentoMs(BigDecimal velocidadeVentoMs) {
        this.velocidadeVentoMs = velocidadeVentoMs;
    }

    public BigDecimal getDirecaoVentoGraus() {
        return direcaoVentoGraus;
    }

    public void setDirecaoVentoGraus(BigDecimal direcaoVentoGraus) {
        this.direcaoVentoGraus = direcaoVentoGraus;
    }

    public BigDecimal getTemperaturaC() {
        return temperaturaC;
    }

    public void setTemperaturaC(BigDecimal temperaturaC) {
        this.temperaturaC = temperaturaC;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }
}
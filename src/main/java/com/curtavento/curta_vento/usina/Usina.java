package com.curtavento.curta_vento.usina;

import com.curtavento.curta_vento.municipio.Municipio;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "usina")
public class Usina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String operador;

    // É aqui que fazemos a ligação entre as tabelas!
    @ManyToOne
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @Column(name = "capacidade_instalada_mw")
    private BigDecimal capacidadeInstaladaMw;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Column(name = "data_inicio_operacao")
    private LocalDate dataInicioOperacao;

    public Usina() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public BigDecimal getCapacidadeInstaladaMw() {
        return capacidadeInstaladaMw;
    }

    public void setCapacidadeInstaladaMw(BigDecimal capacidadeInstaladaMw) {
        this.capacidadeInstaladaMw = capacidadeInstaladaMw;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocalDate getDataInicioOperacao() {
        return dataInicioOperacao;
    }

    public void setDataInicioOperacao(LocalDate dataInicioOperacao) {
        this.dataInicioOperacao = dataInicioOperacao;
    }
}

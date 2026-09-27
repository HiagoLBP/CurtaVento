package com.curtavento.curta_vento.geracao;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "geracao_ons_horaria",
        uniqueConstraints = @UniqueConstraint(name = "uk_geracao_ons_horaria",
                columnNames = {"id_ons", "instante", "fonte"}))
public class GeracaoOnsHoraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_ons", nullable = false, length = 100)
    private String idOns;

    @Column(name = "nome_usina_ons", nullable = false, length = 255)
    private String nomeUsinaOns;

    @Column(nullable = false)
    private LocalDateTime instante;

    @Column(name = "geracao_mwh", nullable = false, precision = 19, scale = 6)
    private BigDecimal geracaoMwh;

    @Column(nullable = false, length = 80)
    private String fonte;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdOns() { return idOns; }
    public void setIdOns(String idOns) { this.idOns = idOns; }
    public String getNomeUsinaOns() { return nomeUsinaOns; }
    public void setNomeUsinaOns(String nomeUsinaOns) { this.nomeUsinaOns = nomeUsinaOns; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public BigDecimal getGeracaoMwh() { return geracaoMwh; }
    public void setGeracaoMwh(BigDecimal geracaoMwh) { this.geracaoMwh = geracaoMwh; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
}

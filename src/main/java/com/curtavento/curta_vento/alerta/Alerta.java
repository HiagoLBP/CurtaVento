package com.curtavento.curta_vento.alerta;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.municipio.Municipio;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", nullable = false)
    private String tipo; // RISCO_CURTAILMENT ou QUEDA_VAF

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "nivel_severidade", nullable = false)
    private String nivelSeveridade; // INFO, WARNING, ALTO, CRITICO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usina_id")
    private Usina usina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "lido", nullable = false)
    private Boolean lido = false;

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getNivelSeveridade() { return nivelSeveridade; }
    public void setNivelSeveridade(String nivelSeveridade) { this.nivelSeveridade = nivelSeveridade; }

    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }

    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Boolean getLido() { return lido; }
    public void setLido(Boolean lido) { this.lido = lido; }
}

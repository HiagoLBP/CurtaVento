package com.curtavento.curta_vento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ons_sync_checkpoint")
public class OnsSyncCheckpointEntity {

    @Id
    @Column(name = "chave", length = 48)
    private String chave;

    @Column(name = "last_modified_epoch", nullable = false)
    private Long lastModifiedEpoch;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public OnsSyncCheckpointEntity() {}

    public OnsSyncCheckpointEntity(String chave, Long lastModifiedEpoch, LocalDateTime atualizadoEm) {
        this.chave = chave;
        this.lastModifiedEpoch = lastModifiedEpoch;
        this.atualizadoEm = atualizadoEm;
    }

    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }
    public Long getLastModifiedEpoch() { return lastModifiedEpoch; }
    public void setLastModifiedEpoch(Long lastModifiedEpoch) { this.lastModifiedEpoch = lastModifiedEpoch; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}

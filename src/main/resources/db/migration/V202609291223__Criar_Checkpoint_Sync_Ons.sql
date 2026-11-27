-- Checkpoint persistente de sincronizacao S3 ONS (evita perda de revisoes M-1/M-2 apos reinicio)
CREATE TABLE IF NOT EXISTS ons_sync_checkpoint (
    chave VARCHAR(48) PRIMARY KEY,
    last_modified_epoch BIGINT NOT NULL,
    atualizado_em DATETIME NOT NULL
);

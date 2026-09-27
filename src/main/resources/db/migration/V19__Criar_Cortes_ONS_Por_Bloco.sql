CREATE TABLE historico_corte_ons_bloco (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usina_id BIGINT NOT NULL,
    instante DATETIME NOT NULL,
    geracao_estimada_mw DECIMAL(14, 6) NOT NULL,
    geracao_verificada_mw DECIMAL(14, 6) NOT NULL,
    energia_cortada_mwh DECIMAL(14, 6) NOT NULL,
    motivo_ons VARCHAR(255),
    CONSTRAINT fk_corte_ons_bloco_usina FOREIGN KEY (usina_id) REFERENCES usina(id),
    CONSTRAINT uk_corte_ons_bloco_usina_instante UNIQUE (usina_id, instante)
);

CREATE INDEX idx_corte_ons_bloco_instante ON historico_corte_ons_bloco (instante);

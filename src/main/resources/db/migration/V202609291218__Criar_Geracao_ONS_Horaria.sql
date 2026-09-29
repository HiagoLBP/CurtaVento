CREATE TABLE geracao_ons_horaria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_ons VARCHAR(100) NOT NULL,
    nome_usina_ons VARCHAR(255) NOT NULL,
    instante DATETIME NOT NULL,
    geracao_mwh DECIMAL(19, 6) NOT NULL,
    fonte VARCHAR(80) NOT NULL,
    CONSTRAINT uk_geracao_ons_horaria UNIQUE (id_ons, instante, fonte),
    INDEX idx_geracao_ons_horaria_instante (instante),
    INDEX idx_geracao_ons_horaria_usina (id_ons)
);

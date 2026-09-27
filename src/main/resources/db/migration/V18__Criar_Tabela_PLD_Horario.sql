CREATE TABLE pld_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mes_referencia INT NOT NULL,
    submercado VARCHAR(20) NOT NULL,
    periodo_comercializacao INT NOT NULL,
    dia DATE NOT NULL,
    hora INT NOT NULL,
    pld_hora DECIMAL(12, 2) NOT NULL,
    CONSTRAINT uk_pld_horario_chave UNIQUE (
        mes_referencia, submercado, periodo_comercializacao, dia, hora
    )
);

CREATE INDEX idx_pld_horario_data_submercado
    ON pld_horario (dia, hora, submercado);

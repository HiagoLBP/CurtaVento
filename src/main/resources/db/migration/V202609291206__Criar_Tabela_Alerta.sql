CREATE TABLE alerta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    mensagem TEXT NOT NULL,
    nivel_severidade VARCHAR(20) NOT NULL,
    usina_id BIGINT,
    municipio_id BIGINT,
    data_criacao TIMESTAMP NOT NULL,
    lido BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_alerta_usina FOREIGN KEY (usina_id) REFERENCES usina(id),
    CONSTRAINT fk_alerta_municipio FOREIGN KEY (municipio_id) REFERENCES municipio(id)
);

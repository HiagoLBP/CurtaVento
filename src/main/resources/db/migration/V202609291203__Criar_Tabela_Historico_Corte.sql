CREATE TABLE historico_corte_ons (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     usina_id BIGINT NOT NULL,
                                     data_corte DATETIME NOT NULL,
                                     duracao_minutos INT NOT NULL,
                                     energia_cortada_mwh DECIMAL(10, 2),
                                     motivo_ons VARCHAR(255),
                                     CONSTRAINT fk_historico_usina FOREIGN KEY (usina_id) REFERENCES usina(id)
);
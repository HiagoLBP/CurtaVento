CREATE TABLE previsao_risco (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                usina_id BIGINT NOT NULL,
                                data_referencia DATE NOT NULL,
                                horizonte_horas INT NOT NULL,
                                nivel_risco VARCHAR(20) NOT NULL,
                                percentual_corte_estimado DECIMAL(5, 2),
                                fatores_justificativa TEXT,
                                criado_em DATETIME NOT NULL,
                                CONSTRAINT fk_previsao_usina FOREIGN KEY (usina_id) REFERENCES usina(id)
);
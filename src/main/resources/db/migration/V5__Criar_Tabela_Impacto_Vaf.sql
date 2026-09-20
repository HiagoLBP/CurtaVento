CREATE TABLE impacto_vaf (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             municipio_id BIGINT NOT NULL,
                             ano_referencia INT NOT NULL,
                             energia_total_cortada_mwh DECIMAL(15, 2) NOT NULL,
                             perda_receita_estimada DECIMAL(15, 2) NOT NULL,
                             impacto_vaf_estimado DECIMAL(15, 2) NOT NULL,
                             data_calculo DATETIME NOT NULL,
                             CONSTRAINT fk_impacto_municipio FOREIGN KEY (municipio_id) REFERENCES municipio(id)
);
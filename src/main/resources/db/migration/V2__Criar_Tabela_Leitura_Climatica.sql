CREATE TABLE leitura_climatica (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   usina_id BIGINT NOT NULL,
                                   data_hora DATETIME NOT NULL,
                                   velocidade_vento_ms DECIMAL(5, 2),
                                   direcao_vento_graus DECIMAL(5, 2),
                                   temperatura_c DECIMAL(5, 2),
                                   fonte VARCHAR(50),
                                   CONSTRAINT fk_leitura_usina FOREIGN KEY (usina_id) REFERENCES usina(id)
);
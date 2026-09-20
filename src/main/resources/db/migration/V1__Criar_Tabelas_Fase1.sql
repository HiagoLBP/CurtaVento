CREATE TABLE municipio (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nome VARCHAR(255) NOT NULL,
                           codigo_ibge VARCHAR(50) NOT NULL UNIQUE,
                           vaf_ano_base DECIMAL(19, 2),
                           ano_base INT
);

CREATE TABLE usina (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(255) NOT NULL,
                       operador VARCHAR(100) NOT NULL,
                       municipio_id BIGINT NOT NULL,
                       capacidade_instalada_mw DECIMAL(10, 2),
                       latitude DECIMAL(10, 8),
                       longitude DECIMAL(11, 8),
                       data_inicio_operacao DATE,
                       CONSTRAINT fk_usina_municipio FOREIGN KEY (municipio_id) REFERENCES municipio(id)
);

CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         senha_hash VARCHAR(255) NOT NULL,
                         perfil VARCHAR(50) NOT NULL,
                         usina_id BIGINT,
                         municipio_id BIGINT,
                         CONSTRAINT fk_usuario_usina FOREIGN KEY (usina_id) REFERENCES usina(id),
                         CONSTRAINT fk_usuario_municipio FOREIGN KEY (municipio_id) REFERENCES municipio(id)
);
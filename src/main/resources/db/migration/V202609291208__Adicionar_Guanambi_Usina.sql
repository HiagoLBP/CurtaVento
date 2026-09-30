-- Assegura que o municipio de Guanambi exista com ID 1 antes de inserir a usina
INSERT INTO municipio (id, nome, codigo_ibge) 
SELECT 1, 'Guanambi', '2911709' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE id = 1) 
AND NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Guanambi');

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude, data_inicio_operacao) 
VALUES ('Conj. Pinda II', 'Renova Energia / AES', 1, 150.0, -14.4177372, -42.6262774, '2016-01-01');
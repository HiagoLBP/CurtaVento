-- 1. Inserir municipios faltantes (usando uma subquery para evitar duplicacao)
INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Araci', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Araci' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Biritinga', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Biritinga' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Boninal', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Boninal' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Bonito', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Bonito' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Brotas de Macaúbas', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Brotas de Macaúbas' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Brumado', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Brumado' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Caetité', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Caetité' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Cafarnaum', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Cafarnaum' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Campo Formoso', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Canudos', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Canudos' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Casa Nova', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Casa Nova' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Gentio do Ouro', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Guanambi', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Ibipeba', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Ibitiara', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Ibitiara' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Igaporã', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Iraquara', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Iraquara' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Itaguaçu da Bahia', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Itaguaçu da Bahia' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Jacaraci', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Jacaraci' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Licínio de Almeida', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Licínio de Almeida' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Morro do Chapéu', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Mulungu do Morro', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Novo Horizonte', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Novo Horizonte' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Ourolândia', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Pindaí', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Riacho de Santana', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Riacho de Santana' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Sento Sé', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Sobradinho', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Sobradinho' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Souto Soares', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Souto Soares' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Tanque Novo', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Tucano', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Tucano' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Uibaí', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Uibaí' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Umburanas', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Umburanas' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Urandi', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Urandi' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Várzea Nova', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA');

INSERT INTO municipio (nome, estado, indice_vaf)
SELECT 'Xique', 'BA', 0.001
WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Xique' AND estado = 'BA');

-- 2. Inserir as usinas com subquery para pegar o ID do municipio correto
INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Macaúbas', id, 35.07, -12.35119149, -42.33852459, 'MACAUBAS ENERGETICA SA'
FROM municipio WHERE nome = 'Brotas de Macaúbas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra do Reino', id, 30.0, -9.51754829, -40.88639419, 'EOLICA PEDRA DO REINO S.A.'
FROM municipio WHERE nome = 'Sobradinho' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Novo Horizonte', id, 30.06, -12.31269913, -42.35098757, 'NOVO HORIZONTE ENERGETICA S/A'
FROM municipio WHERE nome = 'Brotas de Macaúbas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Seabra', id, 35.39, -12.27870333, -42.36260411, 'SEABRA ENERGETICA S/A'
FROM municipio WHERE nome = 'Brotas de Macaúbas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Salto', id, 19.2, -14.15545583, -42.63821972, 'CENTRAIS EOLICAS SERRA DO SALTO LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ilhéus', id, 11.2, -13.8823, -42.66305, 'CENTRAIS EOLICAS ILHEUS LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Candiba', id, 9.6, -14.18314712, -42.62274949, 'CENTRAIS EOLICAS CANDIBA LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Licínio de Almeida', id, 24.0, -14.17694395, -42.6540627, 'CENTRAIS EOLICAS LICINIO DE ALMEIDA LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Nossa Senhora da Conceição', id, 28.8, -13.89569444, -42.67688889, 'CENTRAIS EOLICAS NOSSA SENHORA DA CONCEICAO LTDA.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pajeú do Vento', id, 25.6, -14.01475, -42.63386111, 'CENTRAIS EOLICAS PAJEU DO VENTO LTDA.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Alvorada', id, 8.0, -14.18515114, -42.5910726, 'CENTRAIS EOLICAS ALVORADA LTDA.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Igaporã', id, 30.4, -13.86057434, -42.6573417, 'CENTRAIS EOLICAS IGAPORA LTDA.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Planaltina', id, 27.2, -13.98794444, -42.63894444, 'CENTRAIS EOLICAS PLANALTINA LTDA.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Porto Seguro', id, 6.4, -13.86294444, -42.67266667, 'CENTRAIS EOLICAS PORTO SEGURO LTDA.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pindaí', id, 24.0, -14.20213939, -42.65889865, 'CENTRAIS EOLICAS PINDAI LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Guanambi', id, 20.8, -14.20170756, -42.61830984, 'CENTRAIS EOLICAS GUANAMBI LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Rio Verde', id, 30.4, -14.16402778, -42.60055556, 'CENTRAIS EOLICAS RIO VERDE LTDA.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Guirapá', id, 28.8, -14.21891461, -42.6413885, 'CENTRAIS EOLICAS GUIRAPA LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité 2', id, 30.0, -14.185, -42.503, 'CAETITE 2 ENERGIA RENOVAVEL S/A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra Branca', id, 30.0, -9.87459153, -41.09571479, 'Pedra Branca S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité 3', id, 30.0, -14.21944444, -42.507, 'CAETITE 3 ENERGIA RENOVAVEL S/A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Sete Gameleiras', id, 30.0, -9.86384384, -41.10417464, 'SETE GAMELEIRAS S/A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'São Pedro do Lago', id, 31.46, -9.87469326, -41.09301108, 'São Pedro do Lago S.A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos do Nordeste', id, 23.52, -14.11946418, -42.61408311, 'CENTRAIS EOLICAS VENTOS DO NORDESTE S/A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Da Prata', id, 21.84, -13.96426616, -42.63886294, 'CENTRAIS EOLICAS DA PRATA S.A'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Dos Araçás', id, 31.86, -14.12959332, -42.55965544, 'Centrais Eólicas dos Araçás S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Morrão', id, 30.24, -14.08850925, -42.68270797, 'CENTRAIS EOLICAS MORRAO S/A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tanque', id, 30.0, -14.1025978, -42.61065719, 'CENTRAIS EOLICAS TANQUE S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Seraíma', id, 30.24, -14.14727297, -42.71185123, 'CENTRAIS EOLICAS SERAIMA S.A'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra do Reino III', id, 18.0, -9.49858333, -40.879, 'EOLICATEC SOBRADINHO S.A.'
FROM municipio WHERE nome = 'Sobradinho' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'São Judas', id, 29.9, -11.66425, -41.362, 'ENEL GREEN POWER SAO JUDAS EOLICA S/A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Primavera', id, 29.9, -11.73336111, -41.373, 'ENEL GREEN POWER PRIMAVERA EOLICA S/A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Cristal', id, 29.9, -11.79646316, -41.3933842, 'ENEL GREEN POWER CRISTAL EOLICA S/A'
FROM municipio WHERE nome = 'Cafarnaum' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Teiu', id, 16.65, -14.3381384, -42.59109055, 'CENTRAL EOLICA TEIU S/A'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Angical', id, 12.95, -14.34605667, -42.61368354, 'CENTRAL EOLICA ANGICAL S/A'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Coqueirinho', id, 29.6, -14.41988897, -42.62872019, 'CENTRAL EOLICA COQUEIRINHO S/A'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caititu', id, 22.2, -14.45280192, -42.64099568, 'CENTRAL EOLICA CAITITU S/A'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Inhambu', id, 31.45, -14.39117106, -42.5938536, 'CENTRAL EOLICA INHAMBU S/A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Corrupião', id, 27.75, -14.29269078, -42.58526944, 'CENTRAL EOLICA CORRUPIAO S/A'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tamanduá Mirim', id, 29.6, -14.377552, -42.60938646, 'CENTRAL EOLICA TAMANDUA MIRIM S/A'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Maron', id, 30.24, -14.13370646, -42.60235759, 'CENTRAIS EOLICAS MARON S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pilões', id, 30.24, -14.02915106, -42.61607574, 'CENTRAIS EOLICAS PILOES S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Dourados', id, 28.56, -14.15232508, -42.68159246, 'CENTRAIS EOLICAS DOURADOS S.A'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ametista', id, 28.56, -14.17203284, -42.70022351, 'CENTRAIS EOLICAS AMETISTA S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité', id, 30.24, -14.46886215, -42.58355855, 'CENTRAIS EOLICAS CAETITE S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pelourinho', id, 21.84, -14.52923503, -42.58673115, 'CENTRAIS EOLICAS PELOURINHO S.A'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Joana', id, 28.2, -13.91798451, -42.62044581, 'ENEL GREEN POWER JOANA EOLICA S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Espinhaço', id, 18.48, -14.49572374, -42.58285958, 'CENTRAIS EOLICAS SERRA DO ESPINHACO S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Espigão', id, 10.08, -14.47696269, -42.5852515, 'CENTRAIS EOLICAS ESPIGAO S.A'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Emiliana', id, 28.2, -13.91002361, -42.62417205, 'ENEL GREEN POWER EMILIANA EOLICA S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Borgo', id, 20.16, -14.50840054, -42.58665108, 'CENTRAIS EOLICAS BORGO S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Campo Formoso I', id, 30.0, -10.62770186, -40.68553526, 'CAMPO FORMOSO I ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Andorinha', id, 30.0, -10.583277, -40.63744738, 'ANDORINHA ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Morrinhos', id, 30.0, -10.56956163, -40.62858262, 'MORRINHOS ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos do Sertão', id, 30.0, -10.55667941, -40.5686764, 'SERTAO ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité 1', id, 30.0, -14.16321686, -42.4844902, 'CAETITE 1 ENERGIA RENOVAVEL S/A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Campo Formoso II', id, 30.0, -10.51956005, -40.61138421, 'CAMPO FORMOSO II ENERGIAS RENOVAVEIS S. A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'São Salvador', id, 18.9, -13.83791923, -42.64863784, 'CENTRAIS EOLICAS SAO SALVADOR S.A'
FROM municipio WHERE nome = 'Riacho de Santana' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Dois Riachos', id, 30.0, -11.82245678, -41.45906226, 'ENEL GREEN POWER DOIS RIACHOS EOLICA S.A.'
FROM municipio WHERE nome = 'Cafarnaum' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Baraúnas I', id, 32.9, -9.88679375, -41.12033083, 'Baraúnas I Energética S.A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Morro Branco I', id, 32.9, -9.84970469, -41.05651947, 'Morro Branco I Energética S.A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Maniçoba', id, 30.0, -11.89343089, -41.45833065, 'ENEL GREEN POWER MANICOBA EOLICA S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Damascena', id, 30.0, -11.85671845, -41.47422172, 'ENEL GREEN POWER DAMASCENA EOLICA S.A.'
FROM municipio WHERE nome = 'Cafarnaum' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá V', id, 20.0, -11.18410667, -42.70531252, 'ASSURUA 1 I ENERGIA S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité B', id, 22.1, -14.11463412, -42.47494888, 'EOLICA CAETITE B S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá II', id, 30.0, -11.1637947, -42.67064896, 'ASSURUA 1 II ENERGIA S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité A', id, 23.8, -14.14148093, -42.47826736, 'EOLICA CAETITE A S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Mussambê', id, 32.9, -9.8627638, -41.0834168, 'Mussambê Energética S.A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Esperança', id, 28.0, -11.86161996, -41.41614405, 'ENEL GREEN POWER ESPERANCA EOLICA S.A.'
FROM municipio WHERE nome = 'Bonito' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá VII', id, 18.0, -11.07103885, -42.6788145, 'ASSURUA 1 III ENERGIA S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Abil', id, 23.7, -14.27782253, -42.54345862, 'CENTRAIS EOLICAS ABIL S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tabua', id, 15.0, -14.3159116, -42.57214019, 'CENTRAIS EOLICAS TABUA S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Angico', id, 8.1, -13.89479471, -42.70269754, 'CENTRAIS EOLICAS ANGICO S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Jabuticaba', id, 9.0, -14.32569297, -42.57185217, 'CENTRAIS EOLICAS JABUTICABA S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Taboquinha', id, 21.6, -13.90772152, -42.70502063, 'CENTRAIS EOLICAS TABOQUINHA S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Folha de Serra', id, 21.0, -14.54996203, -42.58064365, 'CENTRAIS EOLICAS FOLHA DA SERRA S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Jacarandá do Cerrado', id, 21.0, -14.5561812, -42.58204778, 'CENTRAIS EOLICAS JACARANDA DO SERRADO S.A.'
FROM municipio WHERE nome = 'Licínio de Almeida' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Acácia', id, 16.2, -13.91558567, -42.69778192, 'CENTRAIS EOLICAS ACACIA S.A'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Vaqueta', id, 23.4, -14.25956065, -42.5543204, 'CENTRAIS EOLICAS VAQUETA S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Teiú 2', id, 8.0, -14.44520868, -42.69171929, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caititu 2', id, 10.0, -14.33182168, -42.61280189, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Acauã', id, 6.0, -14.41277563, -42.65994184, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Arapapá', id, 4.0, -14.43188347, -42.6918938, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Angical 2', id, 10.0, -14.41903828, -42.6419293, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Carcará', id, 10.0, -14.28355941, -42.59324515, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Corrupião 3', id, 10.0, -14.27017056, -42.57548998, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caititu 3', id, 10.0, -14.32017749, -42.61027503, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Papagaio', id, 10.0, -14.48147697, -42.64087206, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Coqueirinho 2', id, 16.0, -14.50305809, -42.65136408, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Casa Nova II', id, 32.9, -9.27055058, -41.05009439, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Casa Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité C', id, 8.5, -14.10820737, -42.47461065, 'EOLICA CAETITE C S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Casa Nova III', id, 28.2, -9.31480205, -41.11748472, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Casa Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Guarás I', id, 30.0, -10.6060965, -40.65449747, 'VENTOS DOS GUARAS I ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tamanduá Mirim 2', id, 16.0, -14.47095341, -42.66552734, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Banda de Couro', id, 32.9, -9.84775015, -41.05389145, 'BANDA DE COURO ENERGÉTICA S/A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Baraúnas II', id, 25.85, -9.87174698, -41.06310981, 'Baraúnas II Energética S.A'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 5', id, 17.5, -10.66499208, -41.60451759, 'EOLICA UMBURANAS 5 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 1', id, 25.0, -10.67947732, -41.5206307, 'EOLICA UMBURANAS 1 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 2', id, 25.0, -10.66991637, -41.50950699, 'EOLICA UMBURANAS 2 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 3', id, 20.0, -10.64024826, -41.50758317, 'EOLICA UMBURANAS 3 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 10', id, 20.0, -10.64988254, -41.59608593, 'EOLICA UMBURANAS 10 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 9', id, 17.5, -10.63864428, -41.61434102, 'EOLICA UMBURANAS 9 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 11', id, 15.0, -10.58632327, -41.49018325, 'EOLICA UMBURANAS 11 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 13', id, 20.0, -10.64692468, -41.56466585, 'EOLICA UMBURANAS 13 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 15', id, 20.0, -10.65281868, -41.49725368, 'EOLICA UMBURANAS 15 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 16', id, 25.0, -10.69414809, -41.56871623, 'EOLICA UMBURANAS 16 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 18', id, 7.5, -10.65027094, -41.58736582, 'EOLICA UMBURANAS 18 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia II', id, 27.0, -12.01558277, -41.44626231, 'VENTOS DA BAHIA F1 I ENERGIA S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia IV', id, 12.0, -11.9843248, -41.44454854, 'VENTOS DA BAHIA F1 III ENERGIA S.A.'
FROM municipio WHERE nome = 'Bonito' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia VIII', id, 27.0, -12.03242556, -41.45653965, 'VENTOS DA BAHIA F1 II ENERGIA S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 6', id, 20.0, -10.67344736, -41.60474488, 'EOLICA UMBURANAS 6 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 8', id, 25.0, -10.69772228, -41.58232239, 'EOLICA UMBURANAS 8 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Cristalândia I', id, 30.0, -13.93122764, -41.56086632, 'CRISTALANDIA I EOLICA S.A.'
FROM municipio WHERE nome = 'Brumado' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Cristalândia II', id, 30.0, -13.97013345, -41.55017895, 'CRISTALANDIA II EOLICA S.A.'
FROM municipio WHERE nome = 'Brumado' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Cristalândia III', id, 30.0, -14.00950837, -41.53327685, 'CRISTALANDIA II EOLICA S.A.'
FROM municipio WHERE nome = 'Brumado' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Amescla', id, 13.5, -13.83506285, -42.7037769, 'CENTRAIS EOLICAS AMESCLA S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Angelim', id, 21.6, -14.044056, -42.65429301, 'CENTRAIS EOLICAS ANGELIM S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Barbatimão', id, 16.2, -13.82661024, -42.70428516, 'CENTRAIS EOLICAS BARBATIMAO S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Cedro', id, 12.0, -14.32898043, -42.57230176, 'CENTRAIS EOLICAS CEDRO S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Facheio', id, 16.2, -13.99459845, -42.69078304, 'CENTRAIS EOLICAS FACHEIO S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Imburana Macho', id, 8.1, -13.86355786, -42.69089807, 'CENTRAIS EOLICAS IMBURANA MACHO S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Jataí', id, 16.2, -13.82937522, -42.69082273, 'CENTRAIS EOLICAS JATAI S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Juazeiro', id, 18.9, -13.84187496, -42.69056477, 'CENTRAIS EOLICAS JUAZEIRO S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Sabiu', id, 13.5, -14.01545978, -42.66330327, 'CENTRAIS EOLICAS SABIU S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umbuzeiro', id, 18.9, -13.97988782, -42.66518946, 'CENTRAIS EOLICAS UMBUZEIRO S.A.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Vellozia', id, 16.5, -14.25403541, -42.54905022, 'CENTRAIS EOLICAS VELLOZIA S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Manineiro', id, 13.8, -14.01696831, -42.67641007, 'CENTRAIS EOLICAS MANINEIRO S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pau D''Água', id, 18.0, -14.53557321, -42.5758388, 'CENTRAIS EOLICAS PAU D''AGUA S.A.'
FROM municipio WHERE nome = 'Urandi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá III', id, 12.5, -11.17055586, -42.6813213, 'ASSURUA 2 I ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá IV', id, 30.0, -11.04747424, -42.6875994, 'ASSURUA 2 II ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Capoeiras III', id, 27.5, -11.17742485, -42.736905, 'ASSURUA 2 III ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Curral de Pedras I', id, 20.0, -11.13497767, -42.65347343, 'ASSURUA 2 IV ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Curral de Pedras II', id, 27.5, -11.21271233, -42.69985117, 'ASSURUA 2 V ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Diamante II', id, 17.5, -11.10651596, -42.67149802, 'ASSURUA 2 VI ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Diamante III', id, 17.5, -11.0903652, -42.68300814, 'ASSURUA 2 VII ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Laranjeiras I', id, 27.5, -11.12278025, -42.68407001, 'ASSURUA 2 VIII ENERGIA S.A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Laranjeiras II', id, 30.0, -11.13107471, -42.69679751, 'ASSURUA 2 IX ENERGIA S.A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Laranjeiras V', id, 25.0, -11.11529657, -42.66990024, 'ASSURUA 2 X ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina I', id, 28.0, -10.05750878, -40.97868042, 'ENEL GREEN POWER DELFINA A EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina II', id, 28.0, -10.03694564, -40.95861817, 'ENEL GREEN POWER DELFINA A EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina V', id, 28.0, -10.02287638, -40.92836422, 'ENEL GREEN POWER DELFINA A EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Mulungu', id, 13.5, -14.0054666, -42.66704098, 'CENTRAIS EOLICAS ITAPUA VII LTDA.'
FROM municipio WHERE nome = 'Guanambi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pau Santo', id, 18.9, -14.26697816, -42.53643261, 'CENTRAIS EOLICAS UNHA D''ANTA S.A.'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Quina', id, 10.8, -13.9808404, -42.67878101, 'CENTRAIS EOLICAS ITAPUA VII LTDA.'
FROM municipio WHERE nome = 'Igaporã' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Morro Branco II', id, 31.18, -9.76602418, -41.0226698, 'Morro Branco II Energética S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia II', id, 28.2, -11.09546596, -41.30566977, 'EOLICA SERRA DA BABILONIA II S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia IX', id, 28.2, -11.09013968, -41.31536498, 'EOLICA SERRA DA BABILONIA IX S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia VI', id, 25.85, -11.04899523, -41.32520275, 'EOLICA SERRA DA BABILONIA VI S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia VII', id, 28.2, -11.05658661, -41.3154866, 'EOLICA SERRA DA BABILONIA VII S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia VIII', id, 28.2, -11.0838435, -41.30105957, 'EOLICA SERRA DA BABILONIA VIII S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia  X', id, 28.2, -11.06810283, -41.3187875, 'EOLICA SERRA DA BABILONIA X S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia XI', id, 28.2, -11.07697816, -41.32158685, 'EOLICA SERRA DA BABILONIA XI S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia XII', id, 28.2, -11.09886591, -41.31586717, 'EOLICA SERRA DA BABILONIA XII S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Inhambú 2', id, 16.45, -14.45136197, -42.65047171, 'CIA BEAL DE ALIMENTOS'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina III', id, 28.0, -10.00491909, -40.93379381, 'ENEL GREEN POWER DELFINA B EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina IV', id, 8.0, -10.06580256, -40.98092913, 'ENEL GREEN POWER DELFINA C EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina VI', id, 30.0, -10.02923792, -40.95196878, 'ENEL GREEN POWER DELFINA D EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Delfina VII', id, 30.0, -9.98632183, -40.9107399, 'ENEL GREEN POWER DELFINA E EOLICA S.A.'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia I', id, 28.6, -11.98767533, -41.52585741, 'PARQUE EOLICO VENTOS DA BAHIA I S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia III', id, 30.8, -11.97474009, -41.52213627, 'PARQUE EOLICO VENTOS DA BAHIA III S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia IX', id, 28.6, -11.98617146, -41.49142514, 'PARQUE EOLICO VENTOS DA BAHIA IX S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia XIII', id, 49.5, -12.13129677, -41.49666159, 'PARQUE EOLICO VENTOS DA BAHIA XIII S.A.'
FROM municipio WHERE nome = 'Iraquara' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia XIV', id, 33.0, -12.1101801, -41.50677639, 'PARQUE EOLICO VENTOS DA BAHIA XIV S.A.'
FROM municipio WHERE nome = 'Iraquara' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano II', id, 24.8, -11.19013779, -38.78170522, 'TUCANO F2 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Tucano' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano III', id, 43.4, -11.32945525, -38.80453795, 'TUCANO F3 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Tucano' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano IV', id, 43.4, -11.42005176, -38.81289148, 'TUCANO F4 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Biritinga' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano VI', id, 49.6, -11.22795737, -38.79201033, 'TUCANO F6 GERACAO DE ENERGIAS SPE S.A'
FROM municipio WHERE nome = 'Tucano' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano VII', id, 55.8, -11.25646723, -38.81124798, 'TUCANO F7 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Tucano' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano VIII', id, 49.6, -11.28593297, -38.79906981, 'TUCANO F8 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Tucano' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano X', id, 43.4, -11.2984963, -38.80133353, 'TUCANO F1 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Tucano' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 04', id, 29.4, -10.38700257, -40.45885143, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 04 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 15', id, 72.0, -11.07876275, -41.26807907, 'Ventos de Santa Jacinta Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 05', id, 42.0, -10.28982544, -40.43405344, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 05 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Boa Vista da Lagoinha', id, 30.0, -11.76135316, -41.38928707, 'ENEL GREEN POWER BOA VISTA EOLICA S A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Caetité 03', id, 36.0, -13.70691891, -42.6001183, 'TANQUE NOVO VI ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Caetité 04', id, 22.5, -13.6252034, -42.55086718, 'TANQUE NOVO VII ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santo Abraão', id, 28.0, -11.62494903, -41.35213098, 'ENEL GREEN POWER SAO ABRAAO EOLICA S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 01', id, 33.6, -10.32267124, -40.43672213, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 01 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 03', id, 33.6, -10.31053008, -40.44036494, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 03 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 06', id, 37.8, -10.41098237, -40.46644302, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 06 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 10', id, 42.0, -10.26507524, -40.43061877, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 10 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 11', id, 42.0, -10.24309451, -40.42447693, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 11 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 13', id, 42.0, -10.34475554, -40.4377539, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 13 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 14', id, 42.0, -10.36782797, -40.44223337, 'PARQUE EOLICO VENTOS DE SAO JANUARIO 14 S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 16', id, 72.0, -11.09936157, -41.2808835, 'Ventos de São Joaquim Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 17', id, 72.0, -11.13073509, -41.2873319, 'Ventos de São Júlio I Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 18', id, 72.0, -11.12647376, -41.30334018, 'Ventos de Santa Justina Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Laranjeiras III', id, 25.0, -11.03539473, -42.6321113, 'ASSURUA 3 I ENERGIA S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Laranjeiras IX', id, 25.0, -11.08122843, -42.61800203, 'ASSURUA 3 II ENERGIA S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo III', id, 29.7, -10.50290276, -41.54008674, 'CLWP EOLICA PARQUE III S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo IV', id, 29.7, -10.4897835, -41.51154837, 'CLWP EOLICA PARQUE IV S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo V', id, 29.7, -10.42297013, -41.42850734, 'CLWP EOLICA PARQUE V S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo VI', id, 29.7, -10.42114796, -41.39572975, 'CLWP EOLICA PARQUE VI S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo VII', id, 29.7, -10.52294314, -41.49361778, 'CLWP EOLICA PARQUE VII S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XXI', id, 29.7, -10.44955113, -41.44806822, 'CLWP EOLICA PARQUE XXI S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 17', id, 22.5, -10.67234882, -41.57045134, 'EOLICA UMBURANAS 17 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 19', id, 25.0, -10.59761022, -41.49465855, 'EOLICA UMBURANAS 19 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 21', id, 22.5, -10.70100472, -41.59507662, 'EOLICA UMBURANAS 21 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 23', id, 15.0, -10.62452756, -41.60279471, 'EOLICA UMBURANAS 23 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburanas 25', id, 17.5, -10.64676367, -41.53336924, 'EOLICA UMBURANAS 25 S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Santa Dulce', id, 28.0, -11.59853886, -41.33942579, 'ENEL GREEN POWER MORRO DO CHAPEU II EOLICA S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Santa Esperança', id, 28.0, -11.60750442, -41.31574191, 'ENEL GREEN POWER MORRO DO CHAPEU II EOLICA S.A'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos do São Mário', id, 30.0, -11.58741408, -41.22852865, 'ENEL GREEN POWER MORRO DO CHAPEU I EOLICA S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos do São Paulo', id, 28.0, -11.57867591, -41.30342444, 'ENEL GREEN POWER MORRO DO CHAPEU I EOLICA S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 15', id, 46.2, -11.5583383, -41.30172757, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANCA 15 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 16', id, 42.0, -11.53059227, -41.32024105, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 16 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 17', id, 42.0, -11.50797772, -41.29458236, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 17 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 21', id, 46.2, -11.49919413, -41.29109346, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 21 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 22', id, 46.2, -11.51693908, -41.28006687, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 22 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 25', id, 25.2, -11.55902476, -41.33004267, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 25 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 26', id, 46.2, -11.52612662, -41.33171371, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 26 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo I', id, 29.7, -10.46544666, -41.49346741, 'CLWP EÓLICA PARQUE I S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo VIII', id, 33.6, -10.4599988, -41.3931605, 'CLWP EÓLICA PARQUE VIII S.A.'
FROM municipio WHERE nome = 'Umburanas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XIII', id, 42.0, -10.40246351, -41.4935042, 'CLWP EÓLICA PARQUE XIII S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XII', id, 54.6, -10.4054121, -41.44864516, 'CLWP EÓLICA PARQUE XII S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XI', id, 16.8, -10.40273666, -41.44636052, 'CLWP EÓLICA PARQUE XI S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo X', id, 33.6, -10.5066865, -41.48676691, 'CLWP EÓLICA PARQUE X S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo largo IX', id, 25.2, -10.46258579, -41.39403857, 'CLWP EÓLICA PARQUE IX S.A.'
FROM municipio WHERE nome = 'Umburanas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo II', id, 29.7, -10.51103117, -41.45451405, 'CLWP EÓLICA PARQUE II S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XIV', id, 21.0, -10.44678334, -41.50083161, 'CLWP EÓLICA PARQUE XIV S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XV', id, 29.7, -10.48909122, -41.43445434, 'CLWP EÓLICA PARQUE XV S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XVI', id, 29.7, -10.44079793, -41.46759994, 'CLWP EÓLICA PARQUE XVI S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XVII', id, 29.4, -10.46295351, -41.51405275, 'CLWP EÓLICA PARQUE XVII S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XVIII', id, 29.7, -10.42029019, -41.47863891, 'CLWP EÓLICA PARQUE XVIII S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XIX', id, 33.6, -10.44090202, -41.57230736, 'CLWP EÓLICA PARQUE XIX S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XX', id, 37.8, -10.48045063, -41.57687853, 'CLWP EÓLICA PARQUE XX S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Campo Largo XXII', id, 33.6, -10.50942356, -41.61168567, 'CLWP EÓLICA PARQUE XXII S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 3', id, 32.0, -10.92544883, -42.55331652, 'VENTOS DE SAO VITOR 03 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 4', id, 32.0, -10.94114345, -42.56103358, 'VENTOS DE SAO VITOR 04 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 10', id, 44.8, -10.94805524, -42.5294563, 'VENTOS DE SAO VITOR 10 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Itaguaçu da Bahia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 11', id, 32.0, -10.93004437, -42.53707598, 'VENTOS DE SAO VITOR 11 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Itaguaçu da Bahia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 12', id, 32.0, -10.99014879, -42.51652227, 'VENTOS DE SAO VITOR 12 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Itaguaçu da Bahia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 14', id, 38.4, -10.99169933, -42.50064184, 'IRMAOS BOA LTDA'
FROM municipio WHERE nome = 'Itaguaçu da Bahia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 08', id, 25.2, -11.58693056, -41.33895335, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANCA 8 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Esperança 13', id, 33.6, -11.58342408, -41.35679636, 'ENEL GREEN POWER VENTOS DE SANTA ESPERANCA 13 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Emilia', id, 27.3, -11.00636025, -41.22712795, 'CENTRAL EOLICA BABILONIA V S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Aparecida', id, 27.3, -10.91301565, -41.22142744, 'CENTRAL EOLICA BABILONIA I S.A.'
FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Santa Beatriz', id, 27.3, -10.93969473, -41.22889662, 'CENTRAL EOLICA BABILONIA II S.A.'
FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos do São Gabriel', id, 27.3, -10.95819775, -41.23343433, 'CENTRAL EOLICA BABILONIA III S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Aurora', id, 27.3, -10.97611855, -41.21994954, 'CENTRAL EOLICA BABILONIA IV S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra do Reino IV', id, 20.0, -5.066152, -36.012362, 'EOLICA PEDRA DO REINO IV S.A.'
FROM municipio WHERE nome = 'Sobradinho' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra das Almas I', id, 40.5, -14.77098218, -42.5672222, 'PARQUE EOLICO SERRA DAS ALMAS I S.A.'
FROM municipio WHERE nome = 'Licínio de Almeida' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra das Almas II', id, 45.0, -14.79798325, -42.57213987, 'PARQUE EOLICO SERRA DAS ALMAS II S.A.'
FROM municipio WHERE nome = 'Licínio de Almeida' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra das Almas III', id, 40.5, -14.81988175, -42.5626836, 'PARQUE EOLICO SERRA DAS ALMAS III S.A.'
FROM municipio WHERE nome = 'Jacaraci' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra das Almas VI', id, 45.0, -14.83093014, -42.58490473, 'PARQUE EOLICO SERRA DAS ALMAS VI S.A.'
FROM municipio WHERE nome = 'Urandi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 1', id, 32.0, -10.89804156, -42.5386471, 'VENTOS DE SAO VITOR 01 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 2', id, 32.0, -10.91153919, -42.54620723, 'VENTOS DE SAO VITOR 02 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 5', id, 32.0, -10.9562976, -42.56218063, 'VENTOS DE SAO VITOR 05 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 6', id, 32.0, -10.97171047, -42.56177997, 'VENTOS DE SAO VITOR 06 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 7', id, 44.8, -10.98737242, -42.56089232, 'VENTOS DE SAO VITOR 07 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 8', id, 32.0, -10.98612573, -42.53962076, 'VENTOS DE SAO VITOR 08 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 9', id, 32.0, -10.9711941, -42.5369186, 'VENTOS DE SAO VITOR 09 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Vitor 13', id, 32.0, -10.9761812, -42.51015523, 'VENTOS DE SAO VITOR 13 ENERGIAS RENOVAVEIS S/A'
FROM municipio WHERE nome = 'Itaguaçu da Bahia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia XVIII', id, 28.6, -12.01548693, -41.49544765, 'PARQUE EOLICO VENTOS DA BAHIA XVIII S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia XXVII', id, 49.5, -12.06263011, -41.48626537, 'PARQUE EOLICO VENTOS DA BAHIA XXVII S.A.'
FROM municipio WHERE nome = 'Mulungu do Morro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Tanque Novo 03', id, 13.5, -13.64982856, -42.57715616, 'TANQUE NOVO III ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Fogo', id, 31.18, -9.805084, -41.022709, 'SERRA DO FOGO ENERGÉTICA S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Vento', id, 31.18, -9.81603, -41.038889, 'SERRA DO VENTO ENERGÉTICA S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Umburana de Cheiro', id, 31.18, -9.835541, -41.042697, 'UMBURANA DE CHEIRO ENERGÉTICA S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos da Bahia XXIII', id, 49.5, -12.08423134, -41.49362596, 'PARQUE EOLICO VENTOS DA BAHIA XXIII S.A.'
FROM municipio WHERE nome = 'Souto Soares' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'ZEUS II', id, 29.4, -9.96984534, -40.88155455, 'ENEL GREEN POWER ZEUS II'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Tucano XVI', id, 12.4, -11.35996856, -38.80407728, 'TUCANO F2 GERACAO DE ENERGIAS SPE S.A.'
FROM municipio WHERE nome = 'Araci' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Baraúnas XX', id, 31.18, -9.78915068, -41.01289439, 'Baraúnas XX Energética S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ararinha Azul', id, 21.15, -14.41267553, -42.64976084, 'CIA BEAL DE ALIMENTOS'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité D', id, 50.4, -14.1145591, -42.49976533, 'EOLICA CAETITE D S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité E', id, 37.8, -14.07435991, -42.51232761, 'EOLICA CAETITE ECO S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Caetité F', id, 25.2, -14.07535267, -42.52628322, 'EOLICA CAETITE F S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Corrupião II', id, 18.8, -14.41536206, -42.6411244, 'CIA BEAL DE ALIMENTOS'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra do Reino V', id, 16.0, -9.54944968, -40.89579893, 'EOLICA PEDRA DO REINO V S.A.'
FROM municipio WHERE nome = 'Sobradinho' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia A', id, 21.2, -11.1203576, -41.32421958, 'EOLICA SDB ALFA S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra das Almas IV', id, 45.0, -14.79229001, -42.60132273, 'PARQUE EOLICO SERRA DAS ALMAS IV S.A.'
FROM municipio WHERE nome = 'Licínio de Almeida' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra das Almas V', id, 45.0, -14.81507581, -42.59621515, 'PARQUE EOLICO SERRA DAS ALMAS V S.A.'
FROM municipio WHERE nome = 'Urandi' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Teiú 3', id, 23.5, -14.40879642, -42.64864637, 'CIA BEAL DE ALIMENTOS'
FROM municipio WHERE nome = 'Pindaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 19', id, 72.0, -11.13989048, -41.30848805, 'Ventos de São João XXIII Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 20', id, 50.4, -10.44202226, -40.46165762, 'VENTOS DE SÃO BENTO ENERGIAS RENOVÁVEIS S.A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 21', id, 50.4, -10.42861608, -40.50689139, 'VENTOS DE SÃO GALVÃO ENERGIAS RENOVÁVEIS S/A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de São Januário 22', id, 50.4, -10.47361786, -40.52396426, 'VENTOS DE SANTO ELOY ENERGIAS RENOVÁVEIS S/A'
FROM municipio WHERE nome = 'Campo Formoso' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Casa Nova A', id, 27.0, -9.26020855, -41.01681339, 'AXIA ENERGIA NORDESTE S.A.'
FROM municipio WHERE nome = 'Casa Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Fazenda Belmonte', id, 0.0, -10.37972222, -41.61861111, 'CANINANA ENERGY LTDA'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Fazenda Melancia', id, 0.0, -10.31774325, -41.52614208, 'CANINANA ENERGY LTDA'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Fazenda Floresta', id, 0.0, -10.37990399, -41.61867627, 'CANINANA ENERGY LTDA'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Baraúnas XV (Antiga Massaroca I)', id, 48.51, -9.75010326, -40.97532638, 'Baraúnas XV Energética S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Baraúnas IV (Antiga Massaroca II)', id, 41.58, -9.67418218, -41.11013771, 'BARAUNAS IV ENERGETICA S.A.'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 01', id, 51.3, -11.38881453, -42.14715142, 'OSLO I S/A'
FROM municipio WHERE nome = 'Uibaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 02', id, 11.4, -11.37453368, -42.17721284, 'OSLO II S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 03', id, 39.9, -11.40948463, -42.15943416, 'OSLO III S/A.'
FROM municipio WHERE nome = 'Uibaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 05', id, 34.2, -11.45400602, -42.16921949, 'OSLO IV S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 06', id, 34.2, -11.48827849, -42.13403787, 'OSLO VI S/A.'
FROM municipio WHERE nome = 'Uibaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 07', id, 17.1, -11.38125707, -42.17558097, 'OSLO II S/A.'
FROM municipio WHERE nome = 'Uibaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 08', id, 28.5, -11.46907711, -42.16582734, 'OSLO IV S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugênia 09', id, 39.9, -11.54041079, -42.11596769, 'OSLO V S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 10', id, 45.6, -11.50062055, -42.14534355, 'OSLO IX S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 11', id, 28.5, -11.44346082, -42.15958093, 'OSLO VIII S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 12', id, 39.9, -11.42750927, -42.157872, 'OSLO III S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugênia 13', id, 45.6, -11.52087909, -42.14410409, 'OSLO X S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Eugenia 14', id, 22.8, -11.52678665, -42.11847006, 'OSLO V S/A.'
FROM municipio WHERE nome = 'Ibipeba' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Fazenda Alegre', id, 0.0, -10.33009536, -41.43470186, 'CANINANA ENERGY LTDA'
FROM municipio WHERE nome = 'Sento Sé' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia B', id, 31.8, -11.12657611, -41.32641931, 'EOLICA SDB B S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia C', id, 26.5, -11.13278547, -41.32862823, 'EOLICA SDB C S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia D', id, 31.8, -11.14265824, -41.33155279, 'EOLICA SDB D S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia E', id, 26.5, -11.14937021, -41.33303391, 'EOLICA SDB ECO S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Babilônia F', id, 21.2, -11.15938659, -41.33606986, 'EOLICA SDB F S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Brejinhos A', id, 37.8, -14.17986019, -42.53302446, 'EOLICA BREJINHOS ALFA S.A'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Brejinhos B', id, 42.0, -14.20685148, -42.53267662, 'EOLICA BREJINHOS B S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Oitis 21', id, 44.0, -9.03160148, -41.70058954, 'OITIS 21 ENERGIA RENOVÁVEL S.A.'
FROM municipio WHERE nome = 'Casa Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Oitis 22', id, 49.5, -9.07383623, -41.71480375, 'OITIS 22 ENERGIA RENOVÁVEL S.A.'
FROM municipio WHERE nome = 'Casa Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra da Mangabeira', id, 79.8, -11.43252238, -42.13742953, 'SERRA DA MANGABEIRA S/A.'
FROM municipio WHERE nome = 'Uibaí' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 1', id, 47.3, -10.7860708, -41.44222225, 'Enel Green Power Aroeira 01 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 2', id, 43.0, -10.80514852, -41.43627081, 'Enel Green Power Aroeira 02 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 3', id, 43.0, -10.82725136, -41.45113853, 'Enel Green Power Aroeira 03 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 4', id, 43.0, -10.81859762, -41.48688916, 'Enel Green Power Aroeira 04 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 5', id, 43.0, -10.78519203, -41.49349595, 'Enel Green Power Aroeira 05 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 6', id, 38.7, -10.766796, -41.53010862, 'Enel Green Power Aroeira 06 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 7', id, 47.3, -10.75943554, -41.49842884, 'Enel Green Power Aroeira 07 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aroeira 8', id, 43.0, -10.75481294, -41.47878916, 'Enel Green Power Aroeira 08 S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Caetité 01', id, 27.0, -13.6583284, -42.57601608, 'TANQUE NOVO IV ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Caetité 02', id, 36.0, -13.68371013, -42.59385791, 'TANQUE NOVO V ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Tanque Novo 01', id, 27.0, -13.64056133, -42.54798357, 'TANQUE NOVO I ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Tanque Novo' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Aura Tanque Novo 02', id, 18.0, -13.67715706, -42.57321782, 'TANQUE NOVO II ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Caetité' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Canudos I', id, 49.7, -10.18922908, -38.77506982, 'EOLICA CANUDOS III SPE S.A.'
FROM municipio WHERE nome = 'Canudos' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Canudos II', id, 49.7, -10.14664538, -38.77132041, 'EOLICA CANUDOS II SPE S.A.'
FROM municipio WHERE nome = 'Canudos' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santo Antonio 04', id, 67.5, -11.06293107, -41.27985095, 'Ventos de Santa Felicidade Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra Pintada I', id, 49.5, -10.77992924, -41.21338827, 'USINA EÓLICA PEDRA PINTADA A LTDA.'
FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra Pintada II', id, 49.5, -10.7417214, -41.20693885, 'USINA EÓLICA PEDRA PINTADA B LTDA.'
FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra Pintada III', id, 45.0, -10.70554318, -41.20747111, 'USINA EOLICA PEDRA PINTADA C LTDA.'
FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Pedra Pintada IV', id, 49.5, -10.66862703, -41.1850732, 'USINA EOLICA PEDRA PINTADA D LTDA.'
FROM municipio WHERE nome = 'Ourolândia' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 1', id, 45.0, -11.37032999, -42.60718563, 'EÓLICA SERRA DO ASSURUÁ 1 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 2', id, 22.5, -11.40945403, -42.62116527, 'EÓLICA SERRA DO ASSURUÁ 2 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 3', id, 40.5, -11.41682779, -42.60672329, 'EÓLICA SERRA DO ASSURUÁ 3 LTDA'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 4', id, 36.0, -11.41859485, -42.59187916, 'EÓLICA SERRA DO ASSURUÁ 4 LTDA'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 5', id, 36.0, -11.49936917, -42.58843712, 'EÓLICA SERRA DO ASSURUÁ 5 LTDA'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 6', id, 36.0, -11.52760458, -42.57613797, 'EÓLICA SERRA DO ASSURUÁ 6 LTDA'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 7', id, 27.0, -11.54919379, -42.59480231, 'EÓLICA SERRA DO ASSURUÁ 7 LTDA'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 8', id, 22.5, -11.53657115, -42.5549033, 'EÓLICA SERRA DO ASSURUÁ 8 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 9', id, 40.5, -11.5070182, -42.59879875, 'EÓLICA SERRA DO ASSURUÁ 9 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 10', id, 40.5, -11.51235777, -42.57906321, 'EÓLICA SERRA DO ASSURUÁ 10 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 11', id, 40.5, -11.52265266, -42.54811515, 'EÓLICA SERRA DO ASSURUÁ 11 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 12', id, 27.0, -11.56801525, -42.58706275, 'EÓLICA SERRA DO ASSURUÁ 12 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 13', id, 36.0, -11.58731884, -42.58840939, 'EÓLICA SERRA DO ASSURUÁ 13 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 14', id, 27.0, -11.59500697, -42.58928742, 'EÓLICA SERRA DO ASSURUÁ 14 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 15', id, 36.0, -11.47298307, -42.61702492, 'EÓLICA SERRA DO ASSURUÁ 15 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 16', id, 40.5, -11.46293235, -42.57286184, 'EÓLICA SERRA DO ASSURUÁ 16 LTDA.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 17', id, 40.5, -11.46593104, -42.5586094, 'A. ANGELONI &amp'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 18', id, 40.5, -11.46663628, -42.53606785, 'Eólica Serra do Assuruá 18 Ltda.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 19', id, 40.5, -11.4859242, -42.56789212, 'Eólica Serra do Assuruá 19 Ltda.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 20', id, 40.5, -11.52906287, -42.62359045, 'Eólica Serra do Assuruá 20 Ltda'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 21', id, 40.5, -11.49587231, -42.54625827, 'Eólica Serra do Assuruá 21 Ltda.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 22', id, 27.0, -11.56613233, -42.56076855, 'Eólica Serra do Assuruá 22 Ltda.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 23', id, 36.0, -11.53970024, -42.52747853, 'Eólica Serra do Assuruá 23 Ltda'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Serra do Assuruá 24', id, 27.0, -11.60390806, -42.55601392, 'Eólica Serra do Assuruá 24 Ltda'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 01', id, 45.0, -12.6191959, -42.05772247, 'VENTOS DE SAO ROMUALDO ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Ibitiara' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 02', id, 45.0, -12.6396309, -42.0540304, 'VENTOS DE SAO TEOFANO ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Ibitiara' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 03', id, 45.0, -12.66016558, -42.05282891, 'VENTOS DE SAO TEONAS ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Ibitiara' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 04', id, 40.5, -12.68160891, -42.04968463, 'VENTOS DE SAO THOMAS ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Ibitiara' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 05', id, 40.5, -12.70199688, -42.04668041, 'VENTOS DE SAO TILAO ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Novo Horizonte' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 06', id, 40.5, -12.72241588, -42.04323336, 'VENTOS DE SANTO UBALDO ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Boninal' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 07', id, 40.5, -12.74632859, -42.03321201, 'VENTOS DE SANTO URBANO I ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Boninal' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 08', id, 45.0, -12.77090365, -42.03074457, 'VENTOS DE SAO VIGILIO ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Novo Horizonte' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 09', id, 40.5, -12.8476983, -42.0674189, 'VENTOS DE SANTA VIRGINIA ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Novo Horizonte' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 10', id, 40.5, -12.86804119, -42.06443001, 'VENTOS DE SAO VLADIMIR ENERGIAS RENOVAVEIS S.A.'
FROM municipio WHERE nome = 'Novo Horizonte' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 4 I', id, 36.0, -11.02539165, -42.64105068, 'ASSURUA 4 I ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 4 II', id, 31.5, -11.03596114, -42.61452038, 'ASSURUA 4 II ENERGIA S.A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 4 III', id, 36.0, -11.06036901, -42.63062401, 'ASSURUA 4 III ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 4 IV', id, 36.0, -11.08393419, -42.62663031, 'ASSURUA 4 IV ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 4 V', id, 36.0, -11.10734948, -42.62924124, 'ASSURUA 4 V ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 4 VI', id, 36.0, -11.06616944, -42.60300235, 'ASSURUA 4 VI ENERGIA S.A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Morro 1', id, 45.6, -12.25090352, -42.37421592, 'MORRO DO CRUZEIRO I S.A.'
FROM municipio WHERE nome = 'Brotas de Macaúbas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Morro 2', id, 34.2, -12.22453209, -42.37804034, 'MORRO DO CRUZEIRO II S.A.'
FROM municipio WHERE nome = 'Brotas de Macaúbas' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 14', id, 72.0, -11.05163371, -41.2631038, 'Ventos de São Julião Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 15', id, 72.0, -11.0337387, -41.23768996, 'Ventos de São Raimundo Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santa Luzia 16', id, 72.0, -11.00649616, -41.25645584, 'Ventos de São Roberto Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Várzea Nova' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santo Antônio 05', id, 67.5, -11.04464558, -41.28982781, 'Ventos de Santa Ana Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santo Antônio 06', id, 67.5, -11.01949045, -41.29400553, 'Ventos de Santa Sabina Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santo Antônio 07', id, 72.0, -11.02428258, -41.29846104, 'Ventos de Santa Irene Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Ventos de Santo Antônio 08', id, 63.0, -11.04836694, -41.30555544, 'Ventos de Santa Rufina Energias Renováveis S.A.'
FROM municipio WHERE nome = 'Morro do Chapéu' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 5 I', id, 40.6, -10.98219659, -42.61010211, 'CARGILL AGRICOLA S A'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 5 II', id, 46.4, -10.99740745, -42.62420222, 'DIGITAL DC BRASIL LTDA.'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 5 III', id, 40.6, -11.01855191, -42.67649199, 'CARGILL AGRICOLA S A'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 5 IV', id, 46.4, -11.02188033, -42.60370852, 'MANAUS AMBIENTAL S.A.'
FROM municipio WHERE nome = 'Xique' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 5 V', id, 34.8, -11.14133575, -42.63391168, 'MANAUS AMBIENTAL S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)
SELECT 'Assuruá 5 VI', id, 34.8, -11.02376103, -42.58903056, 'MANAUS AMBIENTAL S.A.'
FROM municipio WHERE nome = 'Gentio do Ouro' AND estado = 'BA' LIMIT 1;

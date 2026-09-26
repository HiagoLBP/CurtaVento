-- Migration V11: Inserir municipios e usinas

-- 1. Inserir municipios faltantes
INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Araci', '2902005'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Araci');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Biritinga', '2903904'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Biritinga');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Boninal', '2904050'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Boninal');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Bonito', '2904308'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Bonito');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Brotas de Macaúbas', '2904506'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Brotas de Macaúbas');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Brumado', '2904605'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Brumado');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Caetité', '2905206'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Caetité');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Cafarnaum', '2905305'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Cafarnaum');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Campo Formoso', '2906006'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Campo Formoso');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Canudos', '2906824'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Canudos');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Casa Nova', '2907202'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Casa Nova');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Gentio do Ouro', '2911303'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Gentio do Ouro');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Guanambi', '2911709'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Guanambi');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Ibipeba', '2912400'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Ibipeba');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Ibitiara', '2912806'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Ibitiara');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Igaporã', '2913200'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Igaporã');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Iraquara', '2914000'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Iraquara');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Itaguaçu da Bahia', '2915353'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Itaguaçu da Bahia');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Jacaraci', '2917300'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Jacaraci');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Licínio de Almeida', '2919405'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Licínio de Almeida');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Morro do Chapéu', '2921708'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Morro do Chapéu');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Mulungu do Morro', '2922250'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Mulungu do Morro');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Novo Horizonte', '2923035'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Novo Horizonte');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Ourolândia', '2923357'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Ourolândia');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Pindaí', '2924306'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Pindaí');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Riacho de Santana', '2926400'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Riacho de Santana');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Sento Sé', '2930204'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Sento Sé');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Sobradinho', '2930774'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Sobradinho');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Souto Soares', '2930808'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Souto Soares');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Tanque Novo', '2931053'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Tanque Novo');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Tucano', '2931905'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Tucano');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Uibaí', '2932507'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Uibaí');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Umburanas', '2932457'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Umburanas');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Urandi', '2932606'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Urandi');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Várzea Nova', '2933158'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Várzea Nova');

INSERT INTO municipio (nome, codigo_ibge)
SELECT 'Xique', '2933604'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = 'Xique');

-- 2. Inserir usinas eolicas
INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Macaúbas', 'MACAUBAS ENERGETICA SA', id, 35.07, -12.35119149, -42.33852459
FROM municipio WHERE nome = 'Brotas de Macaúbas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra do Reino', 'EOLICA PEDRA DO REINO S.A.', id, 30.0, -9.51754829, -40.88639419
FROM municipio WHERE nome = 'Sobradinho' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Novo Horizonte', 'NOVO HORIZONTE ENERGETICA S/A', id, 30.06, -12.31269913, -42.35098757
FROM municipio WHERE nome = 'Brotas de Macaúbas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Seabra', 'SEABRA ENERGETICA S/A', id, 35.39, -12.27870333, -42.36260411
FROM municipio WHERE nome = 'Brotas de Macaúbas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Salto', 'CENTRAIS EOLICAS SERRA DO SALTO LTDA.', id, 19.2, -14.15545583, -42.63821972
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ilhéus', 'CENTRAIS EOLICAS ILHEUS LTDA.', id, 11.2, -13.8823, -42.66305
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Candiba', 'CENTRAIS EOLICAS CANDIBA LTDA.', id, 9.6, -14.18314712, -42.62274949
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Licínio de Almeida', 'CENTRAIS EOLICAS LICINIO DE ALMEIDA LTDA.', id, 24.0, -14.17694395, -42.6540627
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Nossa Senhora da Conceição', 'CENTRAIS EOLICAS NOSSA SENHORA DA CONCEICAO LTDA.', id, 28.8, -13.89569444, -42.67688889
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pajeú do Vento', 'CENTRAIS EOLICAS PAJEU DO VENTO LTDA.', id, 25.6, -14.01475, -42.63386111
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Alvorada', 'CENTRAIS EOLICAS ALVORADA LTDA.', id, 8.0, -14.18515114, -42.5910726
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Igaporã', 'CENTRAIS EOLICAS IGAPORA LTDA.', id, 30.4, -13.86057434, -42.6573417
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Planaltina', 'CENTRAIS EOLICAS PLANALTINA LTDA.', id, 27.2, -13.98794444, -42.63894444
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Porto Seguro', 'CENTRAIS EOLICAS PORTO SEGURO LTDA.', id, 6.4, -13.86294444, -42.67266667
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pindaí', 'CENTRAIS EOLICAS PINDAI LTDA.', id, 24.0, -14.20213939, -42.65889865
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Guanambi', 'CENTRAIS EOLICAS GUANAMBI LTDA.', id, 20.8, -14.20170756, -42.61830984
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Rio Verde', 'CENTRAIS EOLICAS RIO VERDE LTDA.', id, 30.4, -14.16402778, -42.60055556
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Guirapá', 'CENTRAIS EOLICAS GUIRAPA LTDA.', id, 28.8, -14.21891461, -42.6413885
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité 2', 'CAETITE 2 ENERGIA RENOVAVEL S/A', id, 30.0, -14.185, -42.503
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra Branca', 'Pedra Branca S.A.', id, 30.0, -9.87459153, -41.09571479
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité 3', 'CAETITE 3 ENERGIA RENOVAVEL S/A.', id, 30.0, -14.21944444, -42.507
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Sete Gameleiras', 'SETE GAMELEIRAS S/A', id, 30.0, -9.86384384, -41.10417464
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'São Pedro do Lago', 'São Pedro do Lago S.A', id, 31.46, -9.87469326, -41.09301108
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos do Nordeste', 'CENTRAIS EOLICAS VENTOS DO NORDESTE S/A', id, 23.52, -14.11946418, -42.61408311
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Da Prata', 'CENTRAIS EOLICAS DA PRATA S.A', id, 21.84, -13.96426616, -42.63886294
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Dos Araçás', 'Centrais Eólicas dos Araçás S.A', id, 31.86, -14.12959332, -42.55965544
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Morrão', 'CENTRAIS EOLICAS MORRAO S/A', id, 30.24, -14.08850925, -42.68270797
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tanque', 'CENTRAIS EOLICAS TANQUE S.A', id, 30.0, -14.1025978, -42.61065719
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Seraíma', 'CENTRAIS EOLICAS SERAIMA S.A', id, 30.24, -14.14727297, -42.71185123
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra do Reino III', 'EOLICATEC SOBRADINHO S.A.', id, 18.0, -9.49858333, -40.879
FROM municipio WHERE nome = 'Sobradinho' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'São Judas', 'ENEL GREEN POWER SAO JUDAS EOLICA S/A', id, 29.9, -11.66425, -41.362
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Primavera', 'ENEL GREEN POWER PRIMAVERA EOLICA S/A', id, 29.9, -11.73336111, -41.373
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Cristal', 'ENEL GREEN POWER CRISTAL EOLICA S/A', id, 29.9, -11.79646316, -41.3933842
FROM municipio WHERE nome = 'Cafarnaum' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Teiu', 'CENTRAL EOLICA TEIU S/A', id, 16.65, -14.3381384, -42.59109055
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Angical', 'CENTRAL EOLICA ANGICAL S/A', id, 12.95, -14.34605667, -42.61368354
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Coqueirinho', 'CENTRAL EOLICA COQUEIRINHO S/A', id, 29.6, -14.41988897, -42.62872019
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caititu', 'CENTRAL EOLICA CAITITU S/A', id, 22.2, -14.45280192, -42.64099568
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Inhambu', 'CENTRAL EOLICA INHAMBU S/A', id, 31.45, -14.39117106, -42.5938536
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Corrupião', 'CENTRAL EOLICA CORRUPIAO S/A', id, 27.75, -14.29269078, -42.58526944
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tamanduá Mirim', 'CENTRAL EOLICA TAMANDUA MIRIM S/A', id, 29.6, -14.377552, -42.60938646
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Maron', 'CENTRAIS EOLICAS MARON S.A', id, 30.24, -14.13370646, -42.60235759
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pilões', 'CENTRAIS EOLICAS PILOES S.A', id, 30.24, -14.02915106, -42.61607574
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Dourados', 'CENTRAIS EOLICAS DOURADOS S.A', id, 28.56, -14.15232508, -42.68159246
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ametista', 'CENTRAIS EOLICAS AMETISTA S.A', id, 28.56, -14.17203284, -42.70022351
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité', 'CENTRAIS EOLICAS CAETITE S.A', id, 30.24, -14.46886215, -42.58355855
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pelourinho', 'CENTRAIS EOLICAS PELOURINHO S.A', id, 21.84, -14.52923503, -42.58673115
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Joana', 'ENEL GREEN POWER JOANA EOLICA S.A.', id, 28.2, -13.91798451, -42.62044581
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Espinhaço', 'CENTRAIS EOLICAS SERRA DO ESPINHACO S.A', id, 18.48, -14.49572374, -42.58285958
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Espigão', 'CENTRAIS EOLICAS ESPIGAO S.A', id, 10.08, -14.47696269, -42.5852515
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Emiliana', 'ENEL GREEN POWER EMILIANA EOLICA S.A.', id, 28.2, -13.91002361, -42.62417205
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Borgo', 'CENTRAIS EOLICAS BORGO S.A', id, 20.16, -14.50840054, -42.58665108
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Campo Formoso I', 'CAMPO FORMOSO I ENERGIAS RENOVAVEIS S.A.', id, 30.0, -10.62770186, -40.68553526
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Andorinha', 'ANDORINHA ENERGIAS RENOVAVEIS S.A.', id, 30.0, -10.583277, -40.63744738
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Morrinhos', 'MORRINHOS ENERGIAS RENOVAVEIS S.A.', id, 30.0, -10.56956163, -40.62858262
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos do Sertão', 'SERTAO ENERGIAS RENOVAVEIS S.A.', id, 30.0, -10.55667941, -40.5686764
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité 1', 'CAETITE 1 ENERGIA RENOVAVEL S/A', id, 30.0, -14.16321686, -42.4844902
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Campo Formoso II', 'CAMPO FORMOSO II ENERGIAS RENOVAVEIS S. A.', id, 30.0, -10.51956005, -40.61138421
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'São Salvador', 'CENTRAIS EOLICAS SAO SALVADOR S.A', id, 18.9, -13.83791923, -42.64863784
FROM municipio WHERE nome = 'Riacho de Santana' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Dois Riachos', 'ENEL GREEN POWER DOIS RIACHOS EOLICA S.A.', id, 30.0, -11.82245678, -41.45906226
FROM municipio WHERE nome = 'Cafarnaum' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Baraúnas I', 'Baraúnas I Energética S.A', id, 32.9, -9.88679375, -41.12033083
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Morro Branco I', 'Morro Branco I Energética S.A', id, 32.9, -9.84970469, -41.05651947
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Maniçoba', 'ENEL GREEN POWER MANICOBA EOLICA S.A.', id, 30.0, -11.89343089, -41.45833065
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Damascena', 'ENEL GREEN POWER DAMASCENA EOLICA S.A.', id, 30.0, -11.85671845, -41.47422172
FROM municipio WHERE nome = 'Cafarnaum' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá V', 'ASSURUA 1 I ENERGIA S.A.', id, 20.0, -11.18410667, -42.70531252
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité B', 'EOLICA CAETITE B S.A.', id, 22.1, -14.11463412, -42.47494888
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá II', 'ASSURUA 1 II ENERGIA S.A.', id, 30.0, -11.1637947, -42.67064896
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité A', 'EOLICA CAETITE A S.A.', id, 23.8, -14.14148093, -42.47826736
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Mussambê', 'Mussambê Energética S.A', id, 32.9, -9.8627638, -41.0834168
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Esperança', 'ENEL GREEN POWER ESPERANCA EOLICA S.A.', id, 28.0, -11.86161996, -41.41614405
FROM municipio WHERE nome = 'Bonito' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá VII', 'ASSURUA 1 III ENERGIA S.A.', id, 18.0, -11.07103885, -42.6788145
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Abil', 'CENTRAIS EOLICAS ABIL S.A.', id, 23.7, -14.27782253, -42.54345862
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tabua', 'CENTRAIS EOLICAS TABUA S.A.', id, 15.0, -14.3159116, -42.57214019
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Angico', 'CENTRAIS EOLICAS ANGICO S.A.', id, 8.1, -13.89479471, -42.70269754
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Jabuticaba', 'CENTRAIS EOLICAS JABUTICABA S.A.', id, 9.0, -14.32569297, -42.57185217
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Taboquinha', 'CENTRAIS EOLICAS TABOQUINHA S.A.', id, 21.6, -13.90772152, -42.70502063
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Folha de Serra', 'CENTRAIS EOLICAS FOLHA DA SERRA S.A', id, 21.0, -14.54996203, -42.58064365
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Jacarandá do Cerrado', 'CENTRAIS EOLICAS JACARANDA DO SERRADO S.A.', id, 21.0, -14.5561812, -42.58204778
FROM municipio WHERE nome = 'Licínio de Almeida' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Acácia', 'CENTRAIS EOLICAS ACACIA S.A', id, 16.2, -13.91558567, -42.69778192
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Vaqueta', 'CENTRAIS EOLICAS VAQUETA S.A.', id, 23.4, -14.25956065, -42.5543204
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Teiú 2', 'AXIA ENERGIA NORDESTE S.A.', id, 8.0, -14.44520868, -42.69171929
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caititu 2', 'AXIA ENERGIA NORDESTE S.A.', id, 10.0, -14.33182168, -42.61280189
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Acauã', 'AXIA ENERGIA NORDESTE S.A.', id, 6.0, -14.41277563, -42.65994184
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Arapapá', 'AXIA ENERGIA NORDESTE S.A.', id, 4.0, -14.43188347, -42.6918938
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Angical 2', 'AXIA ENERGIA NORDESTE S.A.', id, 10.0, -14.41903828, -42.6419293
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Carcará', 'AXIA ENERGIA NORDESTE S.A.', id, 10.0, -14.28355941, -42.59324515
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Corrupião 3', 'AXIA ENERGIA NORDESTE S.A.', id, 10.0, -14.27017056, -42.57548998
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caititu 3', 'AXIA ENERGIA NORDESTE S.A.', id, 10.0, -14.32017749, -42.61027503
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Papagaio', 'AXIA ENERGIA NORDESTE S.A.', id, 10.0, -14.48147697, -42.64087206
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Coqueirinho 2', 'AXIA ENERGIA NORDESTE S.A.', id, 16.0, -14.50305809, -42.65136408
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Casa Nova II', 'AXIA ENERGIA NORDESTE S.A.', id, 32.9, -9.27055058, -41.05009439
FROM municipio WHERE nome = 'Casa Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité C', 'EOLICA CAETITE C S.A.', id, 8.5, -14.10820737, -42.47461065
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Casa Nova III', 'AXIA ENERGIA NORDESTE S.A.', id, 28.2, -9.31480205, -41.11748472
FROM municipio WHERE nome = 'Casa Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Guarás I', 'VENTOS DOS GUARAS I ENERGIAS RENOVAVEIS S.A.', id, 30.0, -10.6060965, -40.65449747
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tamanduá Mirim 2', 'AXIA ENERGIA NORDESTE S.A.', id, 16.0, -14.47095341, -42.66552734
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Banda de Couro', 'BANDA DE COURO ENERGÉTICA S/A', id, 32.9, -9.84775015, -41.05389145
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Baraúnas II', 'Baraúnas II Energética S.A', id, 25.85, -9.87174698, -41.06310981
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 5', 'EOLICA UMBURANAS 5 S.A.', id, 17.5, -10.66499208, -41.60451759
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 1', 'EOLICA UMBURANAS 1 S.A.', id, 25.0, -10.67947732, -41.5206307
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 2', 'EOLICA UMBURANAS 2 S.A.', id, 25.0, -10.66991637, -41.50950699
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 3', 'EOLICA UMBURANAS 3 S.A.', id, 20.0, -10.64024826, -41.50758317
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 10', 'EOLICA UMBURANAS 10 S.A.', id, 20.0, -10.64988254, -41.59608593
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 9', 'EOLICA UMBURANAS 9 S.A.', id, 17.5, -10.63864428, -41.61434102
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 11', 'EOLICA UMBURANAS 11 S.A.', id, 15.0, -10.58632327, -41.49018325
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 13', 'EOLICA UMBURANAS 13 S.A.', id, 20.0, -10.64692468, -41.56466585
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 15', 'EOLICA UMBURANAS 15 S.A.', id, 20.0, -10.65281868, -41.49725368
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 16', 'EOLICA UMBURANAS 16 S.A.', id, 25.0, -10.69414809, -41.56871623
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 18', 'EOLICA UMBURANAS 18 S.A.', id, 7.5, -10.65027094, -41.58736582
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia II', 'VENTOS DA BAHIA F1 I ENERGIA S.A.', id, 27.0, -12.01558277, -41.44626231
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia IV', 'VENTOS DA BAHIA F1 III ENERGIA S.A.', id, 12.0, -11.9843248, -41.44454854
FROM municipio WHERE nome = 'Bonito' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia VIII', 'VENTOS DA BAHIA F1 II ENERGIA S.A.', id, 27.0, -12.03242556, -41.45653965
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 6', 'EOLICA UMBURANAS 6 S.A.', id, 20.0, -10.67344736, -41.60474488
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 8', 'EOLICA UMBURANAS 8 S.A.', id, 25.0, -10.69772228, -41.58232239
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Cristalândia I', 'CRISTALANDIA I EOLICA S.A.', id, 30.0, -13.93122764, -41.56086632
FROM municipio WHERE nome = 'Brumado' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Cristalândia II', 'CRISTALANDIA II EOLICA S.A.', id, 30.0, -13.97013345, -41.55017895
FROM municipio WHERE nome = 'Brumado' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Cristalândia III', 'CRISTALANDIA II EOLICA S.A.', id, 30.0, -14.00950837, -41.53327685
FROM municipio WHERE nome = 'Brumado' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Amescla', 'CENTRAIS EOLICAS AMESCLA S.A.', id, 13.5, -13.83506285, -42.7037769
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Angelim', 'CENTRAIS EOLICAS ANGELIM S.A.', id, 21.6, -14.044056, -42.65429301
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Barbatimão', 'CENTRAIS EOLICAS BARBATIMAO S.A.', id, 16.2, -13.82661024, -42.70428516
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Cedro', 'CENTRAIS EOLICAS CEDRO S.A', id, 12.0, -14.32898043, -42.57230176
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Facheio', 'CENTRAIS EOLICAS FACHEIO S.A.', id, 16.2, -13.99459845, -42.69078304
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Imburana Macho', 'CENTRAIS EOLICAS IMBURANA MACHO S.A.', id, 8.1, -13.86355786, -42.69089807
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Jataí', 'CENTRAIS EOLICAS JATAI S.A.', id, 16.2, -13.82937522, -42.69082273
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Juazeiro', 'CENTRAIS EOLICAS JUAZEIRO S.A.', id, 18.9, -13.84187496, -42.69056477
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Sabiu', 'CENTRAIS EOLICAS SABIU S.A.', id, 13.5, -14.01545978, -42.66330327
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umbuzeiro', 'CENTRAIS EOLICAS UMBUZEIRO S.A.', id, 18.9, -13.97988782, -42.66518946
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Vellozia', 'CENTRAIS EOLICAS VELLOZIA S.A.', id, 16.5, -14.25403541, -42.54905022
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Manineiro', 'CENTRAIS EOLICAS MANINEIRO S.A', id, 13.8, -14.01696831, -42.67641007
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pau D''Água', 'CENTRAIS EOLICAS PAU D''AGUA S.A.', id, 18.0, -14.53557321, -42.5758388
FROM municipio WHERE nome = 'Urandi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá III', 'ASSURUA 2 I ENERGIA S.A', id, 12.5, -11.17055586, -42.6813213
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá IV', 'ASSURUA 2 II ENERGIA S.A', id, 30.0, -11.04747424, -42.6875994
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Capoeiras III', 'ASSURUA 2 III ENERGIA S.A', id, 27.5, -11.17742485, -42.736905
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Curral de Pedras I', 'ASSURUA 2 IV ENERGIA S.A', id, 20.0, -11.13497767, -42.65347343
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Curral de Pedras II', 'ASSURUA 2 V ENERGIA S.A', id, 27.5, -11.21271233, -42.69985117
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Diamante II', 'ASSURUA 2 VI ENERGIA S.A', id, 17.5, -11.10651596, -42.67149802
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Diamante III', 'ASSURUA 2 VII ENERGIA S.A', id, 17.5, -11.0903652, -42.68300814
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Laranjeiras I', 'ASSURUA 2 VIII ENERGIA S.A', id, 27.5, -11.12278025, -42.68407001
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Laranjeiras II', 'ASSURUA 2 IX ENERGIA S.A', id, 30.0, -11.13107471, -42.69679751
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Laranjeiras V', 'ASSURUA 2 X ENERGIA S.A', id, 25.0, -11.11529657, -42.66990024
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina I', 'ENEL GREEN POWER DELFINA A EOLICA S.A.', id, 28.0, -10.05750878, -40.97868042
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina II', 'ENEL GREEN POWER DELFINA A EOLICA S.A.', id, 28.0, -10.03694564, -40.95861817
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina V', 'ENEL GREEN POWER DELFINA A EOLICA S.A.', id, 28.0, -10.02287638, -40.92836422
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Mulungu', 'CENTRAIS EOLICAS ITAPUA VII LTDA.', id, 13.5, -14.0054666, -42.66704098
FROM municipio WHERE nome = 'Guanambi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pau Santo', 'CENTRAIS EOLICAS UNHA D''ANTA S.A.', id, 18.9, -14.26697816, -42.53643261
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Quina', 'CENTRAIS EOLICAS ITAPUA VII LTDA.', id, 10.8, -13.9808404, -42.67878101
FROM municipio WHERE nome = 'Igaporã' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Morro Branco II', 'Morro Branco II Energética S.A.', id, 31.18, -9.76602418, -41.0226698
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia II', 'EOLICA SERRA DA BABILONIA II S.A', id, 28.2, -11.09546596, -41.30566977
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia IX', 'EOLICA SERRA DA BABILONIA IX S.A', id, 28.2, -11.09013968, -41.31536498
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia VI', 'EOLICA SERRA DA BABILONIA VI S.A', id, 25.85, -11.04899523, -41.32520275
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia VII', 'EOLICA SERRA DA BABILONIA VII S.A', id, 28.2, -11.05658661, -41.3154866
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia VIII', 'EOLICA SERRA DA BABILONIA VIII S.A', id, 28.2, -11.0838435, -41.30105957
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia  X', 'EOLICA SERRA DA BABILONIA X S.A', id, 28.2, -11.06810283, -41.3187875
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia XI', 'EOLICA SERRA DA BABILONIA XI S.A', id, 28.2, -11.07697816, -41.32158685
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia XII', 'EOLICA SERRA DA BABILONIA XII S.A', id, 28.2, -11.09886591, -41.31586717
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Inhambú 2', 'CIA BEAL DE ALIMENTOS', id, 16.45, -14.45136197, -42.65047171
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina III', 'ENEL GREEN POWER DELFINA B EOLICA S.A.', id, 28.0, -10.00491909, -40.93379381
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina IV', 'ENEL GREEN POWER DELFINA C EOLICA S.A.', id, 8.0, -10.06580256, -40.98092913
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina VI', 'ENEL GREEN POWER DELFINA D EOLICA S.A.', id, 30.0, -10.02923792, -40.95196878
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Delfina VII', 'ENEL GREEN POWER DELFINA E EOLICA S.A.', id, 30.0, -9.98632183, -40.9107399
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia I', 'PARQUE EOLICO VENTOS DA BAHIA I S.A.', id, 28.6, -11.98767533, -41.52585741
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia III', 'PARQUE EOLICO VENTOS DA BAHIA III S.A.', id, 30.8, -11.97474009, -41.52213627
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia IX', 'PARQUE EOLICO VENTOS DA BAHIA IX S.A.', id, 28.6, -11.98617146, -41.49142514
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia XIII', 'PARQUE EOLICO VENTOS DA BAHIA XIII S.A.', id, 49.5, -12.13129677, -41.49666159
FROM municipio WHERE nome = 'Iraquara' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia XIV', 'PARQUE EOLICO VENTOS DA BAHIA XIV S.A.', id, 33.0, -12.1101801, -41.50677639
FROM municipio WHERE nome = 'Iraquara' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano II', 'TUCANO F2 GERACAO DE ENERGIAS SPE S.A.', id, 24.8, -11.19013779, -38.78170522
FROM municipio WHERE nome = 'Tucano' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano III', 'TUCANO F3 GERACAO DE ENERGIAS SPE S.A.', id, 43.4, -11.32945525, -38.80453795
FROM municipio WHERE nome = 'Tucano' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano IV', 'TUCANO F4 GERACAO DE ENERGIAS SPE S.A.', id, 43.4, -11.42005176, -38.81289148
FROM municipio WHERE nome = 'Biritinga' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano VI', 'TUCANO F6 GERACAO DE ENERGIAS SPE S.A', id, 49.6, -11.22795737, -38.79201033
FROM municipio WHERE nome = 'Tucano' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano VII', 'TUCANO F7 GERACAO DE ENERGIAS SPE S.A.', id, 55.8, -11.25646723, -38.81124798
FROM municipio WHERE nome = 'Tucano' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano VIII', 'TUCANO F8 GERACAO DE ENERGIAS SPE S.A.', id, 49.6, -11.28593297, -38.79906981
FROM municipio WHERE nome = 'Tucano' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano X', 'TUCANO F1 GERACAO DE ENERGIAS SPE S.A.', id, 43.4, -11.2984963, -38.80133353
FROM municipio WHERE nome = 'Tucano' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 04', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 04 S.A', id, 29.4, -10.38700257, -40.45885143
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 15', 'Ventos de Santa Jacinta Energias Renováveis S.A.', id, 72.0, -11.07876275, -41.26807907
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 05', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 05 S.A', id, 42.0, -10.28982544, -40.43405344
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Boa Vista da Lagoinha', 'ENEL GREEN POWER BOA VISTA EOLICA S A', id, 30.0, -11.76135316, -41.38928707
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Caetité 03', 'TANQUE NOVO VI ENERGIAS RENOVAVEIS S.A.', id, 36.0, -13.70691891, -42.6001183
FROM municipio WHERE nome = 'Tanque Novo' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Caetité 04', 'TANQUE NOVO VII ENERGIAS RENOVAVEIS S.A.', id, 22.5, -13.6252034, -42.55086718
FROM municipio WHERE nome = 'Tanque Novo' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santo Abraão', 'ENEL GREEN POWER SAO ABRAAO EOLICA S.A', id, 28.0, -11.62494903, -41.35213098
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 01', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 01 S.A', id, 33.6, -10.32267124, -40.43672213
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 03', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 03 S.A', id, 33.6, -10.31053008, -40.44036494
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 06', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 06 S.A', id, 37.8, -10.41098237, -40.46644302
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 10', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 10 S.A', id, 42.0, -10.26507524, -40.43061877
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 11', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 11 S.A', id, 42.0, -10.24309451, -40.42447693
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 13', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 13 S.A', id, 42.0, -10.34475554, -40.4377539
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 14', 'PARQUE EOLICO VENTOS DE SAO JANUARIO 14 S.A', id, 42.0, -10.36782797, -40.44223337
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 16', 'Ventos de São Joaquim Energias Renováveis S.A.', id, 72.0, -11.09936157, -41.2808835
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 17', 'Ventos de São Júlio I Energias Renováveis S.A.', id, 72.0, -11.13073509, -41.2873319
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 18', 'Ventos de Santa Justina Energias Renováveis S.A.', id, 72.0, -11.12647376, -41.30334018
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Laranjeiras III', 'ASSURUA 3 I ENERGIA S.A.', id, 25.0, -11.03539473, -42.6321113
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Laranjeiras IX', 'ASSURUA 3 II ENERGIA S.A.', id, 25.0, -11.08122843, -42.61800203
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo III', 'CLWP EOLICA PARQUE III S.A.', id, 29.7, -10.50290276, -41.54008674
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo IV', 'CLWP EOLICA PARQUE IV S.A.', id, 29.7, -10.4897835, -41.51154837
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo V', 'CLWP EOLICA PARQUE V S.A.', id, 29.7, -10.42297013, -41.42850734
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo VI', 'CLWP EOLICA PARQUE VI S.A.', id, 29.7, -10.42114796, -41.39572975
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo VII', 'CLWP EOLICA PARQUE VII S.A.', id, 29.7, -10.52294314, -41.49361778
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XXI', 'CLWP EOLICA PARQUE XXI S.A.', id, 29.7, -10.44955113, -41.44806822
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 17', 'EOLICA UMBURANAS 17 S.A.', id, 22.5, -10.67234882, -41.57045134
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 19', 'EOLICA UMBURANAS 19 S.A.', id, 25.0, -10.59761022, -41.49465855
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 21', 'EOLICA UMBURANAS 21 S.A.', id, 22.5, -10.70100472, -41.59507662
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 23', 'EOLICA UMBURANAS 23 S.A.', id, 15.0, -10.62452756, -41.60279471
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburanas 25', 'EOLICA UMBURANAS 25 S.A.', id, 17.5, -10.64676367, -41.53336924
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Santa Dulce', 'ENEL GREEN POWER MORRO DO CHAPEU II EOLICA S.A', id, 28.0, -11.59853886, -41.33942579
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Santa Esperança', 'ENEL GREEN POWER MORRO DO CHAPEU II EOLICA S.A', id, 28.0, -11.60750442, -41.31574191
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos do São Mário', 'ENEL GREEN POWER MORRO DO CHAPEU I EOLICA S.A.', id, 30.0, -11.58741408, -41.22852865
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos do São Paulo', 'ENEL GREEN POWER MORRO DO CHAPEU I EOLICA S.A.', id, 28.0, -11.57867591, -41.30342444
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 15', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANCA 15 S.A.', id, 46.2, -11.5583383, -41.30172757
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 16', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 16 S.A.', id, 42.0, -11.53059227, -41.32024105
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 17', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 17 S.A.', id, 42.0, -11.50797772, -41.29458236
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 21', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 21 S.A.', id, 46.2, -11.49919413, -41.29109346
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 22', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 22 S.A.', id, 46.2, -11.51693908, -41.28006687
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 25', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 25 S.A.', id, 25.2, -11.55902476, -41.33004267
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 26', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANÇA 26 S.A.', id, 46.2, -11.52612662, -41.33171371
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo I', 'CLWP EÓLICA PARQUE I S.A.', id, 29.7, -10.46544666, -41.49346741
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo VIII', 'CLWP EÓLICA PARQUE VIII S.A.', id, 33.6, -10.4599988, -41.3931605
FROM municipio WHERE nome = 'Umburanas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XIII', 'CLWP EÓLICA PARQUE XIII S.A.', id, 42.0, -10.40246351, -41.4935042
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XII', 'CLWP EÓLICA PARQUE XII S.A.', id, 54.6, -10.4054121, -41.44864516
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XI', 'CLWP EÓLICA PARQUE XI S.A.', id, 16.8, -10.40273666, -41.44636052
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo X', 'CLWP EÓLICA PARQUE X S.A.', id, 33.6, -10.5066865, -41.48676691
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo largo IX', 'CLWP EÓLICA PARQUE IX S.A.', id, 25.2, -10.46258579, -41.39403857
FROM municipio WHERE nome = 'Umburanas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo II', 'CLWP EÓLICA PARQUE II S.A.', id, 29.7, -10.51103117, -41.45451405
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XIV', 'CLWP EÓLICA PARQUE XIV S.A.', id, 21.0, -10.44678334, -41.50083161
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XV', 'CLWP EÓLICA PARQUE XV S.A.', id, 29.7, -10.48909122, -41.43445434
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XVI', 'CLWP EÓLICA PARQUE XVI S.A.', id, 29.7, -10.44079793, -41.46759994
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XVII', 'CLWP EÓLICA PARQUE XVII S.A.', id, 29.4, -10.46295351, -41.51405275
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XVIII', 'CLWP EÓLICA PARQUE XVIII S.A.', id, 29.7, -10.42029019, -41.47863891
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XIX', 'CLWP EÓLICA PARQUE XIX S.A.', id, 33.6, -10.44090202, -41.57230736
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XX', 'CLWP EÓLICA PARQUE XX S.A.', id, 37.8, -10.48045063, -41.57687853
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Campo Largo XXII', 'CLWP EÓLICA PARQUE XXII S.A.', id, 33.6, -10.50942356, -41.61168567
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 3', 'VENTOS DE SAO VITOR 03 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.92544883, -42.55331652
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 4', 'VENTOS DE SAO VITOR 04 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.94114345, -42.56103358
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 10', 'VENTOS DE SAO VITOR 10 ENERGIAS RENOVAVEIS S/A', id, 44.8, -10.94805524, -42.5294563
FROM municipio WHERE nome = 'Itaguaçu da Bahia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 11', 'VENTOS DE SAO VITOR 11 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.93004437, -42.53707598
FROM municipio WHERE nome = 'Itaguaçu da Bahia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 12', 'VENTOS DE SAO VITOR 12 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.99014879, -42.51652227
FROM municipio WHERE nome = 'Itaguaçu da Bahia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 14', 'IRMAOS BOA LTDA', id, 38.4, -10.99169933, -42.50064184
FROM municipio WHERE nome = 'Itaguaçu da Bahia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 08', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANCA 8 S.A.', id, 25.2, -11.58693056, -41.33895335
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Esperança 13', 'ENEL GREEN POWER VENTOS DE SANTA ESPERANCA 13 S.A.', id, 33.6, -11.58342408, -41.35679636
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Emilia', 'CENTRAL EOLICA BABILONIA V S.A.', id, 27.3, -11.00636025, -41.22712795
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Aparecida', 'CENTRAL EOLICA BABILONIA I S.A.', id, 27.3, -10.91301565, -41.22142744
FROM municipio WHERE nome = 'Ourolândia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Santa Beatriz', 'CENTRAL EOLICA BABILONIA II S.A.', id, 27.3, -10.93969473, -41.22889662
FROM municipio WHERE nome = 'Ourolândia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos do São Gabriel', 'CENTRAL EOLICA BABILONIA III S.A.', id, 27.3, -10.95819775, -41.23343433
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Aurora', 'CENTRAL EOLICA BABILONIA IV S.A.', id, 27.3, -10.97611855, -41.21994954
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra do Reino IV', 'EOLICA PEDRA DO REINO IV S.A.', id, 20.0, -5.066152, -36.012362
FROM municipio WHERE nome = 'Sobradinho' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra das Almas I', 'PARQUE EOLICO SERRA DAS ALMAS I S.A.', id, 40.5, -14.77098218, -42.5672222
FROM municipio WHERE nome = 'Licínio de Almeida' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra das Almas II', 'PARQUE EOLICO SERRA DAS ALMAS II S.A.', id, 45.0, -14.79798325, -42.57213987
FROM municipio WHERE nome = 'Licínio de Almeida' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra das Almas III', 'PARQUE EOLICO SERRA DAS ALMAS III S.A.', id, 40.5, -14.81988175, -42.5626836
FROM municipio WHERE nome = 'Jacaraci' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra das Almas VI', 'PARQUE EOLICO SERRA DAS ALMAS VI S.A.', id, 45.0, -14.83093014, -42.58490473
FROM municipio WHERE nome = 'Urandi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 1', 'VENTOS DE SAO VITOR 01 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.89804156, -42.5386471
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 2', 'VENTOS DE SAO VITOR 02 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.91153919, -42.54620723
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 5', 'VENTOS DE SAO VITOR 05 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.9562976, -42.56218063
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 6', 'VENTOS DE SAO VITOR 06 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.97171047, -42.56177997
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 7', 'VENTOS DE SAO VITOR 07 ENERGIAS RENOVAVEIS S/A', id, 44.8, -10.98737242, -42.56089232
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 8', 'VENTOS DE SAO VITOR 08 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.98612573, -42.53962076
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 9', 'VENTOS DE SAO VITOR 09 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.9711941, -42.5369186
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Vitor 13', 'VENTOS DE SAO VITOR 13 ENERGIAS RENOVAVEIS S/A', id, 32.0, -10.9761812, -42.51015523
FROM municipio WHERE nome = 'Itaguaçu da Bahia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia XVIII', 'PARQUE EOLICO VENTOS DA BAHIA XVIII S.A.', id, 28.6, -12.01548693, -41.49544765
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia XXVII', 'PARQUE EOLICO VENTOS DA BAHIA XXVII S.A.', id, 49.5, -12.06263011, -41.48626537
FROM municipio WHERE nome = 'Mulungu do Morro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Tanque Novo 03', 'TANQUE NOVO III ENERGIAS RENOVAVEIS S.A.', id, 13.5, -13.64982856, -42.57715616
FROM municipio WHERE nome = 'Tanque Novo' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Fogo', 'SERRA DO FOGO ENERGÉTICA S.A.', id, 31.18, -9.805084, -41.022709
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Vento', 'SERRA DO VENTO ENERGÉTICA S.A.', id, 31.18, -9.81603, -41.038889
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Umburana de Cheiro', 'UMBURANA DE CHEIRO ENERGÉTICA S.A.', id, 31.18, -9.835541, -41.042697
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos da Bahia XXIII', 'PARQUE EOLICO VENTOS DA BAHIA XXIII S.A.', id, 49.5, -12.08423134, -41.49362596
FROM municipio WHERE nome = 'Souto Soares' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'ZEUS II', 'ENEL GREEN POWER ZEUS II', id, 29.4, -9.96984534, -40.88155455
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Tucano XVI', 'TUCANO F2 GERACAO DE ENERGIAS SPE S.A.', id, 12.4, -11.35996856, -38.80407728
FROM municipio WHERE nome = 'Araci' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Baraúnas XX', 'Baraúnas XX Energética S.A.', id, 31.18, -9.78915068, -41.01289439
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ararinha Azul', 'CIA BEAL DE ALIMENTOS', id, 21.15, -14.41267553, -42.64976084
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité D', 'EOLICA CAETITE D S.A.', id, 50.4, -14.1145591, -42.49976533
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité E', 'EOLICA CAETITE ECO S.A.', id, 37.8, -14.07435991, -42.51232761
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Caetité F', 'EOLICA CAETITE F S.A.', id, 25.2, -14.07535267, -42.52628322
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Corrupião II', 'CIA BEAL DE ALIMENTOS', id, 18.8, -14.41536206, -42.6411244
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra do Reino V', 'EOLICA PEDRA DO REINO V S.A.', id, 16.0, -9.54944968, -40.89579893
FROM municipio WHERE nome = 'Sobradinho' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia A', 'EOLICA SDB ALFA S.A.', id, 21.2, -11.1203576, -41.32421958
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra das Almas IV', 'PARQUE EOLICO SERRA DAS ALMAS IV S.A.', id, 45.0, -14.79229001, -42.60132273
FROM municipio WHERE nome = 'Licínio de Almeida' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra das Almas V', 'PARQUE EOLICO SERRA DAS ALMAS V S.A.', id, 45.0, -14.81507581, -42.59621515
FROM municipio WHERE nome = 'Urandi' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Teiú 3', 'CIA BEAL DE ALIMENTOS', id, 23.5, -14.40879642, -42.64864637
FROM municipio WHERE nome = 'Pindaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 19', 'Ventos de São João XXIII Energias Renováveis S.A.', id, 72.0, -11.13989048, -41.30848805
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 20', 'VENTOS DE SÃO BENTO ENERGIAS RENOVÁVEIS S.A', id, 50.4, -10.44202226, -40.46165762
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 21', 'VENTOS DE SÃO GALVÃO ENERGIAS RENOVÁVEIS S/A', id, 50.4, -10.42861608, -40.50689139
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de São Januário 22', 'VENTOS DE SANTO ELOY ENERGIAS RENOVÁVEIS S/A', id, 50.4, -10.47361786, -40.52396426
FROM municipio WHERE nome = 'Campo Formoso' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Casa Nova A', 'AXIA ENERGIA NORDESTE S.A.', id, 27.0, -9.26020855, -41.01681339
FROM municipio WHERE nome = 'Casa Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Fazenda Belmonte', 'CANINANA ENERGY LTDA', id, 0.0, -10.37972222, -41.61861111
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Fazenda Melancia', 'CANINANA ENERGY LTDA', id, 0.0, -10.31774325, -41.52614208
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Fazenda Floresta', 'CANINANA ENERGY LTDA', id, 0.0, -10.37990399, -41.61867627
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Baraúnas XV (Antiga Massaroca I)', 'Baraúnas XV Energética S.A.', id, 48.51, -9.75010326, -40.97532638
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Baraúnas IV (Antiga Massaroca II)', 'BARAUNAS IV ENERGETICA S.A.', id, 41.58, -9.67418218, -41.11013771
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 01', 'OSLO I S/A', id, 51.3, -11.38881453, -42.14715142
FROM municipio WHERE nome = 'Uibaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 02', 'OSLO II S/A.', id, 11.4, -11.37453368, -42.17721284
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 03', 'OSLO III S/A.', id, 39.9, -11.40948463, -42.15943416
FROM municipio WHERE nome = 'Uibaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 05', 'OSLO IV S/A.', id, 34.2, -11.45400602, -42.16921949
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 06', 'OSLO VI S/A.', id, 34.2, -11.48827849, -42.13403787
FROM municipio WHERE nome = 'Uibaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 07', 'OSLO II S/A.', id, 17.1, -11.38125707, -42.17558097
FROM municipio WHERE nome = 'Uibaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 08', 'OSLO IV S/A.', id, 28.5, -11.46907711, -42.16582734
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugênia 09', 'OSLO V S/A.', id, 39.9, -11.54041079, -42.11596769
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 10', 'OSLO IX S/A.', id, 45.6, -11.50062055, -42.14534355
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 11', 'OSLO VIII S/A.', id, 28.5, -11.44346082, -42.15958093
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 12', 'OSLO III S/A.', id, 39.9, -11.42750927, -42.157872
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugênia 13', 'OSLO X S/A.', id, 45.6, -11.52087909, -42.14410409
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Eugenia 14', 'OSLO V S/A.', id, 22.8, -11.52678665, -42.11847006
FROM municipio WHERE nome = 'Ibipeba' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Fazenda Alegre', 'CANINANA ENERGY LTDA', id, 0.0, -10.33009536, -41.43470186
FROM municipio WHERE nome = 'Sento Sé' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia B', 'EOLICA SDB B S.A.', id, 31.8, -11.12657611, -41.32641931
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia C', 'EOLICA SDB C S.A.', id, 26.5, -11.13278547, -41.32862823
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia D', 'EOLICA SDB D S.A.', id, 31.8, -11.14265824, -41.33155279
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia E', 'EOLICA SDB ECO S.A.', id, 26.5, -11.14937021, -41.33303391
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Babilônia F', 'EOLICA SDB F S.A.', id, 21.2, -11.15938659, -41.33606986
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Brejinhos A', 'EOLICA BREJINHOS ALFA S.A', id, 37.8, -14.17986019, -42.53302446
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Brejinhos B', 'EOLICA BREJINHOS B S.A.', id, 42.0, -14.20685148, -42.53267662
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Oitis 21', 'OITIS 21 ENERGIA RENOVÁVEL S.A.', id, 44.0, -9.03160148, -41.70058954
FROM municipio WHERE nome = 'Casa Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Oitis 22', 'OITIS 22 ENERGIA RENOVÁVEL S.A.', id, 49.5, -9.07383623, -41.71480375
FROM municipio WHERE nome = 'Casa Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra da Mangabeira', 'SERRA DA MANGABEIRA S/A.', id, 79.8, -11.43252238, -42.13742953
FROM municipio WHERE nome = 'Uibaí' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 1', 'Enel Green Power Aroeira 01 S.A.', id, 47.3, -10.7860708, -41.44222225
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 2', 'Enel Green Power Aroeira 02 S.A.', id, 43.0, -10.80514852, -41.43627081
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 3', 'Enel Green Power Aroeira 03 S.A.', id, 43.0, -10.82725136, -41.45113853
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 4', 'Enel Green Power Aroeira 04 S.A.', id, 43.0, -10.81859762, -41.48688916
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 5', 'Enel Green Power Aroeira 05 S.A.', id, 43.0, -10.78519203, -41.49349595
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 6', 'Enel Green Power Aroeira 06 S.A.', id, 38.7, -10.766796, -41.53010862
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 7', 'Enel Green Power Aroeira 07 S.A.', id, 47.3, -10.75943554, -41.49842884
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aroeira 8', 'Enel Green Power Aroeira 08 S.A.', id, 43.0, -10.75481294, -41.47878916
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Caetité 01', 'TANQUE NOVO IV ENERGIAS RENOVAVEIS S.A.', id, 27.0, -13.6583284, -42.57601608
FROM municipio WHERE nome = 'Tanque Novo' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Caetité 02', 'TANQUE NOVO V ENERGIAS RENOVAVEIS S.A.', id, 36.0, -13.68371013, -42.59385791
FROM municipio WHERE nome = 'Tanque Novo' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Tanque Novo 01', 'TANQUE NOVO I ENERGIAS RENOVAVEIS S.A.', id, 27.0, -13.64056133, -42.54798357
FROM municipio WHERE nome = 'Tanque Novo' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Aura Tanque Novo 02', 'TANQUE NOVO II ENERGIAS RENOVAVEIS S.A.', id, 18.0, -13.67715706, -42.57321782
FROM municipio WHERE nome = 'Caetité' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Canudos I', 'EOLICA CANUDOS III SPE S.A.', id, 49.7, -10.18922908, -38.77506982
FROM municipio WHERE nome = 'Canudos' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Canudos II', 'EOLICA CANUDOS II SPE S.A.', id, 49.7, -10.14664538, -38.77132041
FROM municipio WHERE nome = 'Canudos' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santo Antonio 04', 'Ventos de Santa Felicidade Energias Renováveis S.A.', id, 67.5, -11.06293107, -41.27985095
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra Pintada I', 'USINA EÓLICA PEDRA PINTADA A LTDA.', id, 49.5, -10.77992924, -41.21338827
FROM municipio WHERE nome = 'Ourolândia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra Pintada II', 'USINA EÓLICA PEDRA PINTADA B LTDA.', id, 49.5, -10.7417214, -41.20693885
FROM municipio WHERE nome = 'Ourolândia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra Pintada III', 'USINA EOLICA PEDRA PINTADA C LTDA.', id, 45.0, -10.70554318, -41.20747111
FROM municipio WHERE nome = 'Ourolândia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Pedra Pintada IV', 'USINA EOLICA PEDRA PINTADA D LTDA.', id, 49.5, -10.66862703, -41.1850732
FROM municipio WHERE nome = 'Ourolândia' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 1', 'EÓLICA SERRA DO ASSURUÁ 1 LTDA.', id, 45.0, -11.37032999, -42.60718563
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 2', 'EÓLICA SERRA DO ASSURUÁ 2 LTDA.', id, 22.5, -11.40945403, -42.62116527
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 3', 'EÓLICA SERRA DO ASSURUÁ 3 LTDA', id, 40.5, -11.41682779, -42.60672329
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 4', 'EÓLICA SERRA DO ASSURUÁ 4 LTDA', id, 36.0, -11.41859485, -42.59187916
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 5', 'EÓLICA SERRA DO ASSURUÁ 5 LTDA', id, 36.0, -11.49936917, -42.58843712
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 6', 'EÓLICA SERRA DO ASSURUÁ 6 LTDA', id, 36.0, -11.52760458, -42.57613797
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 7', 'EÓLICA SERRA DO ASSURUÁ 7 LTDA', id, 27.0, -11.54919379, -42.59480231
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 8', 'EÓLICA SERRA DO ASSURUÁ 8 LTDA.', id, 22.5, -11.53657115, -42.5549033
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 9', 'EÓLICA SERRA DO ASSURUÁ 9 LTDA.', id, 40.5, -11.5070182, -42.59879875
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 10', 'EÓLICA SERRA DO ASSURUÁ 10 LTDA.', id, 40.5, -11.51235777, -42.57906321
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 11', 'EÓLICA SERRA DO ASSURUÁ 11 LTDA.', id, 40.5, -11.52265266, -42.54811515
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 12', 'EÓLICA SERRA DO ASSURUÁ 12 LTDA.', id, 27.0, -11.56801525, -42.58706275
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 13', 'EÓLICA SERRA DO ASSURUÁ 13 LTDA.', id, 36.0, -11.58731884, -42.58840939
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 14', 'EÓLICA SERRA DO ASSURUÁ 14 LTDA.', id, 27.0, -11.59500697, -42.58928742
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 15', 'EÓLICA SERRA DO ASSURUÁ 15 LTDA.', id, 36.0, -11.47298307, -42.61702492
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 16', 'EÓLICA SERRA DO ASSURUÁ 16 LTDA.', id, 40.5, -11.46293235, -42.57286184
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 17', 'A. ANGELONI &amp', id, 40.5, -11.46593104, -42.5586094
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 18', 'Eólica Serra do Assuruá 18 Ltda.', id, 40.5, -11.46663628, -42.53606785
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 19', 'Eólica Serra do Assuruá 19 Ltda.', id, 40.5, -11.4859242, -42.56789212
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 20', 'Eólica Serra do Assuruá 20 Ltda', id, 40.5, -11.52906287, -42.62359045
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 21', 'Eólica Serra do Assuruá 21 Ltda.', id, 40.5, -11.49587231, -42.54625827
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 22', 'Eólica Serra do Assuruá 22 Ltda.', id, 27.0, -11.56613233, -42.56076855
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 23', 'Eólica Serra do Assuruá 23 Ltda', id, 36.0, -11.53970024, -42.52747853
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Serra do Assuruá 24', 'Eólica Serra do Assuruá 24 Ltda', id, 27.0, -11.60390806, -42.55601392
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 01', 'VENTOS DE SAO ROMUALDO ENERGIAS RENOVAVEIS S.A.', id, 45.0, -12.6191959, -42.05772247
FROM municipio WHERE nome = 'Ibitiara' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 02', 'VENTOS DE SAO TEOFANO ENERGIAS RENOVAVEIS S.A.', id, 45.0, -12.6396309, -42.0540304
FROM municipio WHERE nome = 'Ibitiara' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 03', 'VENTOS DE SAO TEONAS ENERGIAS RENOVAVEIS S.A.', id, 45.0, -12.66016558, -42.05282891
FROM municipio WHERE nome = 'Ibitiara' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 04', 'VENTOS DE SAO THOMAS ENERGIAS RENOVAVEIS S.A.', id, 40.5, -12.68160891, -42.04968463
FROM municipio WHERE nome = 'Ibitiara' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 05', 'VENTOS DE SAO TILAO ENERGIAS RENOVAVEIS S.A.', id, 40.5, -12.70199688, -42.04668041
FROM municipio WHERE nome = 'Novo Horizonte' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 06', 'VENTOS DE SANTO UBALDO ENERGIAS RENOVAVEIS S.A.', id, 40.5, -12.72241588, -42.04323336
FROM municipio WHERE nome = 'Boninal' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 07', 'VENTOS DE SANTO URBANO I ENERGIAS RENOVAVEIS S.A.', id, 40.5, -12.74632859, -42.03321201
FROM municipio WHERE nome = 'Boninal' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 08', 'VENTOS DE SAO VIGILIO ENERGIAS RENOVAVEIS S.A.', id, 45.0, -12.77090365, -42.03074457
FROM municipio WHERE nome = 'Novo Horizonte' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 09', 'VENTOS DE SANTA VIRGINIA ENERGIAS RENOVAVEIS S.A.', id, 40.5, -12.8476983, -42.0674189
FROM municipio WHERE nome = 'Novo Horizonte' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 10', 'VENTOS DE SAO VLADIMIR ENERGIAS RENOVAVEIS S.A.', id, 40.5, -12.86804119, -42.06443001
FROM municipio WHERE nome = 'Novo Horizonte' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 4 I', 'ASSURUA 4 I ENERGIA S.A', id, 36.0, -11.02539165, -42.64105068
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 4 II', 'ASSURUA 4 II ENERGIA S.A', id, 31.5, -11.03596114, -42.61452038
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 4 III', 'ASSURUA 4 III ENERGIA S.A', id, 36.0, -11.06036901, -42.63062401
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 4 IV', 'ASSURUA 4 IV ENERGIA S.A', id, 36.0, -11.08393419, -42.62663031
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 4 V', 'ASSURUA 4 V ENERGIA S.A', id, 36.0, -11.10734948, -42.62924124
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 4 VI', 'ASSURUA 4 VI ENERGIA S.A', id, 36.0, -11.06616944, -42.60300235
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Morro 1', 'MORRO DO CRUZEIRO I S.A.', id, 45.6, -12.25090352, -42.37421592
FROM municipio WHERE nome = 'Brotas de Macaúbas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Morro 2', 'MORRO DO CRUZEIRO II S.A.', id, 34.2, -12.22453209, -42.37804034
FROM municipio WHERE nome = 'Brotas de Macaúbas' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 14', 'Ventos de São Julião Energias Renováveis S.A.', id, 72.0, -11.05163371, -41.2631038
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 15', 'Ventos de São Raimundo Energias Renováveis S.A.', id, 72.0, -11.0337387, -41.23768996
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santa Luzia 16', 'Ventos de São Roberto Energias Renováveis S.A.', id, 72.0, -11.00649616, -41.25645584
FROM municipio WHERE nome = 'Várzea Nova' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santo Antônio 05', 'Ventos de Santa Ana Energias Renováveis S.A.', id, 67.5, -11.04464558, -41.28982781
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santo Antônio 06', 'Ventos de Santa Sabina Energias Renováveis S.A.', id, 67.5, -11.01949045, -41.29400553
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santo Antônio 07', 'Ventos de Santa Irene Energias Renováveis S.A.', id, 72.0, -11.02428258, -41.29846104
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Ventos de Santo Antônio 08', 'Ventos de Santa Rufina Energias Renováveis S.A.', id, 63.0, -11.04836694, -41.30555544
FROM municipio WHERE nome = 'Morro do Chapéu' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 5 I', 'CARGILL AGRICOLA S A', id, 40.6, -10.98219659, -42.61010211
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 5 II', 'DIGITAL DC BRASIL LTDA.', id, 46.4, -10.99740745, -42.62420222
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 5 III', 'CARGILL AGRICOLA S A', id, 40.6, -11.01855191, -42.67649199
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 5 IV', 'MANAUS AMBIENTAL S.A.', id, 46.4, -11.02188033, -42.60370852
FROM municipio WHERE nome = 'Xique' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 5 V', 'MANAUS AMBIENTAL S.A.', id, 34.8, -11.14133575, -42.63391168
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)
SELECT 'Assuruá 5 VI', 'MANAUS AMBIENTAL S.A.', id, 34.8, -11.02376103, -42.58903056
FROM municipio WHERE nome = 'Gentio do Ouro' LIMIT 1;

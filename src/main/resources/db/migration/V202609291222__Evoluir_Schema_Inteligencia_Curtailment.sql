-- Evolução de schema para inteligência regulatória (REL/CNF/ENE), PLD dinâmico, subestação e previsão solar (Curva do Pato)

ALTER TABLE usina
    ADD COLUMN id_ons VARCHAR(60) NULL,
    ADD COLUMN ceg VARCHAR(40) NULL,
    ADD COLUMN nome_conjunto_ons VARCHAR(150) NULL,
    ADD COLUMN subestacao_conexao VARCHAR(150) NULL,
    ADD COLUMN preco_ppa_mwh DECIMAL(10, 2) NOT NULL DEFAULT 200.00;

ALTER TABLE historico_corte_ons
    ADD COLUMN energia_estimada_mwh DECIMAL(14, 4) NULL,
    ADD COLUMN energia_verificada_mwh DECIMAL(14, 4) NULL,
    ADD COLUMN cod_razao_restricao VARCHAR(30) NULL,
    ADD COLUMN dsc_restricao VARCHAR(500) NULL,
    ADD COLUMN subestacao_conexao VARCHAR(150) NULL,
    ADD COLUMN vento_verificado_ms DECIMAL(8, 2) NULL,
    ADD COLUMN valorado_pld_rs DECIMAL(16, 2) NULL;

ALTER TABLE historico_corte_ons_bloco
    ADD COLUMN cod_razao_restricao VARCHAR(30) NULL,
    ADD COLUMN dsc_restricao VARCHAR(500) NULL,
    ADD COLUMN subestacao_conexao VARCHAR(150) NULL,
    ADD COLUMN vento_verificado_ms DECIMAL(8, 2) NULL,
    ADD COLUMN pld_mwh DECIMAL(12, 2) NULL;

ALTER TABLE leitura_climatica
    ADD COLUMN radiacao_solar_wm2 DECIMAL(10, 2) NULL,
    ADD COLUMN rajada_vento_ms DECIMAL(10, 2) NULL;

CREATE INDEX idx_corte_ons_razao ON historico_corte_ons (cod_razao_restricao);
CREATE INDEX idx_corte_ons_bloco_razao ON historico_corte_ons_bloco (cod_razao_restricao);
CREATE INDEX idx_usina_subestacao ON usina (subestacao_conexao);

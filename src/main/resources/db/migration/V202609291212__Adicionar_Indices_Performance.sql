-- Índices para melhorar a performance de buscas por período e usina
CREATE INDEX idx_historico_corte_ons_data ON historico_corte_ons(data_corte);
CREATE INDEX idx_leitura_climatica_usina_data ON leitura_climatica(usina_id, data_hora);
CREATE INDEX idx_previsao_risco_usina_data ON previsao_risco(usina_id, data_referencia);

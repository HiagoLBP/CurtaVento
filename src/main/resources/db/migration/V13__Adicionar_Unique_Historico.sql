-- Remove duplicatas mantendo apenas o maior ID
DELETE t1 FROM historico_corte_ons t1
INNER JOIN historico_corte_ons t2 
WHERE t1.id < t2.id 
  AND t1.usina_id = t2.usina_id 
  AND t1.data_corte = t2.data_corte;

-- Adiciona a constraint UNIQUE para blindar o banco
ALTER TABLE historico_corte_ons 
ADD CONSTRAINT uk_usina_data_corte UNIQUE (usina_id, data_corte);

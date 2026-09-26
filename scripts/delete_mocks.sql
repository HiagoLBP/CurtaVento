SET FOREIGN_KEY_CHECKS=0;
DELETE FROM leitura_climatica WHERE usina_id IN (1,2,3,4);
DELETE FROM alerta WHERE usina_id IN (1,2,3,4);
DELETE FROM previsao_risco WHERE usina_id IN (1,2,3,4);
DELETE FROM historico_corte_ons WHERE usina_id IN (1,2,3,4);
DELETE FROM usina WHERE id IN (1,2,3,4);
SET FOREIGN_KEY_CHECKS=1;

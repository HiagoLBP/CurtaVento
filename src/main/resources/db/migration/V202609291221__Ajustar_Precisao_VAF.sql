-- Migration: Ajustar precisao de vaf_ano_base e sincronizar valores

ALTER TABLE municipio MODIFY COLUMN vaf_ano_base DECIMAL(19,7);

UPDATE municipio SET vaf_ano_base = ipm WHERE ipm IS NOT NULL;

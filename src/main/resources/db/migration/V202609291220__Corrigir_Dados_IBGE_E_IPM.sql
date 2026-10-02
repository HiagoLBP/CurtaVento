-- Migration: Corrigir Dados IBGE e IPM (Ajustar grafia e garantir coluna ipm)

SET @ipm_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'municipio'
      AND column_name = 'ipm'
);

SET @add_ipm_column = IF(
    @ipm_column_exists = 0,
    'ALTER TABLE municipio ADD COLUMN ipm DECIMAL(10,7)',
    'SELECT 1'
);

PREPARE add_ipm_column_statement FROM @add_ipm_column;
EXECUTE add_ipm_column_statement;
DEALLOCATE PREPARE add_ipm_column_statement;

-- Corrigir nome de Dias d'Ávila
UPDATE municipio SET nome = 'Dias d''Ávila' WHERE codigo_ibge = '2910057';

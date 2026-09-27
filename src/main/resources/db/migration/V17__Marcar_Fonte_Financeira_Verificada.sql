-- Registros financeiros existentes não possuem fonte verificável conhecida.
-- O valor original é preservado, mas não pode alimentar indicadores ou alertas.
ALTER TABLE impacto_vaf
    ADD COLUMN financeiro_disponivel BOOLEAN NOT NULL DEFAULT FALSE;

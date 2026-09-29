-- Valores financeiros só podem ser persistidos quando houver fonte verificável
-- para preço de energia e regra fiscal aplicável ao município.
ALTER TABLE impacto_vaf
    MODIFY perda_receita_estimada DECIMAL(15, 2) NULL,
    MODIFY impacto_vaf_estimado DECIMAL(15, 2) NULL;

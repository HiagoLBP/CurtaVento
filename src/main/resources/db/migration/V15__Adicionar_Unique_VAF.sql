-- Garantir que não existam relatórios VAF duplicados para o mesmo município e ano
-- Primeiro remove duplicidades mantendo o maior ID
DELETE t1 
FROM impacto_vaf t1
INNER JOIN impacto_vaf t2 
WHERE t1.id < t2.id 
  AND t1.municipio_id = t2.municipio_id 
  AND t1.ano_referencia = t2.ano_referencia;

-- Depois adiciona a constraint
ALTER TABLE impacto_vaf ADD CONSTRAINT uk_municipio_ano UNIQUE (municipio_id, ano_referencia);

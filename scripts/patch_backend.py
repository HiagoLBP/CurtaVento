import os
import re

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"

# 1. Update HistoricoCorteOnsRepository.java
repo_file = os.path.join(base_dir, "src/main/java/com/curtavento/curta_vento/corte/HistoricoCorteOnsRepository.java")
with open(repo_file, "r", encoding="utf-8") as f:
    repo_content = f.read()

repo_content = repo_content.replace(
    "@org.springframework.data.jpa.repository.Query(\"SELECT c.usina.municipio.nome, COUNT(DISTINCT c.usina.id), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOns c GROUP BY c.usina.municipio.nome ORDER BY SUM(c.energiaCortadaMwh) DESC\")\n    List<Object[]> findImpactoPorMunicipio();",
    "@org.springframework.data.jpa.repository.Query(\"SELECT c.usina.municipio.nome, COUNT(DISTINCT c.usina.id), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOns c WHERE YEAR(c.dataCorte) = :ano GROUP BY c.usina.municipio.nome ORDER BY SUM(c.energiaCortadaMwh) DESC\")\n    List<Object[]> findImpactoPorMunicipio(@org.springframework.data.repository.query.Param(\"ano\") int ano);"
)

with open(repo_file, "w", encoding="utf-8") as f:
    f.write(repo_content)

# 2. Update ImpactoMunicipalController.java
impacto_ctrl = os.path.join(base_dir, "src/main/java/com/curtavento/curta_vento/corte/ImpactoMunicipalController.java")
with open(impacto_ctrl, "r", encoding="utf-8") as f:
    imp_content = f.read()

imp_content = imp_content.replace(
    "private static final double PRECO_MADI_MWH = 150.00;",
    "private static final double PRECO_MADI_MWH = 200.00;"
)
imp_content = imp_content.replace(
    "public ResponseEntity<List<Map<String, Object>>> getImpactoPorMunicipio() {",
    "public ResponseEntity<List<Map<String, Object>>> getImpactoPorMunicipio(@org.springframework.web.bind.annotation.RequestParam(defaultValue = \"2026\", required = false) int ano) {"
)
imp_content = imp_content.replace(
    "List<Object[]> resultados = repository.findImpactoPorMunicipio();",
    "List<Object[]> resultados = repository.findImpactoPorMunicipio(ano);"
)

with open(impacto_ctrl, "w", encoding="utf-8") as f:
    f.write(imp_content)

# 3. Update DashboardController.java
dash_ctrl = os.path.join(base_dir, "src/main/java/com/curtavento/curta_vento/dashboard/DashboardController.java")
with open(dash_ctrl, "r", encoding="utf-8") as f:
    dash_content = f.read()

# Replace the energiaCortada part in Usinas
dash_content = dash_content.replace(
    "dto.put(\"energiaCortada\", somaCortesUsina);",
    "dto.put(\"energiaCortada\", somaCortesUsina);\n            dto.put(\"prejuizoFinanceiro\", somaCortesUsina * 200.0);"
)

# Replace the Municipios part
new_muni_logic = """
        // 2. Municípios e Totais do VAF baseados no ANO selecionado no Dropdown
        List<Map<String, Object>> municipiosDTO = new ArrayList<>();
        double somaEnergiaTotal = 0.0;
        double somaImpactoTotal = 0.0;
        double somaPerdaReceitaTotal = 0.0;

        List<Object[]> impactosMun = corteRepository.findImpactoPorMunicipio(ano);
        for (Object[] linha : impactosMun) {
            String nome = (String) linha[0];
            java.math.BigDecimal totalCortado = (java.math.BigDecimal) linha[2];
            double mwh = totalCortado != null ? totalCortado.doubleValue() : 0.0;
            
            double perdaReceitaReal = mwh * 200.0;
            double impactoReal = perdaReceitaReal * 0.18 * 0.25; // 18% ICMS, 25% repasse VAF
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("nome", nome);
            dto.put("valor", impactoReal);
            dto.put("max", 50000000.0);
            dto.put("trend", "Calculado");
            
            somaEnergiaTotal += mwh;
            somaImpactoTotal += impactoReal;
            somaPerdaReceitaTotal += perdaReceitaReal;
            
            municipiosDTO.add(dto);
        }
"""

# Regex substitution for the Municipios section
import re
dash_content = re.sub(
    r"// 2\. Municpios e Totais do VAF baseados no ANO selecionado no Dropdown.*?// Injeǜo dos dados analisados",
    new_muni_logic.replace("", "?") + "\n        // Injeção dos dados analisados",
    dash_content,
    flags=re.DOTALL | re.IGNORECASE
)
# Fix encoding issues in the search string for regex fallback
dash_content = re.sub(
    r"// 2\. Munic.*?// Inje",
    new_muni_logic + "\n        // Inje",
    dash_content,
    flags=re.DOTALL
)

with open(dash_ctrl, "w", encoding="utf-8") as f:
    f.write(dash_content)

print("Java files updated!")

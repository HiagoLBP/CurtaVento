import os
import re

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"
dash_ctrl = os.path.join(base_dir, "src/main/java/com/curtavento/curta_vento/dashboard/DashboardController.java")

with open(dash_ctrl, "r", encoding="utf-8") as f:
    content = f.read()

# Add 'limite' parameter to getResumoGeral
content = content.replace(
    'public ResponseEntity<Map<String, Object>> getResumoGeral(@RequestParam(defaultValue = "2026", required = false) int ano) {',
    'public ResponseEntity<Map<String, Object>> getResumoGeral(@RequestParam(defaultValue = "2026", required = false) int ano, @RequestParam(defaultValue = "1000", required = false) int limite) {'
)

# Optimization: Pre-fetch energy cuts (findRankingCortesAno) to avoid 381 separate queries!
opt_code = """
        List<Object[]> rankingCortes = corteRepository.findRankingCortesAno(ano);
        Map<String, Double> cortesMap = new HashMap<>();
        for (Object[] row : rankingCortes) {
            String uName = (String) row[0];
            java.math.BigDecimal val = (java.math.BigDecimal) row[1];
            cortesMap.put(uName, val != null ? val.doubleValue() : 0.0);
        }

        List<Map<String, Object>> usinasDTO = new ArrayList<>();
"""

content = re.sub(
    r"List<Map<String, Object>> usinasDTO = new ArrayList\<\>\(\);",
    opt_code,
    content
)

# Replace the slow query inside the loop
slow_query = """            java.time.LocalDateTime inicioAno = java.time.LocalDateTime.of(ano, 1, 1, 0, 0);
            java.time.LocalDateTime fimAno = java.time.LocalDateTime.of(ano, 12, 31, 23, 59, 59);
            List<com.curtavento.curta_vento.corte.HistoricoCorteOns> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(u.getId(), inicioAno, fimAno);
            double somaCortesUsina = cortes.stream()
                .filter(c -> c.getEnergiaCortadaMwh() != null)
                .mapToDouble(c -> c.getEnergiaCortadaMwh().doubleValue())
                .sum();"""

fast_query = """            double somaCortesUsina = cortesMap.getOrDefault(u.getNome(), 0.0);"""

content = content.replace(slow_query, fast_query)

# Sort the list and apply the limit
sort_and_limit = """
        // Ordena por maior prejuizo e aplica o limite
        usinasDTO.sort((a, b) -> Double.compare((Double) b.get("prejuizoFinanceiro"), (Double) a.get("prejuizoFinanceiro")));
        if (limite < usinasDTO.size()) {
            usinasDTO = usinasDTO.subList(0, limite);
        }

        // 2. Municípios e Totais do VAF
"""

content = re.sub(
    r"// 2\. Munic[^C]*?Totais do VAF",
    sort_and_limit.replace("\\", "\\\\"),
    content
)

with open(dash_ctrl, "w", encoding="utf-8") as f:
    f.write(content)
print("DashboardController.java optimized!")

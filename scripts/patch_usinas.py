import os
import re

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"
usinas_file = os.path.join(base_dir, "src/main/resources/static/usinas.html")

with open(usinas_file, "r", encoding="utf-8") as f:
    html = f.read()

# Add financial loss row to the usinas cards
new_stat = """
              <div class="u-stat-row">
                <span>Energia Cortada:</span>
                <strong class="val-crit">${(u.energiaCortada/1000).toLocaleString('pt-BR', {minimumFractionDigits:2, maximumFractionDigits:2})} GWh</strong>
              </div>
              <div class="u-stat-row">
                <span>Receita Perdida:</span>
                <strong style="color:var(--high)">R$ ${(u.prejuizoFinanceiro/1000000).toLocaleString('pt-BR', {minimumFractionDigits:2, maximumFractionDigits:2})} milhões</strong>
              </div>
"""

html = re.sub(
    r'<div class="u-stat-row">\s*<span>Energia Cortada \(2026\):</span>\s*<strong class="val-crit">\$\{\(u\.energiaCortada/1000\)\.toLocaleString\(\'pt-BR\', \{minimumFractionDigits:2, maximumFractionDigits:2\}\)\} GWh</strong>\s*</div>',
    new_stat.replace('\\', '\\\\'),
    html,
    flags=re.DOTALL
)

with open(usinas_file, "w", encoding="utf-8") as f:
    f.write(html)
print("usinas.html patched!")

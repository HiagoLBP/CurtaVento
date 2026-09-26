import os
import re

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"
index_file = os.path.join(base_dir, "src/main/resources/static/index.html")

with open(index_file, "r", encoding="utf-8") as f:
    html = f.read()

# Update fetch logic
html = html.replace(
    "`${API_BASE}/api/dashboard/resumo-geral?ano=${ano}`",
    "`${API_BASE}/api/dashboard/resumo-geral?ano=${ano}&limite=10`"
)

# Update the table title and add a "Ver todas" button
header_html = """
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0;">Top 10 Usinas Críticas (Maior Prejuízo)</h3>
          <a href="usinas.html" style="background: var(--brand); color: white; padding: 6px 12px; border-radius: 6px; text-decoration: none; font-size: 13px; font-weight: 500;">Ver todas as Usinas</a>
        </div>
        <div class="table-container">
"""

html = re.sub(
    r'<h3 style="margin-top: 0; margin-bottom: 16px;">Monitoramento de Usinas</h3>\s*<div class="table-container">',
    header_html.replace('\\', '\\\\'),
    html
)

with open(index_file, "w", encoding="utf-8") as f:
    f.write(html)
print("index.html optimized!")

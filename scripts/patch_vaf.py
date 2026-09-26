import os
import re

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"
vaf_file = os.path.join(base_dir, "src/main/resources/static/vaf.html")

with open(vaf_file, "r", encoding="utf-8") as f:
    html = f.read()

# Replace hardcoded '/api/impacto-vaf' with the dynamic one if a selector is added, 
# or just add the selector if missing.
selector_html = """
    <div class="header-left">
      <h1>Impacto VAF</h1>
      <span>Perda de arrecadação dos municípios</span>
    </div>
    <div class="header-controls" style="display:flex; gap:16px;">
        <select id="ano-selector" class="year-select" onchange="init()" style="padding:6px 12px; border-radius:6px; border:1px solid var(--border);">
          <option value="2026">2026 ▼</option>
          <option value="2023">2023 ▼</option>
        </select>
    </div>
"""

html = re.sub(
    r'<div class="header-left">\s*<h1>Impacto VAF</h1>\s*<span>Perda de arrecadação dos municípios</span>\s*</div>',
    selector_html,
    html
)

html = html.replace(
    "const data = await fetchWithAuth('/api/impacto-vaf');",
    "const ano = document.getElementById('ano-selector') ? document.getElementById('ano-selector').value : 2026;\n    const data = await fetchWithAuth('/api/impacto-vaf?ano=' + ano);"
)

with open(vaf_file, "w", encoding="utf-8") as f:
    f.write(html)
print("vaf.html patched!")

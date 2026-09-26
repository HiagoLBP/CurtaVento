import os

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"
index_file = os.path.join(base_dir, "src/main/resources/static/index.html")

with open(index_file, "r", encoding="utf-8") as f:
    html = f.read()

# Update table headers
html = html.replace(
    '<th class="sortable">Perda no Ano (GWh)</th>',
    '<th class="sortable">Perda no Ano (GWh)</th>\n              <th class="sortable">Prejuízo Financeiro</th>\n              <th class="sortable">Status VAF</th>'
)

# Update the renderUsinas function
new_render_logic = """
    lista.forEach(u => {
      // Contagem para Situação Operacional
      if (u.risco === 'BAIXO') contBaixo++;
      else if (u.risco === 'MEDIO') contMedio++;
      else if (u.risco === 'ALTO' || u.risco === 'CRITICO') contAlto++;

      const tr = document.createElement('tr');
      const perdaGwh = (u.energiaCortada / 1000).toLocaleString('pt-BR', {minimumFractionDigits:1, maximumFractionDigits:1});
      const riscoClass = u.risco ? u.risco.toLowerCase() : 'baixo';
      const riscoLabel = RISK_LABEL[u.risco] || 'Baixo';
      
      const pR$ = (u.prejuizoFinanceiro || 0);
      const preFormatado = pR$ > 1000000 ? 
          'R$ ' + (pR$/1000000).toLocaleString('pt-BR', {minimumFractionDigits:1, maximumFractionDigits:1}) + ' mi' : 
          'R$ ' + (pR$/1000).toLocaleString('pt-BR', {minimumFractionDigits:1, maximumFractionDigits:1}) + ' mil';
          
      const statusVaf = pR$ > 1000000 ? '<span style="color:var(--crit); font-weight:bold;">Crítico</span>' : 
                        pR$ > 100000 ? '<span style="color:var(--high); font-weight:bold;">Atenção</span>' : 
                        '<span style="color:var(--ok);">Normal</span>';

      tr.innerHTML = `
        <td>
          <div style="font-weight: 500;">${u.nome}</div>
          <div style="font-size: 12px; color: var(--text-muted);">${u.operador}</div>
        </td>
        <td style="font-variant-numeric: tabular-nums;">${u.vento}</td>
        <td style="font-weight: 500;">${u.vento24h}</td>
        <td style="color: var(--text-muted);">${u.vento48h}</td>
        <td>
          <span class="risk-badge risk-${riscoClass}"></span> ${riscoLabel}
        </td>
        <td style="color: var(--high); font-weight: 500;">${perdaGwh} GWh</td>
        <td style="font-variant-numeric: tabular-nums; font-weight: 500;">${preFormatado}</td>
        <td>${statusVaf}</td>
        <td><a href="#" style="color: var(--primary); text-decoration: none; font-size: 13px; font-weight: 500;">Ver detalhes</a></td>
      `;
      tbody.appendChild(tr);
    });
"""

import re
html = re.sub(
    r"lista\.forEach\(u => \{.*?tbody\.appendChild\(tr\);\s*\}\);",
    new_render_logic.replace('\\', '\\\\'),
    html,
    flags=re.DOTALL
)

with open(index_file, "w", encoding="utf-8") as f:
    f.write(html)
print("index.html patched!")

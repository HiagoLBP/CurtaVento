import os

base_dir = r"c:\Users\Hiago\Desktop\curta-vento"
usinas_file = os.path.join(base_dir, "src/main/resources/static/usinas.html")

with open(usinas_file, "r", encoding="utf-8") as f:
    html = f.read()

# Add pagination controls and search bar
header_replace = """
    <div class="header-left">
      <h1>Gestão de Usinas</h1>
      <span>Cadastro e Status dos Ativos</span>
    </div>
    <div class="header-controls" style="display:flex; gap:16px;">
        <input type="text" id="search-usina" placeholder="Buscar usina..." onkeyup="filtrarPaginar()" style="padding:8px 12px; border:1px solid var(--border); border-radius:6px; font-family:var(--font);">
        <select id="ano-selector" class="year-select" onchange="init()" style="padding:8px 12px; border-radius:6px; border:1px solid var(--border);">
          <option value="2026">2026 ▼</option>
          <option value="2023">2023 ▼</option>
        </select>
    </div>
"""

html = html.replace(
    '<div class="header-left">\n      <h1>Gestǜo de Usinas</h1>\n      <span>Cadastro e Status dos Ativos</span>\n    </div>',
    header_replace
)
# Fallback for encoding issues
html = html.replace(
    '<div class="header-left">\n      <h1>Gestão de Usinas</h1>\n      <span>Cadastro e Status dos Ativos</span>\n    </div>',
    header_replace
)

pagination_html = """
    <div class="usinas-grid" id="usinas-grid">
      <!-- Injetado via JS -->
    </div>
    <div id="pagination-controls" style="display:flex; justify-content:center; align-items:center; gap:16px; margin-top:32px;">
      <button onclick="mudarPagina(-1)" id="btn-prev" style="padding:8px 16px; background:var(--bg-surface); border:1px solid var(--border); border-radius:6px; cursor:pointer;">Anterior</button>
      <span id="page-info" style="font-size:14px; font-weight:500;">Página 1</span>
      <button onclick="mudarPagina(1)" id="btn-next" style="padding:8px 16px; background:var(--brand); color:white; border:none; border-radius:6px; cursor:pointer;">Próxima</button>
    </div>
"""

html = html.replace(
    '<div class="usinas-grid" id="usinas-grid">\n      <!-- Injetado via JS -->\n    </div>',
    pagination_html
)

# Replace the init JS script to handle pagination
js_replace = """
  let todasUsinas = [];
  let paginaAtual = 1;
  const ITENS_POR_PAGINA = 12;

  function renderGrid(lista) {
    const grid = document.getElementById('usinas-grid');
    grid.innerHTML = '';
    
    lista.forEach(u => {
        const omHtml = u.risco === 'ALTO' || u.risco === 'CRITICO' 
            ? '<strong style="color:var(--warn)">ALERTA ⚠️</strong>' 
            : '<strong style="color:var(--ok)">LIBERADO ✅</strong>';
            
        const color = RISK_COLOR[u.risco] || 'var(--text-muted)';
        
        let dataStr = 'N/I';
        if(u.dataInicio) {
          const parts = u.dataInicio.split('-');
          if(parts.length === 3) dataStr = `${parts[2]}/${parts[1]}/${parts[0]}`;
        }
        
        const perdaFinanceira = u.prejuizoFinanceiro || 0;
        const perdaText = perdaFinanceira > 1000000 
            ? `R$ ${(perdaFinanceira/1000000).toLocaleString('pt-BR', {minimumFractionDigits:2, maximumFractionDigits:2})} milhões`
            : `R$ ${(perdaFinanceira/1000).toLocaleString('pt-BR', {minimumFractionDigits:2, maximumFractionDigits:2})} mil`;

        grid.innerHTML += `
          <div class="usina-card">
            <div class="u-header">
              <h3>${u.nome}</h3>
              <p>${u.operador || 'N/I'}</p>
            </div>
            
            <div class="u-details">
              <div class="u-row"><span>Capacidade:</span> <strong>${u.capacidade} MW</strong></div>
              <div class="u-row"><span>Município:</span> <strong>${u.municipio}</strong></div>
              <div class="u-row"><span>Início de Operação:</span> <strong>${dataStr}</strong></div>
            </div>
            
            <div class="u-stats">
              <div class="u-stat-row">
                <span>Risco Atual:</span>
                <strong style="color:${color}"><span class="status-dot" style="background:${color}"></span> ${u.risco}</strong>
              </div>
              <div class="u-stat-row">
                <span>Janela O&M:</span>
                ${omHtml}
              </div>
              <div class="u-stat-row">
                <span>Energia Cortada:</span>
                <strong class="val-crit">${((u.energiaCortada || 0)/1000).toLocaleString('pt-BR', {minimumFractionDigits:2, maximumFractionDigits:2})} GWh</strong>
              </div>
              <div class="u-stat-row">
                <span>Receita Perdida:</span>
                <strong style="color:var(--high)">${perdaText}</strong>
              </div>
            </div>
          </div>
        `;
    });
  }

  function filtrarPaginar() {
      const search = document.getElementById('search-usina').value.toLowerCase();
      const filtradas = todasUsinas.filter(u => u.nome.toLowerCase().includes(search) || (u.operador && u.operador.toLowerCase().includes(search)));
      
      const totalPaginas = Math.ceil(filtradas.length / ITENS_POR_PAGINA) || 1;
      if (paginaAtual > totalPaginas) paginaAtual = totalPaginas;
      
      const start = (paginaAtual - 1) * ITENS_POR_PAGINA;
      const end = start + ITENS_POR_PAGINA;
      const paginadas = filtradas.slice(start, end);
      
      renderGrid(paginadas);
      
      document.getElementById('page-info').textContent = `Página ${paginaAtual} de ${totalPaginas} (${filtradas.length} resultados)`;
      document.getElementById('btn-prev').disabled = paginaAtual === 1;
      document.getElementById('btn-next').disabled = paginaAtual === totalPaginas;
      
      document.getElementById('btn-prev').style.opacity = paginaAtual === 1 ? '0.5' : '1';
      document.getElementById('btn-next').style.opacity = paginaAtual === totalPaginas ? '0.5' : '1';
  }

  function mudarPagina(dir) {
      paginaAtual += dir;
      filtrarPaginar();
      window.scrollTo(0,0);
  }

  async function init() {
    const ano = document.getElementById('ano-selector').value || 2026;
    const grid = document.getElementById('usinas-grid');
    grid.innerHTML = '<p>Carregando dados das usinas...</p>';
    
    const data = await fetchWithAuth(`/api/dashboard/resumo-geral?ano=${ano}`);
    if (data && data.usinas) {
      todasUsinas = data.usinas;
      paginaAtual = 1;
      filtrarPaginar();
    }
  }
"""

import re
html = re.sub(
    r"async function init\(\).*?init\(\);",
    js_replace.replace('\\', '\\\\') + "\n  init();",
    html,
    flags=re.DOTALL
)

with open(usinas_file, "w", encoding="utf-8") as f:
    f.write(html)
print("usinas.html optimized!")

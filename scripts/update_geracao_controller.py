import os

filepath = r"C:\Users\Hiago\.copilot\repos\copilot-worktrees\curta-vento\hiagolbp-fantastic-doodle\src\main\java\com\curtavento\curta_vento\geracao\GeracaoOnsHorariaController.java"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

new_endpoint = """
    @PostMapping
    public ResponseEntity<Map<String, Object>> importarAutomatico(
            @RequestParam("ano") int ano, 
            @RequestParam("mes") int mes) throws Exception {
        GeracaoOnsHorariaService.ImportResult resultado = service.importarViaHttp(ano, mes);
        return ResponseEntity.ok(Map.of(
                "linhasDaBahia", resultado.lidas(),
                "linhasImportadas", resultado.importadas(),
                "linhasRejeitadas", resultado.rejeitadas(),
                "mensagem", "Importação automática finalizada com sucesso para " + mes + "/" + ano));
    }
}
"""

content = content.replace("}\n", new_endpoint)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

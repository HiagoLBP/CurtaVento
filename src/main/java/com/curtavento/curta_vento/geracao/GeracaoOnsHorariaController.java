package com.curtavento.curta_vento.geracao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/ons/geracao-horaria")
public class GeracaoOnsHorariaController {

    private final GeracaoOnsHorariaService service;

    public GeracaoOnsHorariaController(GeracaoOnsHorariaService service) {
        this.service = service;
    }

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importar(@RequestParam("arquivo") MultipartFile arquivo)
            throws Exception {
        GeracaoOnsHorariaService.ImportResult resultado = service.importar(arquivo);
        return ResponseEntity.ok(Map.of(
                "linhasLidas", resultado.lidas(),
                "linhasImportadas", resultado.importadas(),
                "linhasRejeitadas", resultado.rejeitadas(),
                "fonte", "ONS geracao horaria por usina"));
    }

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

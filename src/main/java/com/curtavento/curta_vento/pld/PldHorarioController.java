package com.curtavento.curta_vento.pld;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/pld")
public class PldHorarioController {

    private final PldHorarioService service;

    public PldHorarioController(PldHorarioService service) {
        this.service = service;
    }

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importar(
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            org.springframework.web.multipart.MultipartHttpServletRequest request) throws Exception {
        
        MultipartFile fileToImport = arquivo;
        if (fileToImport == null || fileToImport.isEmpty()) {
            var files = request.getFileMap();
            if (!files.isEmpty()) {
                fileToImport = files.values().iterator().next();
            }
        }
        if (fileToImport == null || fileToImport.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado. Certifique-se de preencher a Key como 'arquivo' no Postman.");
        }

        PldHorarioService.ImportResult resultado = service.importar(fileToImport);
        return ResponseEntity.ok(Map.of(
                "linhasLidas", resultado.lidas(),
                "linhasImportadas", resultado.importadas(),
                "linhasRejeitadas", resultado.rejeitadas(),
                "fonte", "CCEE PLD_HORARIO",
                "submercadoEsperado", "NORDESTE"));
    }
}

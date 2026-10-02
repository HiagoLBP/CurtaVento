package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.ImportarPldHorarioUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/pld")
public class PldHorarioController {

    private final ImportarPldHorarioUseCase importarPldHorarioUseCase;

    public PldHorarioController(ImportarPldHorarioUseCase importarPldHorarioUseCase) {
        this.importarPldHorarioUseCase = importarPldHorarioUseCase;
    }

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importar(
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            MultipartHttpServletRequest request) throws Exception {
        
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

        ImportarPldHorarioUseCase.ImportResult resultado = importarPldHorarioUseCase.importar(fileToImport);
        return ResponseEntity.ok(Map.of(
                "linhasLidas", resultado.lidas(),
                "linhasImportadas", resultado.importadas(),
                "linhasRejeitadas", resultado.rejeitadas(),
                "fonte", "CCEE PLD_HORARIO",
                "submercadoEsperado", "NORDESTE"));
    }
}

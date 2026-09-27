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
    public ResponseEntity<Map<String, Object>> importar(@RequestParam("arquivo") MultipartFile arquivo) throws Exception {
        PldHorarioService.ImportResult resultado = service.importar(arquivo);
        return ResponseEntity.ok(Map.of(
                "linhasLidas", resultado.lidas(),
                "linhasImportadas", resultado.importadas(),
                "linhasRejeitadas", resultado.rejeitadas(),
                "fonte", "CCEE PLD_HORARIO",
                "submercadoEsperado", "NORDESTE"));
    }
}

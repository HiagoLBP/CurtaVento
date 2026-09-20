package com.curtavento.curta_vento.clima;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeituraClimaticaController {

    private final LeituraClimaticaService service;

    public LeituraClimaticaController(LeituraClimaticaService service) {
        this.service = service;
    }

    @GetMapping("/usinas/{id}/clima")
    public List<LeituraClimatica> buscarHistorico(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        return service.buscarHistorico(id, inicio, fim);
    }

    @PostMapping("/leituras-climaticas")
    public LeituraClimatica cadastrar(@RequestBody LeituraClimatica leitura) {
        return service.salvar(leitura);
    }
}
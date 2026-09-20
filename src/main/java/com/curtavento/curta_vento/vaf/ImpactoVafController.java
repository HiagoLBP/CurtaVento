package com.curtavento.curta_vento.vaf;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vaf")
public class ImpactoVafController {

    private final ImpactoVafService service;

    public ImpactoVafController(ImpactoVafService service) {
        this.service = service;
    }

    @PostMapping("/calcular/{municipioId}")
    public ImpactoVaf calcular(@PathVariable Long municipioId, @RequestParam int ano) {
        return service.calcularImpacto(municipioId, ano);
    }
}
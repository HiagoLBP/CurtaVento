package com.curtavento.curta_vento.corte;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cortes")
public class HistoricoCorteOnsController {

    private final HistoricoCorteOnsService service;

    public HistoricoCorteOnsController(HistoricoCorteOnsService service) {
        this.service = service;
    }

    @PostMapping
    public HistoricoCorteOns registrar(@RequestBody HistoricoCorteOns corte) {
        return service.registrarCorte(corte);
    }

    @GetMapping("/usina/{usinaId}")
    public List<HistoricoCorteOns> listarPorUsina(@PathVariable Long usinaId) {
        return service.buscarPorUsina(usinaId);
    }
}
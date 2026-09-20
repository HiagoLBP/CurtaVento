package com.curtavento.curta_vento.usina;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usinas")
public class UsinaController {

    private final UsinaService service;

    public UsinaController(UsinaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usina> listar() {
        return service.listarTodas();
    }

    @PostMapping
    public Usina cadastrar(@RequestBody Usina usina) {
        return service.salvar(usina);
    }
}
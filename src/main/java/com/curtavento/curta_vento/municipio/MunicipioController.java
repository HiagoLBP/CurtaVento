package com.curtavento.curta_vento.municipio;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/municipios")
public class MunicipioController {

    private final MunicipioService service;

    public MunicipioController(MunicipioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Municipio> listar() {
        return service.listarTodos();
    }

    @PostMapping
    public Municipio cadastrar(@RequestBody Municipio municipio) {
        return service.salvar(municipio);
    }
}
package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.domain.model.Municipio;
import com.curtavento.curta_vento.application.usecase.ListarMunicipiosUseCase;
import com.curtavento.curta_vento.application.usecase.SalvarMunicipioUseCase;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/municipios")
public class MunicipioController {

    private final ListarMunicipiosUseCase listarMunicipiosUseCase;
    private final SalvarMunicipioUseCase salvarMunicipioUseCase;

    public MunicipioController(ListarMunicipiosUseCase listarMunicipiosUseCase, SalvarMunicipioUseCase salvarMunicipioUseCase) {
        this.listarMunicipiosUseCase = listarMunicipiosUseCase;
        this.salvarMunicipioUseCase = salvarMunicipioUseCase;
    }

    @GetMapping
    public List<Municipio> listar() {
        return listarMunicipiosUseCase.executar();
    }

    @PostMapping
    public Municipio cadastrar(@RequestBody Municipio municipio) {
        return salvarMunicipioUseCase.executar(municipio);
    }
}

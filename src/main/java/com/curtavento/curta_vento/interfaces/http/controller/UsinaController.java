package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.domain.model.Usina;
import com.curtavento.curta_vento.application.usecase.ListarUsinasUseCase;
import com.curtavento.curta_vento.application.usecase.SalvarUsinaUseCase;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usinas")
public class UsinaController {

    private final ListarUsinasUseCase listarUsinasUseCase;
    private final SalvarUsinaUseCase salvarUsinaUseCase;

    public UsinaController(ListarUsinasUseCase listarUsinasUseCase, SalvarUsinaUseCase salvarUsinaUseCase) {
        this.listarUsinasUseCase = listarUsinasUseCase;
        this.salvarUsinaUseCase = salvarUsinaUseCase;
    }

    @GetMapping
    public List<Usina> listar() {
        return listarUsinasUseCase.executar();
    }

    @PostMapping
    public Usina cadastrar(@RequestBody Usina usina) {
        return salvarUsinaUseCase.executar(usina);
    }
}

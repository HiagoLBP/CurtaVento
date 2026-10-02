package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.Usina;
import com.curtavento.curta_vento.domain.port.out.UsinaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarUsinasUseCase {
    private final UsinaRepositoryPort repositoryPort;

    public ListarUsinasUseCase(UsinaRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public List<Usina> executar() {
        return repositoryPort.findAll();
    }
}

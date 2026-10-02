package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.Municipio;
import com.curtavento.curta_vento.domain.port.out.MunicipioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarMunicipiosUseCase {
    private final MunicipioRepositoryPort repositoryPort;

    public ListarMunicipiosUseCase(MunicipioRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public List<Municipio> executar() {
        return repositoryPort.findAll();
    }
}

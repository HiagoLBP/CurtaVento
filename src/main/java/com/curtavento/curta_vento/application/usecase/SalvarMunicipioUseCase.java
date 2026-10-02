package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.Municipio;
import com.curtavento.curta_vento.domain.port.out.MunicipioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalvarMunicipioUseCase {
    private final MunicipioRepositoryPort repositoryPort;

    public SalvarMunicipioUseCase(MunicipioRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Transactional
    public Municipio executar(Municipio municipio) {
        return repositoryPort.save(municipio);
    }
}

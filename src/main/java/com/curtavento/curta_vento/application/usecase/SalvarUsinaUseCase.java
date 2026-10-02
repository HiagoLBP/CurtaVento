package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.Usina;
import com.curtavento.curta_vento.domain.port.out.UsinaRepositoryPort;
import com.curtavento.curta_vento.domain.port.out.MunicipioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalvarUsinaUseCase {
    private final UsinaRepositoryPort usinaRepositoryPort;
    private final MunicipioRepositoryPort municipioRepositoryPort;

    public SalvarUsinaUseCase(UsinaRepositoryPort usinaRepositoryPort, MunicipioRepositoryPort municipioRepositoryPort) {
        this.usinaRepositoryPort = usinaRepositoryPort;
        this.municipioRepositoryPort = municipioRepositoryPort;
    }

    @Transactional
    public Usina executar(Usina usina) {
        if (usina.getMunicipio() != null && usina.getMunicipio().getId() != null) {
            var municipioExistente = municipioRepositoryPort.findById(usina.getMunicipio().getId())
                    .orElseThrow(() -> new RuntimeException("Municipio nao encontrado com o ID: " + usina.getMunicipio().getId()));
            usina.setMunicipio(municipioExistente);
        } else {
            throw new RuntimeException("E obrigatorio informar o municipio da usina.");
        }
        return usinaRepositoryPort.save(usina);
    }
}

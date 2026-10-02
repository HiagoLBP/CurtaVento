package com.curtavento.curta_vento.domain.port.out;

import com.curtavento.curta_vento.domain.model.Municipio;
import java.util.List;
import java.util.Optional;

public interface MunicipioRepositoryPort {
    List<Municipio> findAll();
    Optional<Municipio> findById(Long id);
    Municipio save(Municipio municipio);
}

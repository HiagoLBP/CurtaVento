package com.curtavento.curta_vento.domain.port.out;

import com.curtavento.curta_vento.domain.model.Usina;
import java.util.List;
import java.util.Optional;

public interface UsinaRepositoryPort {
    List<Usina> findAll();
    Optional<Usina> findById(Long id);
    Usina save(Usina usina);
}

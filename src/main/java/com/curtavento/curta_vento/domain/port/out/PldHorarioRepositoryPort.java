package com.curtavento.curta_vento.domain.port.out;

import com.curtavento.curta_vento.domain.model.PldHorario;
import java.time.LocalDate;
import java.util.Optional;

public interface PldHorarioRepositoryPort {
    Optional<PldHorario> findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(LocalDate dia, Integer hora, String submercado, Integer periodoComercializacao);
    PldHorario save(PldHorario pldHorario);
}

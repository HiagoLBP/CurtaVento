package com.curtavento.curta_vento.domain.port.out;

import com.curtavento.curta_vento.domain.model.LeituraClimatica;
import java.time.LocalDateTime;
import java.util.List;

public interface LeituraClimaticaRepositoryPort {
    List<LeituraClimatica> findUltimasLeiturasGeral();
    List<LeituraClimatica> findByUsinaIdAndDataHoraBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);
    LeituraClimatica findFirstByUsinaIdOrderByDataHoraDesc(Long usinaId);
    LeituraClimatica save(LeituraClimatica leitura);
}
package com.curtavento.curta_vento.clima;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraClimaticaRepository extends JpaRepository<LeituraClimatica, Long> {
    List<LeituraClimatica> findByUsinaIdAndDataHoraBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);
    LeituraClimatica findFirstByUsinaIdOrderByDataHoraDesc(Long usinaId);
}
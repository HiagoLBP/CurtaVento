package com.curtavento.curta_vento.geracao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GeracaoOnsHorariaRepository extends JpaRepository<GeracaoOnsHoraria, Long> {
    Optional<GeracaoOnsHoraria> findByIdOnsAndInstanteAndFonte(
            String idOns, LocalDateTime instante, String fonte);
}

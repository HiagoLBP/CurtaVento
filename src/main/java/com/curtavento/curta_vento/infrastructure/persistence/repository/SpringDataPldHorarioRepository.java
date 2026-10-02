package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.PldHorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataPldHorarioRepository extends JpaRepository<PldHorarioEntity, Long> {
    Optional<PldHorarioEntity> findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(LocalDate dia, Integer hora, String submercado, Integer periodoComercializacao);
}

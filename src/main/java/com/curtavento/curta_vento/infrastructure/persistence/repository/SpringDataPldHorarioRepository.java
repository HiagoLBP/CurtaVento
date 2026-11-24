package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.PldHorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataPldHorarioRepository extends JpaRepository<PldHorarioEntity, Long> {
    Optional<PldHorarioEntity> findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(
            LocalDate dia, Integer hora, String submercado, Integer periodoComercializacao);

    List<PldHorarioEntity> findByDiaBetweenAndSubmercado(LocalDate inicio, LocalDate fim, String submercado);

    @Query("SELECT AVG(p.pldHora) FROM PldHorarioEntity p WHERE p.dia >= :inicio AND p.dia <= :fim AND UPPER(p.submercado) = UPPER(:submercado)")
    java.math.BigDecimal findMediaPldPeriodo(@Param("inicio") LocalDate inicio,
                                             @Param("fim") LocalDate fim,
                                             @Param("submercado") String submercado);
}

package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsBlocoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataHistoricoCorteOnsBlocoRepository extends JpaRepository<HistoricoCorteOnsBlocoEntity, Long> {

    Optional<HistoricoCorteOnsBlocoEntity> findByUsinaIdAndInstante(Long usinaId, LocalDateTime instante);

    List<HistoricoCorteOnsBlocoEntity> findByUsinaIdAndInstanteBetweenOrderByInstanteAsc(
            Long usinaId, LocalDateTime inicio, LocalDateTime fim);

    List<HistoricoCorteOnsBlocoEntity> findByInstanteBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COALESCE(b.codRazaoRestricao, b.motivoOns, 'NAO_CLASSIFICADO'), SUM(b.energiaCortadaMwh) " +
            "FROM HistoricoCorteOnsBlocoEntity b WHERE b.instante >= :inicio AND b.instante <= :fim " +
            "GROUP BY COALESCE(b.codRazaoRestricao, b.motivoOns, 'NAO_CLASSIFICADO')")
    List<Object[]> findResumoPorMotivo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT b.usina.id, b.usina.nome, COALESCE(b.codRazaoRestricao, b.motivoOns, 'NAO_CLASSIFICADO'), " +
            "SUM(b.energiaCortadaMwh), COUNT(b), " +
            "SUM(b.energiaCortadaMwh * COALESCE(b.pldMwh, b.usina.precoPpaMwh)) " +
            "FROM HistoricoCorteOnsBlocoEntity b " +
            "WHERE b.instante >= :inicio AND b.instante <= :fim " +
            "AND (:usinaId IS NULL OR b.usina.id = :usinaId) " +
            "GROUP BY b.usina.id, b.usina.nome, COALESCE(b.codRazaoRestricao, b.motivoOns, 'NAO_CLASSIFICADO')")
    List<Object[]> findAgregadoRegulatorio(@Param("inicio") LocalDateTime inicio,
                                           @Param("fim") LocalDateTime fim,
                                           @Param("usinaId") Long usinaId);

    @Query("SELECT SUM(b.energiaCortadaMwh), SUM(b.geracaoEstimadaMw * 0.5), SUM(b.geracaoVerificadaMw * 0.5) " +
            "FROM HistoricoCorteOnsBlocoEntity b WHERE b.usina.id = :usinaId AND b.instante >= :inicio AND b.instante <= :fim")
    List<Object[]> findTotaisEnergiaPorUsina(@Param("usinaId") Long usinaId,
                                             @Param("inicio") LocalDateTime inicio,
                                             @Param("fim") LocalDateTime fim);

    @Query("SELECT b.usina.id, SUM(b.energiaCortadaMwh), SUM(b.geracaoEstimadaMw * 0.5) " +
            "FROM HistoricoCorteOnsBlocoEntity b WHERE b.instante >= :inicio AND b.instante <= :fim " +
            "GROUP BY b.usina.id")
    List<Object[]> findDesperdicioPorUsina(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT SUM(b.energiaCortadaMwh) FROM HistoricoCorteOnsBlocoEntity b WHERE b.instante >= :inicio AND b.instante <= :fim")
    BigDecimal sumEnergiaCortadaPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}

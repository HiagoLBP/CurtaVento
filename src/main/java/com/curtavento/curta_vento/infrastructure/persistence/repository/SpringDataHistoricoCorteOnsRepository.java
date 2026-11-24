package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataHistoricoCorteOnsRepository extends JpaRepository<HistoricoCorteOnsEntity, Long> {

    List<HistoricoCorteOnsEntity> findByUsinaId(Long usinaId);

    List<HistoricoCorteOnsEntity> findByUsinaIdAndDataCorteBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);

    List<HistoricoCorteOnsEntity> findByDataCorteBetween(LocalDateTime inicio, LocalDateTime fim);

    Optional<HistoricoCorteOnsEntity> findByUsinaIdAndDataCorte(Long usinaId, LocalDateTime dataCorte);

    @Query("SELECT c.usina.nome, SUM(c.energiaCortadaMwh) FROM HistoricoCorteOnsEntity c WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim GROUP BY c.usina.nome ORDER BY SUM(c.energiaCortadaMwh) DESC")
    List<Object[]> findRankingCortesAno(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT MONTH(c.dataCorte), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOnsEntity c WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim GROUP BY MONTH(c.dataCorte) ORDER BY MONTH(c.dataCorte)")
    List<Object[]> findEvolucaoMensalCortes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = "SELECT * FROM historico_corte_ons ORDER BY data_corte DESC LIMIT 100", nativeQuery = true)
    List<HistoricoCorteOnsEntity> findUltimosCortes();

    @Query("SELECT c.usina.municipio.nome, COUNT(DISTINCT c.usina.id), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOnsEntity c WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim GROUP BY c.usina.municipio.nome ORDER BY SUM(c.energiaCortadaMwh) DESC")
    List<Object[]> findImpactoPorMunicipio(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COALESCE(c.codRazaoRestricao, c.motivoOns, 'NAO_CLASSIFICADO'), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOnsEntity c WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim GROUP BY COALESCE(c.codRazaoRestricao, c.motivoOns, 'NAO_CLASSIFICADO')")
    List<Object[]> findMotivosCorte(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT SUM(c.energiaCortadaMwh) FROM HistoricoCorteOnsEntity c WHERE c.usina.municipio.id = :municipioId AND c.dataCorte >= :inicio AND c.dataCorte <= :fim")
    BigDecimal sumEnergiaCortadaPorMunicipioEAno(@Param("municipioId") Long municipioId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT SUM(c.energiaCortadaMwh), SUM(c.energiaEstimadaMwh), SUM(c.energiaVerificadaMwh), SUM(c.valoradoPldRs) " +
            "FROM HistoricoCorteOnsEntity c WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim")
    List<Object[]> findTotaisConsolidadosPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT c.usina.id, SUM(c.energiaCortadaMwh), SUM(c.energiaEstimadaMwh) " +
            "FROM HistoricoCorteOnsEntity c WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim GROUP BY c.usina.id")
    List<Object[]> findDesperdicioPorUsina(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT SUM(c.energiaCortadaMwh), SUM(c.energiaEstimadaMwh), SUM(c.energiaVerificadaMwh), SUM(c.valoradoPldRs) " +
            "FROM HistoricoCorteOnsEntity c WHERE c.usina.id = :usinaId AND c.dataCorte >= :inicio AND c.dataCorte <= :fim")
    List<Object[]> findTotaisPorUsinaPeriodo(@Param("usinaId") Long usinaId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT c.usina.id, c.usina.nome, COALESCE(c.codRazaoRestricao, c.motivoOns, 'NAO_CLASSIFICADO'), " +
            "SUM(c.energiaCortadaMwh), SUM(c.duracaoMinutos) / 30, " +
            "SUM(COALESCE(c.valoradoPldRs, c.energiaCortadaMwh * c.usina.precoPpaMwh)) " +
            "FROM HistoricoCorteOnsEntity c " +
            "WHERE c.dataCorte >= :inicio AND c.dataCorte <= :fim " +
            "AND (:usinaId IS NULL OR c.usina.id = :usinaId) " +
            "GROUP BY c.usina.id, c.usina.nome, COALESCE(c.codRazaoRestricao, c.motivoOns, 'NAO_CLASSIFICADO')")
    List<Object[]> findAgregadoRegulatorioDiario(@Param("inicio") LocalDateTime inicio,
                                                 @Param("fim") LocalDateTime fim,
                                                 @Param("usinaId") Long usinaId);
}

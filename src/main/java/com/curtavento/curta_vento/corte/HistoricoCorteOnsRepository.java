package com.curtavento.curta_vento.corte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricoCorteOnsRepository extends JpaRepository<HistoricoCorteOns, Long> {

    List<HistoricoCorteOns> findByUsinaId(Long usinaId);

    List<HistoricoCorteOns> findByUsinaIdAndDataCorteBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);

    java.util.Optional<HistoricoCorteOns> findByUsinaIdAndDataCorte(Long usinaId, LocalDateTime dataCorte);

    @org.springframework.data.jpa.repository.Query("SELECT c.usina.nome, SUM(c.energiaCortadaMwh) FROM HistoricoCorteOns c WHERE YEAR(c.dataCorte) = :ano GROUP BY c.usina.nome ORDER BY SUM(c.energiaCortadaMwh) DESC")
    List<Object[]> findRankingCortesAno(@org.springframework.data.repository.query.Param("ano") int ano);

    @org.springframework.data.jpa.repository.Query("SELECT MONTH(c.dataCorte), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOns c WHERE YEAR(c.dataCorte) = :ano GROUP BY MONTH(c.dataCorte) ORDER BY MONTH(c.dataCorte)")
    List<Object[]> findEvolucaoMensalCortes(@org.springframework.data.repository.query.Param("ano") int ano);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM historico_corte_ons ORDER BY data_corte DESC LIMIT 100", nativeQuery = true)
    List<HistoricoCorteOns> findUltimosCortes();

    @org.springframework.data.jpa.repository.Query("SELECT c.usina.municipio.nome, COUNT(DISTINCT c.usina.id), SUM(c.energiaCortadaMwh) FROM HistoricoCorteOns c WHERE YEAR(c.dataCorte) = :ano GROUP BY c.usina.municipio.nome ORDER BY SUM(c.energiaCortadaMwh) DESC")
    List<Object[]> findImpactoPorMunicipio(@org.springframework.data.repository.query.Param("ano") int ano);
}
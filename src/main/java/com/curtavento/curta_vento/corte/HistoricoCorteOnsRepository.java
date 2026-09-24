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
}
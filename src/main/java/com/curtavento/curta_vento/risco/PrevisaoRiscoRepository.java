package com.curtavento.curta_vento.risco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrevisaoRiscoRepository extends JpaRepository<PrevisaoRisco, Long> {

    List<PrevisaoRisco> findByUsinaId(Long usinaId);
    PrevisaoRisco findFirstByUsinaIdAndDataReferencia(Long usinaId, java.time.LocalDate dataReferencia);
    PrevisaoRisco findFirstByUsinaIdOrderByDataReferenciaDesc(Long usinaId);
}
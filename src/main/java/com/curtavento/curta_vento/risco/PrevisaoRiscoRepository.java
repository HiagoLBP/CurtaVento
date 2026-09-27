package com.curtavento.curta_vento.risco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrevisaoRiscoRepository extends JpaRepository<PrevisaoRisco, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT p.* FROM previsao_risco p WHERE NOT EXISTS (SELECT 1 FROM previsao_risco newer WHERE newer.usina_id = p.usina_id AND (newer.data_referencia > p.data_referencia OR (newer.data_referencia = p.data_referencia AND newer.id > p.id)))", nativeQuery = true)
    java.util.List<PrevisaoRisco> findUltimosRiscosGeral();


    List<PrevisaoRisco> findByUsinaId(Long usinaId);
    PrevisaoRisco findFirstByUsinaIdAndDataReferenciaOrderByIdDesc(Long usinaId, java.time.LocalDate dataReferencia);
    PrevisaoRisco findFirstByUsinaIdOrderByDataReferenciaDesc(Long usinaId);
}
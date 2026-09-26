package com.curtavento.curta_vento.risco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrevisaoRiscoRepository extends JpaRepository<PrevisaoRisco, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT p.* FROM previsao_risco p INNER JOIN (SELECT usina_id, MAX(data_referencia) as max_data FROM previsao_risco GROUP BY usina_id) grouped ON p.usina_id = grouped.usina_id AND p.data_referencia = grouped.max_data", nativeQuery = true)
    java.util.List<PrevisaoRisco> findUltimosRiscosGeral();


    List<PrevisaoRisco> findByUsinaId(Long usinaId);
    PrevisaoRisco findFirstByUsinaIdAndDataReferencia(Long usinaId, java.time.LocalDate dataReferencia);
    PrevisaoRisco findFirstByUsinaIdOrderByDataReferenciaDesc(Long usinaId);
}
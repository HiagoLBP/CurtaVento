package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.time.LocalDate;

public interface SpringDataPrevisaoRiscoRepository extends JpaRepository<PrevisaoRiscoEntity, Long> {
    @Query(value = "SELECT p.* FROM previsao_risco p INNER JOIN (SELECT usina_id, MAX(data_referencia) as max_data FROM previsao_risco GROUP BY usina_id) grouped ON p.usina_id = grouped.usina_id AND p.data_referencia = grouped.max_data", nativeQuery = true)
    List<PrevisaoRiscoEntity> findUltimosRiscosGeral();

    List<PrevisaoRiscoEntity> findByUsinaId(Long usinaId);
    PrevisaoRiscoEntity findFirstByUsinaIdAndDataReferencia(Long usinaId, LocalDate dataReferencia);
    PrevisaoRiscoEntity findFirstByUsinaIdOrderByDataReferenciaDesc(Long usinaId);
}
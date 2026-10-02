package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataImpactoVafRepository extends JpaRepository<ImpactoVafEntity, Long> {
    List<ImpactoVafEntity> findByAnoReferencia(Integer anoReferencia);
    List<ImpactoVafEntity> findByMunicipioIdAndAnoReferencia(Long municipioId, Integer anoReferencia);
    ImpactoVafEntity findFirstByMunicipioIdAndAnoReferenciaOrderByDataCalculoDesc(Long municipioId, Integer anoReferencia);
    List<ImpactoVafEntity> findByMunicipioIdOrderByDataCalculoDesc(Long municipioId);
}
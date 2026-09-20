package com.curtavento.curta_vento.vaf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImpactoVafRepository extends JpaRepository<ImpactoVaf, Long> {

    List<ImpactoVaf> findByMunicipioIdAndAnoReferencia(Long municipioId, Integer anoReferencia);
}
package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.AlertaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataAlertaRepository extends JpaRepository<AlertaEntity, Long> {
    List<AlertaEntity> findByUsinaIdOrderByDataCriacaoDesc(Long usinaId);
    List<AlertaEntity> findByMunicipioIdOrderByDataCriacaoDesc(Long municipioId);
    List<AlertaEntity> findAllByOrderByDataCriacaoDesc();
}
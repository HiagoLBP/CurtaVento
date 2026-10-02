package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMunicipioRepository extends JpaRepository<MunicipioEntity, Long> {
}

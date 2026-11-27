package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.OnsSyncCheckpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOnsSyncCheckpointRepository extends JpaRepository<OnsSyncCheckpointEntity, String> {
}

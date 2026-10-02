package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SpringDataUsinaRepository extends JpaRepository<UsinaEntity, Long> {
    @Query("SELECT u FROM UsinaEntity u WHERE u.nome = :nome")
    Optional<UsinaEntity> findByNome(@Param("nome") String nome);
}

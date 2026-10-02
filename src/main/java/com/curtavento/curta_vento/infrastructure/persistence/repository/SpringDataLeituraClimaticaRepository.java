package com.curtavento.curta_vento.infrastructure.persistence.repository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataLeituraClimaticaRepository extends JpaRepository<LeituraClimaticaEntity, Long> {
    @Query(value = "SELECT l.* FROM leitura_climatica l INNER JOIN (SELECT usina_id, MAX(data_hora) as max_data FROM leitura_climatica GROUP BY usina_id) grouped ON l.usina_id = grouped.usina_id AND l.data_hora = grouped.max_data", nativeQuery = true)
    List<LeituraClimaticaEntity> findUltimasLeiturasGeral();

    List<LeituraClimaticaEntity> findByUsinaIdAndDataHoraBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);
    LeituraClimaticaEntity findFirstByUsinaIdOrderByDataHoraDesc(Long usinaId);
}
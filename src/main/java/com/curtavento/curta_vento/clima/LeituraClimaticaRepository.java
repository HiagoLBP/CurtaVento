package com.curtavento.curta_vento.clima;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraClimaticaRepository extends JpaRepository<LeituraClimatica, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT l.* FROM leitura_climatica l INNER JOIN (SELECT usina_id, MAX(data_hora) as max_data FROM leitura_climatica GROUP BY usina_id) grouped ON l.usina_id = grouped.usina_id AND l.data_hora = grouped.max_data", nativeQuery = true)
    java.util.List<LeituraClimatica> findUltimasLeiturasGeral();

    List<LeituraClimatica> findByUsinaIdAndDataHoraBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);
    LeituraClimatica findFirstByUsinaIdOrderByDataHoraDesc(Long usinaId);
}
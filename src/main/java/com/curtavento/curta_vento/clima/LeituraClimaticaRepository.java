package com.curtavento.curta_vento.clima;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraClimaticaRepository extends JpaRepository<LeituraClimatica, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT l.* FROM leitura_climatica l WHERE NOT EXISTS (SELECT 1 FROM leitura_climatica newer WHERE newer.usina_id = l.usina_id AND (newer.data_hora > l.data_hora OR (newer.data_hora = l.data_hora AND newer.id > l.id)))", nativeQuery = true)
    java.util.List<LeituraClimatica> findUltimasLeiturasGeral();

    List<LeituraClimatica> findByUsinaIdAndDataHoraBetween(Long usinaId, LocalDateTime inicio, LocalDateTime fim);
    LeituraClimatica findFirstByUsinaIdOrderByDataHoraDesc(Long usinaId);
}
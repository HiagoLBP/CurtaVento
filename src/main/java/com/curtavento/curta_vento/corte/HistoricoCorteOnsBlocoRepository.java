package com.curtavento.curta_vento.corte;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoCorteOnsBlocoRepository extends JpaRepository<HistoricoCorteOnsBloco, Long> {
    List<HistoricoCorteOnsBloco> findByInstanteBetween(LocalDateTime inicio, LocalDateTime fim);
}

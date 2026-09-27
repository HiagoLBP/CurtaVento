package com.curtavento.curta_vento.pld;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface PldHorarioRepository extends JpaRepository<PldHorario, Long> {
    Optional<PldHorario> findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(
            LocalDate dia, Integer hora, String submercado, Integer periodoComercializacao);
    Optional<PldHorario> findFirstByDiaAndHoraAndSubmercadoOrderByPeriodoComercializacaoAsc(
            LocalDate dia, Integer hora, String submercado);
    long countByMesReferencia(Integer mesReferencia);
}

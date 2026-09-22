package com.curtavento.curta_vento.alerta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    
    // Lista alertas de uma usina específica
    List<Alerta> findByUsinaIdOrderByDataCriacaoDesc(Long usinaId);
    
    // Lista alertas de um município específico
    List<Alerta> findByMunicipioIdOrderByDataCriacaoDesc(Long municipioId);
    
    // Lista todos (para admin)
    List<Alerta> findAllByOrderByDataCriacaoDesc();
}

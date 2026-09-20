package com.curtavento.curta_vento.usina;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsinaRepository extends JpaRepository<Usina, Long> {
}
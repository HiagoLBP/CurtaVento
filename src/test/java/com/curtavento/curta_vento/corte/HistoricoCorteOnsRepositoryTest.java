package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;

import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class HistoricoCorteOnsRepositoryTest {

    @Autowired
    private SpringDataHistoricoCorteOnsRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveAgruparRankingCortesAnoCorretamente() {
        // Arrange
        MunicipioEntity mun = new MunicipioEntity();
        mun.setNome("Caetite");
        entityManager.persist(mun);

        UsinaEntity usinaA = new UsinaEntity();
        usinaA.setNome("UsinaEntity Eolica A");
        usinaA.setMunicipio(mun);
        entityManager.persist(usinaA);

        UsinaEntity usinaB = new UsinaEntity();
        usinaB.setNome("UsinaEntity Eolica B");
        usinaB.setMunicipio(mun);
        entityManager.persist(usinaB);

        HistoricoCorteOnsEntity corte1 = new HistoricoCorteOnsEntity();
        corte1.setUsina(usinaA);
        corte1.setDataCorte(LocalDateTime.of(2026, 5, 10, 12, 0));
        corte1.setEnergiaCortadaMwh(new BigDecimal("150.0"));
        entityManager.persist(corte1);

        HistoricoCorteOnsEntity corte2 = new HistoricoCorteOnsEntity();
        corte2.setUsina(usinaA);
        corte2.setDataCorte(LocalDateTime.of(2026, 6, 12, 12, 0));
        corte2.setEnergiaCortadaMwh(new BigDecimal("50.0"));
        entityManager.persist(corte2);

        HistoricoCorteOnsEntity corte3 = new HistoricoCorteOnsEntity();
        corte3.setUsina(usinaB);
        corte3.setDataCorte(LocalDateTime.of(2026, 5, 11, 12, 0));
        corte3.setEnergiaCortadaMwh(new BigDecimal("300.0"));
        entityManager.persist(corte3);

        entityManager.flush();

        // Act
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 12, 31, 23, 59);
        List<Object[]> ranking = repository.findRankingCortesAno(inicio, fim);

        // Assert
        assertEquals(2, ranking.size());
        
        // Deve vir ordenado de forma decrescente: UsinaEntity B (300), depois UsinaEntity A (200)
        assertEquals("UsinaEntity Eolica B", ranking.get(0)[0]);
        // Hibernate converte SUM para BigDecimal ou Double dependendo da coluna, usando toString para seguranca
        assertEquals(0, new BigDecimal("300.0").compareTo(new BigDecimal(ranking.get(0)[1].toString())));

        assertEquals("UsinaEntity Eolica A", ranking.get(1)[0]);
        assertEquals(0, new BigDecimal("200.0").compareTo(new BigDecimal(ranking.get(1)[1].toString())));
    }
}

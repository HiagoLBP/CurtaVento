package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
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
    private HistoricoCorteOnsRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveAgruparRankingCortesAnoCorretamente() {
        // Arrange
        Municipio mun = new Municipio();
        mun.setNome("Caetite");
        entityManager.persist(mun);

        Usina usinaA = new Usina();
        usinaA.setNome("Usina Eolica A");
        usinaA.setMunicipio(mun);
        entityManager.persist(usinaA);

        Usina usinaB = new Usina();
        usinaB.setNome("Usina Eolica B");
        usinaB.setMunicipio(mun);
        entityManager.persist(usinaB);

        HistoricoCorteOns corte1 = new HistoricoCorteOns();
        corte1.setUsina(usinaA);
        corte1.setDataCorte(LocalDateTime.of(2026, 5, 10, 12, 0));
        corte1.setEnergiaCortadaMwh(new BigDecimal("150.0"));
        entityManager.persist(corte1);

        HistoricoCorteOns corte2 = new HistoricoCorteOns();
        corte2.setUsina(usinaA);
        corte2.setDataCorte(LocalDateTime.of(2026, 6, 12, 12, 0));
        corte2.setEnergiaCortadaMwh(new BigDecimal("50.0"));
        entityManager.persist(corte2);

        HistoricoCorteOns corte3 = new HistoricoCorteOns();
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
        
        // Deve vir ordenado de forma decrescente: Usina B (300), depois Usina A (200)
        assertEquals("Usina Eolica B", ranking.get(0)[0]);
        // Hibernate converte SUM para BigDecimal ou Double dependendo da coluna, usando toString para seguranca
        assertEquals(0, new BigDecimal("300.0").compareTo(new BigDecimal(ranking.get(0)[1].toString())));

        assertEquals("Usina Eolica A", ranking.get(1)[0]);
        assertEquals(0, new BigDecimal("200.0").compareTo(new BigDecimal(ranking.get(1)[1].toString())));
    }
}

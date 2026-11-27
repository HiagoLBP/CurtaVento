package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.PrecoEnergia;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImpactoVafService {

    private static final BigDecimal IPM_FALLBACK = new BigDecimal("0.02");
    private static final BigDecimal CEM = new BigDecimal("100");

    private final SpringDataImpactoVafRepository impactoRepository;
    private final SpringDataMunicipioRepository municipioRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    public ImpactoVafService(SpringDataImpactoVafRepository impactoRepository,
                             SpringDataMunicipioRepository municipioRepository,
                             SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.impactoRepository = impactoRepository;
        this.municipioRepository = municipioRepository;
        this.corteRepository = corteRepository;
    }

    public ImpactoVafEntity calcularImpacto(Long municipioId, int ano) {
        MunicipioEntity municipio = municipioRepository.findById(municipioId)
                .orElseThrow(() -> new RuntimeException("Município não encontrado com o ID: " + municipioId));

        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        BigDecimal somaBanco = corteRepository.sumEnergiaCortadaPorMunicipioEAno(municipioId, inicioAno, fimAno);
        BigDecimal energiaTotalCortada = somaBanco != null ? somaBanco : BigDecimal.ZERO;

        // 1. Perda de Receita Bruta com base no preço médio configurado em PrecoEnergia
        BigDecimal perdaReceita = energiaTotalCortada.multiply(BigDecimal.valueOf(PrecoEnergia.PRECO_MWH));

        // 2. Impacto estimado sobre o IPM/VAF municipal (prioriza municipio.getIpm(), fallback vafAnoBase ou 2%)
        BigDecimal ipmMunicipio = municipio.getIpm() != null
                ? municipio.getIpm()
                : (municipio.getVafAnoBase() != null ? municipio.getVafAnoBase() : IPM_FALLBACK);
        if (ipmMunicipio.compareTo(BigDecimal.ONE) > 0) {
            // Se o valor foi gravado em percentual (ex.: 2.5 em vez de 0.025), converte com escala segura
            ipmMunicipio = ipmMunicipio.divide(CEM, 8, RoundingMode.HALF_UP);
        }

        BigDecimal impactoFinal = perdaReceita.multiply(ipmMunicipio);

        List<ImpactoVafEntity> impactosExistentes = impactoRepository.findByMunicipioIdAndAnoReferencia(municipioId, ano);
        ImpactoVafEntity relatorio;
        if (impactosExistentes != null && !impactosExistentes.isEmpty()) {
            relatorio = impactosExistentes.get(impactosExistentes.size() - 1);
        } else {
            relatorio = new ImpactoVafEntity();
            relatorio.setMunicipio(municipio);
            relatorio.setAnoReferencia(ano);
        }

        relatorio.setEnergiaTotalCortadaMwh(energiaTotalCortada);
        relatorio.setPerdaReceitaEstimada(perdaReceita);
        relatorio.setImpactoVafEstimado(impactoFinal);
        relatorio.setDataCalculo(LocalDateTime.now());

        try {
            return impactoRepository.save(relatorio);
        } catch (DataIntegrityViolationException ex) {
            System.err.println("Concorrência resolvida: Relatório VAF já existe para o município " + municipioId + " no ano " + ano);
            List<ImpactoVafEntity> salvos = impactoRepository.findByMunicipioIdAndAnoReferencia(municipioId, ano);
            return salvos.get(salvos.size() - 1);
        }
    }
}

package com.curtavento.curta_vento.application.usecase;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImpactoVafService {

    private final SpringDataImpactoVafRepository impactoRepository;
    private final SpringDataMunicipioRepository springDataMunicipioRepository;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    // Preço médio do MWh (R$ 150,00) conforme regra do projeto
    

    public ImpactoVafService(SpringDataImpactoVafRepository impactoRepository, SpringDataMunicipioRepository springDataMunicipioRepository,
                             SpringDataUsinaRepository springDataUsinaRepository, SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.impactoRepository = impactoRepository;
        this.springDataMunicipioRepository = springDataMunicipioRepository;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.corteRepository = corteRepository;
    }

    public ImpactoVafEntity calcularImpacto(Long municipioId, int ano) {
        MunicipioEntity municipio = springDataMunicipioRepository.findById(municipioId)
                .orElseThrow(() -> new RuntimeException("Município não encontrado com o ID: " + municipioId));

        List<UsinaEntity> usinasDoMunicipio = springDataUsinaRepository.findAll().stream()
                .filter(u -> u.getMunicipio().getId().equals(municipioId))
                .collect(Collectors.toList());

        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        BigDecimal energiaTotalCortada = BigDecimal.ZERO;

        for (UsinaEntity usina : usinasDoMunicipio) {
            List<HistoricoCorteOnsEntity> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(usina.getId(), inicioAno, fimAno);

            for (HistoricoCorteOnsEntity corte : cortes) {
                if (corte.getEnergiaCortadaMwh() != null) {
                    energiaTotalCortada = energiaTotalCortada.add(corte.getEnergiaCortadaMwh());
                }
            }
        }

        // 1. Perda de Receita Bruta
        BigDecimal perdaReceita = energiaTotalCortada.multiply(BigDecimal.valueOf(com.curtavento.curta_vento.domain.model.PrecoEnergia.PRECO_MWH));

        // 2. O Impacto final (que vai pra tela)
        BigDecimal ipmMunicipio = municipio.getVafAnoBase() != null ? municipio.getVafAnoBase() : new BigDecimal("0.02"); // Fallback para 2% caso nulo no banco
        if (ipmMunicipio.compareTo(BigDecimal.ONE) > 0) {
            // Se alguém preencheu 2.5 em vez de 0.025 no banco, dividimos por 100
            ipmMunicipio = ipmMunicipio.divide(new BigDecimal("100"));
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
        // Salvando os novos valores ajustados
        relatorio.setPerdaReceitaEstimada(perdaReceita);
        relatorio.setImpactoVafEstimado(impactoFinal);
        relatorio.setDataCalculo(LocalDateTime.now());

        try {
            return impactoRepository.save(relatorio);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            System.err.println("Concorrência resolvida: Relatório VAF já existe para o município " + municipioId + " no ano " + ano);
            List<ImpactoVafEntity> salvos = impactoRepository.findByMunicipioIdAndAnoReferencia(municipioId, ano);
            return salvos.get(salvos.size() - 1);
        }
    }
}
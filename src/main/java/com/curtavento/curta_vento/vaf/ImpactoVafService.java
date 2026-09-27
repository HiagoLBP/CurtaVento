package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.corte.HistoricoCorteOns;
import com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImpactoVafService {

    private final ImpactoVafRepository impactoRepository;
    private final MunicipioRepository municipioRepository;
    private final UsinaRepository usinaRepository;
    private final HistoricoCorteOnsRepository corteRepository;

    // Preço médio do MWh (R$ 150,00) conforme regra do projeto
    

    public ImpactoVafService(ImpactoVafRepository impactoRepository, MunicipioRepository municipioRepository,
                             UsinaRepository usinaRepository, HistoricoCorteOnsRepository corteRepository) {
        this.impactoRepository = impactoRepository;
        this.municipioRepository = municipioRepository;
        this.usinaRepository = usinaRepository;
        this.corteRepository = corteRepository;
    }

    public ImpactoVaf calcularImpacto(Long municipioId, int ano) {
        Municipio municipio = municipioRepository.findById(municipioId)
                .orElseThrow(() -> new RuntimeException("Município não encontrado com o ID: " + municipioId));

        List<Usina> usinasDoMunicipio = usinaRepository.findAll().stream()
                .filter(u -> u.getMunicipio().getId().equals(municipioId))
                .collect(Collectors.toList());

        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        BigDecimal energiaTotalCortada = BigDecimal.ZERO;

        for (Usina usina : usinasDoMunicipio) {
            List<HistoricoCorteOns> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(usina.getId(), inicioAno, fimAno);

            for (HistoricoCorteOns corte : cortes) {
                if (corte.getEnergiaCortadaMwh() != null) {
                    energiaTotalCortada = energiaTotalCortada.add(corte.getEnergiaCortadaMwh());
                }
            }
        }

        List<ImpactoVaf> impactosExistentes = impactoRepository.findByMunicipioIdAndAnoReferencia(municipioId, ano);
        ImpactoVaf relatorio;
        if (impactosExistentes != null && !impactosExistentes.isEmpty()) {
            relatorio = impactosExistentes.get(impactosExistentes.size() - 1);
        } else {
            relatorio = new ImpactoVaf();
            relatorio.setMunicipio(municipio);
            relatorio.setAnoReferencia(ano);
        }

        relatorio.setEnergiaTotalCortadaMwh(energiaTotalCortada);
        // Não há preço por período nem regra fiscal municipal verificável disponíveis.
        // Persistir um número nesses campos transformaria uma estimativa em dado factual.
        relatorio.setPerdaReceitaEstimada(null);
        relatorio.setImpactoVafEstimado(null);
        relatorio.setFinanceiroDisponivel(false);
        relatorio.setDataCalculo(LocalDateTime.now());

        return impactoRepository.save(relatorio);
    }
}
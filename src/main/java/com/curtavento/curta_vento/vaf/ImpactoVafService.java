package com.curtavento.curta_vento.vaf;

import com.curtavento.curta_vento.corte.HistoricoCorteOns;
import com.curtavento.curta_vento.corte.HistoricoCorteOnsRepository;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.pld.PldHorario;
import com.curtavento.curta_vento.pld.PldHorarioRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImpactoVafService {

    private final ImpactoVafRepository impactoRepository;
    private final MunicipioRepository municipioRepository;
    private final UsinaRepository usinaRepository;
    private final HistoricoCorteOnsRepository corteRepository;
    private final PldHorarioRepository pldRepository;

    public ImpactoVafService(ImpactoVafRepository impactoRepository, MunicipioRepository municipioRepository,
                             UsinaRepository usinaRepository, HistoricoCorteOnsRepository corteRepository,
                             PldHorarioRepository pldRepository) {
        this.impactoRepository = impactoRepository;
        this.municipioRepository = municipioRepository;
        this.usinaRepository = usinaRepository;
        this.corteRepository = corteRepository;
        this.pldRepository = pldRepository;
    }

    public ImpactoVaf calcularImpacto(Long municipioId, int ano) {
        Municipio municipio = municipioRepository.findById(municipioId)
                .orElseThrow(() -> new RuntimeException("Municipio nao encontrado com o ID: " + municipioId));

        List<Usina> usinasDoMunicipio = usinaRepository.findAll().stream()
                .filter(u -> u.getMunicipio().getId().equals(municipioId))
                .collect(Collectors.toList());

        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        BigDecimal energiaTotalCortada = BigDecimal.ZERO;
        BigDecimal perdaReceitaTotal = BigDecimal.ZERO;

        for (Usina usina : usinasDoMunicipio) {
            List<HistoricoCorteOns> cortes = corteRepository.findByUsinaIdAndDataCorteBetween(usina.getId(), inicioAno, fimAno);

            for (HistoricoCorteOns corte : cortes) {
                if (corte.getEnergiaCortadaMwh() != null) {
                    BigDecimal mwh = corte.getEnergiaCortadaMwh();
                    energiaTotalCortada = energiaTotalCortada.add(mwh);

                    // Busca o PLD da hora exata do corte para o submercado Nordeste
                    int horaPld = corte.getDataCorte().getHour() + 1; // CCEE usa horas 1 a 24
                    Optional<PldHorario> pld = pldRepository.findFirstByDiaAndHoraAndSubmercadoOrderByPeriodoComercializacaoAsc(
                            corte.getDataCorte().toLocalDate(), horaPld, "NORDESTE");

                    if (pld.isPresent() && pld.get().getPldHora() != null) {
                        BigDecimal receitaHora = mwh.multiply(pld.get().getPldHora());
                        perdaReceitaTotal = perdaReceitaTotal.add(receitaHora);
                    }
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
        
        // 1. O Faturamento perdido  a perda de receita (PLD * MWh cortado)
        relatorio.setPerdaReceitaEstimada(perdaReceitaTotal);
        
        // 2. Alquota ICMS da Bahia  18% - ento o ICMS que deixou de ser gerado:
        BigDecimal icmsEstado = perdaReceitaTotal.multiply(new BigDecimal("0.18"));
        
        // 3. A arrecadao municipal  o ICMS do Estado * IPM do municpio (%)
        BigDecimal impactoMunicipal = BigDecimal.ZERO;
        if (municipio.getIpm() != null) {
            // IPM est em % (ex: 0.0378554), ento dividimos por 100 para a frao
            BigDecimal fracaoIpm = municipio.getIpm().divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
            impactoMunicipal = icmsEstado.multiply(fracaoIpm);
        }
        
        relatorio.setImpactoVafEstimado(impactoMunicipal);
        relatorio.setFinanceiroDisponivel(true); // Dados disponiveis (PLD e IPM integrados)
        relatorio.setDataCalculo(LocalDateTime.now());

        return impactoRepository.save(relatorio);
    }
}
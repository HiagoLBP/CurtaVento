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
    private static final BigDecimal PRECO_MEDIO_MWH = new BigDecimal("150.00");

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

        // 1. Perda de Receita Bruta
        BigDecimal perdaReceita = energiaTotalCortada.multiply(PRECO_MEDIO_MWH);

        // 2. O Impacto final (que vai pra tela)
        BigDecimal ipmMunicipio = municipio.getVafAnoBase() != null ? municipio.getVafAnoBase() : new BigDecimal("0.02"); // Fallback para 2% caso nulo no banco
        if (ipmMunicipio.compareTo(BigDecimal.ONE) > 0) {
            // Se alguém preencheu 2.5 em vez de 0.025 no banco, dividimos por 100
            ipmMunicipio = ipmMunicipio.divide(new BigDecimal("100"));
        }
        
        BigDecimal impactoFinal = perdaReceita.multiply(ipmMunicipio);

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
        // Salvando os novos valores ajustados
        relatorio.setPerdaReceitaEstimada(perdaReceita);
        relatorio.setImpactoVafEstimado(impactoFinal);
        relatorio.setDataCalculo(LocalDateTime.now());

        return impactoRepository.save(relatorio);
    }
}
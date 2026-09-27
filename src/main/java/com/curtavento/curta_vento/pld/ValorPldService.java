package com.curtavento.curta_vento.pld;

import com.curtavento.curta_vento.corte.HistoricoCorteOnsBloco;
import com.curtavento.curta_vento.corte.HistoricoCorteOnsBlocoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValorPldService {
    private final HistoricoCorteOnsBlocoRepository blocoRepository;
    private final PldHorarioRepository pldRepository;

    public ValorPldService(HistoricoCorteOnsBlocoRepository blocoRepository, PldHorarioRepository pldRepository) {
        this.blocoRepository = blocoRepository;
        this.pldRepository = pldRepository;
    }

    public Resultado calcular(LocalDateTime inicio, LocalDateTime fim) {
        List<HistoricoCorteOnsBloco> blocos = blocoRepository.findByInstanteBetween(inicio, fim);
        BigDecimal valor = BigDecimal.ZERO;
        BigDecimal energiaComPld = BigDecimal.ZERO;
        int blocosSemPld = 0;
        for (HistoricoCorteOnsBloco bloco : blocos) {
            PldHorario pld = pldRepository
                    .findFirstByDiaAndHoraAndSubmercadoOrderByPeriodoComercializacaoAsc(
                            bloco.getInstante().toLocalDate(), bloco.getInstante().getHour(), "NORDESTE")
                    .orElse(null);
            if (pld == null) {
                blocosSemPld++;
                continue;
            }
            BigDecimal energia = bloco.getEnergiaCortadaMwh();
            energiaComPld = energiaComPld.add(energia);
            valor = valor.add(energia.multiply(pld.getPldHora()));
        }
        return new Resultado(blocos.size(), blocosSemPld, energiaComPld, valor);
    }

    public record Resultado(int blocosONS, int blocosSemPld, BigDecimal energiaComPldMwh,
                            BigDecimal valorEconomicoEstimadoReais) {}
}

package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsBlocoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HistoricoCorteOnsService {

    private final SpringDataHistoricoCorteOnsRepository corteRepository;
    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;

    public HistoricoCorteOnsService(SpringDataHistoricoCorteOnsRepository corteRepository,
                                    SpringDataHistoricoCorteOnsBlocoRepository blocoRepository) {
        this.corteRepository = corteRepository;
        this.blocoRepository = blocoRepository;
    }

    public HistoricoCorteOnsEntity registrarCorte(HistoricoCorteOnsEntity corte) {
        Optional<HistoricoCorteOnsEntity> existente = corteRepository.findByUsinaIdAndDataCorte(
                corte.getUsina().getId(), corte.getDataCorte());
        if (existente.isPresent()) {
            HistoricoCorteOnsEntity existenteCorte = existente.get();
            existenteCorte.setEnergiaCortadaMwh(corte.getEnergiaCortadaMwh());
            existenteCorte.setDuracaoMinutos(corte.getDuracaoMinutos());
            existenteCorte.setMotivoOns(corte.getMotivoOns());
            existenteCorte.setEnergiaEstimadaMwh(corte.getEnergiaEstimadaMwh());
            existenteCorte.setEnergiaVerificadaMwh(corte.getEnergiaVerificadaMwh());
            existenteCorte.setCodRazaoRestricao(corte.getCodRazaoRestricao());
            existenteCorte.setDscRestricao(corte.getDscRestricao());
            existenteCorte.setSubestacaoConexao(corte.getSubestacaoConexao());
            existenteCorte.setVentoVerificadoMs(corte.getVentoVerificadoMs());
            existenteCorte.setValoradoPldRs(corte.getValoradoPldRs());
            return corteRepository.save(existenteCorte);
        }

        return corteRepository.save(corte);
    }

    @Transactional
    public int registrarCortes(List<HistoricoCorteOnsEntity> cortes, LocalDateTime inicio, LocalDateTime fim) {
        if (cortes == null || cortes.isEmpty()) {
            return 0;
        }
        Map<String, HistoricoCorteOnsEntity> existentesPorChave = new HashMap<>();
        for (HistoricoCorteOnsEntity c : corteRepository.findByDataCorteBetween(inicio, fim)) {
            existentesPorChave.put(c.getUsina().getId() + "|" + c.getDataCorte(), c);
        }

        List<HistoricoCorteOnsEntity> paraSalvar = new ArrayList<>(cortes.size());
        for (HistoricoCorteOnsEntity novo : cortes) {
            String chave = novo.getUsina().getId() + "|" + novo.getDataCorte();
            HistoricoCorteOnsEntity alvo = existentesPorChave.get(chave);
            if (alvo != null) {
                alvo.setEnergiaCortadaMwh(novo.getEnergiaCortadaMwh());
                alvo.setDuracaoMinutos(novo.getDuracaoMinutos());
                alvo.setMotivoOns(novo.getMotivoOns());
                alvo.setEnergiaEstimadaMwh(novo.getEnergiaEstimadaMwh());
                alvo.setEnergiaVerificadaMwh(novo.getEnergiaVerificadaMwh());
                alvo.setCodRazaoRestricao(novo.getCodRazaoRestricao());
                alvo.setDscRestricao(novo.getDscRestricao());
                alvo.setSubestacaoConexao(novo.getSubestacaoConexao());
                alvo.setVentoVerificadoMs(novo.getVentoVerificadoMs());
                alvo.setValoradoPldRs(novo.getValoradoPldRs());
                paraSalvar.add(alvo);
            } else {
                paraSalvar.add(novo);
            }
        }
        corteRepository.saveAll(paraSalvar);
        return paraSalvar.size();
    }

    @Transactional
    public int registrarBlocos(List<HistoricoCorteOnsBlocoEntity> blocos, LocalDateTime inicio, LocalDateTime fim) {
        if (blocos == null || blocos.isEmpty()) {
            return 0;
        }
        Map<String, HistoricoCorteOnsBlocoEntity> existentesPorChave = new HashMap<>();
        for (HistoricoCorteOnsBlocoEntity b : blocoRepository.findByInstanteBetween(inicio, fim)) {
            existentesPorChave.put(b.getUsina().getId() + "|" + b.getInstante(), b);
        }

        List<HistoricoCorteOnsBlocoEntity> paraSalvar = new ArrayList<>(blocos.size());
        for (HistoricoCorteOnsBlocoEntity novo : blocos) {
            String chave = novo.getUsina().getId() + "|" + novo.getInstante();
            HistoricoCorteOnsBlocoEntity alvo = existentesPorChave.get(chave);
            if (alvo != null) {
                alvo.setGeracaoEstimadaMw(novo.getGeracaoEstimadaMw());
                alvo.setGeracaoVerificadaMw(novo.getGeracaoVerificadaMw());
                alvo.setEnergiaCortadaMwh(novo.getEnergiaCortadaMwh());
                alvo.setMotivoOns(novo.getMotivoOns());
                alvo.setCodRazaoRestricao(novo.getCodRazaoRestricao());
                alvo.setDscRestricao(novo.getDscRestricao());
                alvo.setSubestacaoConexao(novo.getSubestacaoConexao());
                alvo.setVentoVerificadoMs(novo.getVentoVerificadoMs());
                alvo.setPldMwh(novo.getPldMwh());
                paraSalvar.add(alvo);
            } else {
                paraSalvar.add(novo);
            }
        }
        blocoRepository.saveAll(paraSalvar);
        return paraSalvar.size();
    }

    public List<HistoricoCorteOnsEntity> buscarPorUsina(Long usinaId) {
        return corteRepository.findByUsinaId(usinaId);
    }
}

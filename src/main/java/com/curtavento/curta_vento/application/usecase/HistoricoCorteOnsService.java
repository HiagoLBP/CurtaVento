package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoricoCorteOnsService {

    private final SpringDataHistoricoCorteOnsRepository corteRepository;

    public HistoricoCorteOnsService(SpringDataHistoricoCorteOnsRepository corteRepository) {
        this.corteRepository = corteRepository;
    }

    public HistoricoCorteOnsEntity registrarCorte(HistoricoCorteOnsEntity corte) {
        Optional<HistoricoCorteOnsEntity> existente = corteRepository.findByUsinaIdAndDataCorte(corte.getUsina().getId(), corte.getDataCorte());
        if (existente.isPresent()) {
            HistoricoCorteOnsEntity existenteCorte = existente.get();
            existenteCorte.setEnergiaCortadaMwh(corte.getEnergiaCortadaMwh());
            existenteCorte.setDuracaoMinutos(corte.getDuracaoMinutos());
            existenteCorte.setMotivoOns(corte.getMotivoOns());
            return corteRepository.save(existenteCorte);
        }

        return corteRepository.save(corte);
    }

    public List<HistoricoCorteOnsEntity> buscarPorUsina(Long usinaId) {
        return corteRepository.findByUsinaId(usinaId);
    }
}
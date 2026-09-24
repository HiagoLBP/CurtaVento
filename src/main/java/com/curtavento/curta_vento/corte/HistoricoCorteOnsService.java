package com.curtavento.curta_vento.corte;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoCorteOnsService {

    private final HistoricoCorteOnsRepository corteRepository;
    private final UsinaRepository usinaRepository;

    public HistoricoCorteOnsService(HistoricoCorteOnsRepository corteRepository, UsinaRepository usinaRepository) {
        this.corteRepository = corteRepository;
        this.usinaRepository = usinaRepository;
    }

    public HistoricoCorteOns registrarCorte(HistoricoCorteOns corte) {
        if (corte.getUsina() != null && corte.getUsina().getId() != null) {
            Usina usina = usinaRepository.findById(corte.getUsina().getId())
                    .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + corte.getUsina().getId()));
            corte.setUsina(usina);
        } else {
            throw new RuntimeException("O ID da usina é obrigatório para registrar um corte do ONS.");
        }

        if (corte.getDataCorte() == null) {
            corte.setDataCorte(LocalDateTime.now());
        }

        // Realiza o Upsert: busca se já existe para não duplicar
        java.util.Optional<HistoricoCorteOns> existente = corteRepository.findByUsinaIdAndDataCorte(corte.getUsina().getId(), corte.getDataCorte());
        if (existente.isPresent()) {
            HistoricoCorteOns existenteCorte = existente.get();
            existenteCorte.setEnergiaCortadaMwh(corte.getEnergiaCortadaMwh());
            existenteCorte.setDuracaoMinutos(corte.getDuracaoMinutos());
            existenteCorte.setMotivoOns(corte.getMotivoOns());
            return corteRepository.save(existenteCorte);
        }

        return corteRepository.save(corte);
    }

    public List<HistoricoCorteOns> buscarPorUsina(Long usinaId) {
        return corteRepository.findByUsinaId(usinaId);
    }
}
package com.curtavento.curta_vento.clima;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeituraClimaticaService {

    private final LeituraClimaticaRepository leituraRepository;
    private final UsinaRepository usinaRepository;

    public LeituraClimaticaService(LeituraClimaticaRepository leituraRepository, UsinaRepository usinaRepository) {
        this.leituraRepository = leituraRepository;
        this.usinaRepository = usinaRepository;
    }

    public LeituraClimatica salvar(LeituraClimatica leitura) {
        if (leitura.getUsina() != null && leitura.getUsina().getId() != null) {
            Usina usina = usinaRepository.findById(leitura.getUsina().getId())
                    .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + leitura.getUsina().getId()));
            leitura.setUsina(usina);
        } else {
            throw new RuntimeException("O ID da usina é obrigatório para registar o clima.");
        }

        if (leitura.getDataHora() == null) {
            leitura.setDataHora(LocalDateTime.now());
        }

        return leituraRepository.save(leitura);
    }

    public List<LeituraClimatica> buscarHistorico(Long usinaId, LocalDateTime inicio, LocalDateTime fim) {
        return leituraRepository.findByUsinaIdAndDataHoraBetween(usinaId, inicio, fim);
    }
    
    public LeituraClimatica buscarUltimaLeitura(Long usinaId) {
        return leituraRepository.findFirstByUsinaIdOrderByDataHoraDesc(usinaId);
    }
}
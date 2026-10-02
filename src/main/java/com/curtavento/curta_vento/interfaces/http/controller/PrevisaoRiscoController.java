package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/previsoes")
public class PrevisaoRiscoController {

    private final com.curtavento.curta_vento.application.usecase.PrevisaoRiscoService riscoService;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataLeituraClimaticaRepository climaRepository;

    public PrevisaoRiscoController(com.curtavento.curta_vento.application.usecase.PrevisaoRiscoService riscoService, SpringDataUsinaRepository springDataUsinaRepository, SpringDataLeituraClimaticaRepository climaRepository) {
        this.riscoService = riscoService;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.climaRepository = climaRepository;
    }

    @GetMapping("/usina/{usinaId}")
    public List<PrevisaoRiscoEntity> listarPorUsina(@PathVariable Long usinaId) {
        return riscoService.buscarPorUsina(usinaId);
    }

    @PostMapping("/recalcular/{usinaId}")
    public PrevisaoRiscoEntity recalcular(@PathVariable Long usinaId, @RequestParam(defaultValue = "24") int horizonte) {
        UsinaEntity usina = springDataUsinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("UsinaEntity não encontrada com o ID: " + usinaId));

        LeituraClimaticaEntity ultimaLeitura = climaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usinaId);

        return riscoService.calcularRisco(usina, ultimaLeitura, horizonte);
    }
}
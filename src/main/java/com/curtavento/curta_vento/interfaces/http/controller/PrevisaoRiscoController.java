package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.PrevisaoCurvaRisco48hUseCase;
import com.curtavento.curta_vento.application.usecase.PrevisaoRiscoService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.LeituraClimaticaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PrevisaoRiscoEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataLeituraClimaticaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/previsoes")
public class PrevisaoRiscoController {

    private final PrevisaoRiscoService riscoService;
    private final PrevisaoCurvaRisco48hUseCase previsaoCurvaRisco48hUseCase;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataLeituraClimaticaRepository climaRepository;

    public PrevisaoRiscoController(PrevisaoRiscoService riscoService,
                                   PrevisaoCurvaRisco48hUseCase previsaoCurvaRisco48hUseCase,
                                   SpringDataUsinaRepository springDataUsinaRepository,
                                   SpringDataLeituraClimaticaRepository climaRepository) {
        this.riscoService = riscoService;
        this.previsaoCurvaRisco48hUseCase = previsaoCurvaRisco48hUseCase;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.climaRepository = climaRepository;
    }

    @GetMapping("/usina/{usinaId}")
    public List<PrevisaoRiscoEntity> listarPorUsina(@PathVariable Long usinaId) {
        return riscoService.buscarPorUsina(usinaId);
    }

    @GetMapping("/curva-48h")
    public ResponseEntity<Map<String, Object>> obterCurva48h(@RequestParam Long usinaId) {
        return ResponseEntity.ok(previsaoCurvaRisco48hUseCase.executar(usinaId));
    }

    @PostMapping("/recalcular/{usinaId}")
    public PrevisaoRiscoEntity recalcular(@PathVariable Long usinaId, @RequestParam(defaultValue = "24") int horizonte) {
        UsinaEntity usina = springDataUsinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        LeituraClimaticaEntity ultimaLeitura = climaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usinaId);

        return riscoService.calcularRisco(usina, ultimaLeitura, horizonte);
    }
}

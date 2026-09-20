package com.curtavento.curta_vento.risco;

import com.curtavento.curta_vento.clima.LeituraClimatica;
import com.curtavento.curta_vento.clima.LeituraClimaticaRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/previsoes")
public class PrevisaoRiscoController {

    private final PrevisaoRiscoService riscoService;
    private final UsinaRepository usinaRepository;
    private final LeituraClimaticaRepository climaRepository;

    public PrevisaoRiscoController(PrevisaoRiscoService riscoService, UsinaRepository usinaRepository, LeituraClimaticaRepository climaRepository) {
        this.riscoService = riscoService;
        this.usinaRepository = usinaRepository;
        this.climaRepository = climaRepository;
    }

    @GetMapping("/usina/{usinaId}")
    public List<PrevisaoRisco> listarPorUsina(@PathVariable Long usinaId) {
        return riscoService.buscarPorUsina(usinaId);
    }

    @PostMapping("/recalcular/{usinaId}")
    public PrevisaoRisco recalcular(@PathVariable Long usinaId, @RequestParam(defaultValue = "24") int horizonte) {
        Usina usina = usinaRepository.findById(usinaId)
                .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usinaId));

        LeituraClimatica ultimaLeitura = climaRepository.findFirstByUsinaIdOrderByDataHoraDesc(usinaId);

        return riscoService.calcularRisco(usina, ultimaLeitura, horizonte);
    }
}
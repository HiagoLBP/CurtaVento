package com.curtavento.curta_vento.pld;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pld")
public class ValorPldController {
    private final ValorPldService service;

    public ValorPldController(ValorPldService service) {
        this.service = service;
    }

    @GetMapping("/valor-estimado")
    public Map<String, Object> calcular(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        ValorPldService.Resultado resultado = service.calcular(inicio, fim);
        return Map.of(
                "indicador", "valor econômico estimado da energia cortada com base no PLD",
                "fonte", "CCEE PLD_HORARIO",
                "submercado", "NORDESTE",
                "blocosOns", resultado.blocosONS(),
                "blocosSemPld", resultado.blocosSemPld(),
                "energiaComPldMwh", resultado.energiaComPldMwh(),
                "valorEconomicoEstimadoReais", resultado.valorEconomicoEstimadoReais(),
                "completo", resultado.blocosSemPld() == 0);
    }
}

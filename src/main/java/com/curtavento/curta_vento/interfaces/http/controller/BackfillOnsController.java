package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.infrastructure.ons.BackfillOnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/backfill-ons")
public class BackfillOnsController {

    private final BackfillOnsService backfillService;

    @Autowired
    public BackfillOnsController(BackfillOnsService backfillService) {
        this.backfillService = backfillService;
    }

    @PostMapping
    public ResponseEntity<String> backfillMes(@RequestParam int ano, @RequestParam int mes) {
        int total = backfillService.baixarEProcessarMes(ano, mes);
        return ResponseEntity.ok("Mês " + mes + "/" + ano + ": " + total + " registros diários agregados gravados.");
    }

    @PostMapping("/intervalo")
    public ResponseEntity<String> backfillIntervalo(
            @RequestParam int anoInicio, @RequestParam int mesInicio,
            @RequestParam int anoFim, @RequestParam int mesFim) {
        int total = backfillService.baixarEProcessarIntervalo(anoInicio, mesInicio, anoFim, mesFim);
        return ResponseEntity.ok("Intervalo " + mesInicio + "/" + anoInicio + " até " + mesFim + "/" + anoFim
                + ": " + total + " registros diários agregados gravados.");
    }

    @Autowired
    private com.curtavento.curta_vento.infrastructure.batch.CalculoRiscoDiarioJob riscoJob;

    @Autowired
    private com.curtavento.curta_vento.infrastructure.batch.RecalculoImpactoMunicipalJob impactoJob;

    @PostMapping("/recalcular-tudo")
    public ResponseEntity<String> recalcularTudo() {
        riscoJob.processarCalculoDeRisco();
        impactoJob.recalcularImpacto();
        return ResponseEntity.ok("Cálculo de Risco e Impacto VAF executados com sucesso no banco de dados!");
    }
}
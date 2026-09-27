package com.curtavento.curta_vento.corte;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ons.backfill.enabled", havingValue = "true")
public class BackfillOnsRunner implements CommandLineRunner {
    private final BackfillOnsService service;

    public BackfillOnsRunner(BackfillOnsService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        int anoInicio = Integer.getInteger("ons.backfill.ano-inicio", 2026);
        int mesInicio = Integer.getInteger("ons.backfill.mes-inicio", 1);
        int anoFim = Integer.getInteger("ons.backfill.ano-fim", 2026);
        int mesFim = Integer.getInteger("ons.backfill.mes-fim", 9);
        service.baixarEProcessarIntervalo(anoInicio, mesInicio, anoFim, mesFim);
    }
}

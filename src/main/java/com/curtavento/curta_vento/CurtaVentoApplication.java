package com.curtavento.curta_vento;

import com.curtavento.curta_vento.infrastructure.ons.BackfillOnsService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class CurtaVentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurtaVentoApplication.class, args);
	}

	@Bean
	public ApplicationRunner cliBackfillRunner(ObjectProvider<BackfillOnsService> backfillOnsServiceProvider) {
		return args -> {
			if (args.containsOption("backfill")) {
				BackfillOnsService backfillOnsService = backfillOnsServiceProvider.getObject();
				String valor = args.getOptionValues("backfill").get(0);
				// Formato esperado: YYYY-MM:YYYY-MM (ex: 2026-01:2026-11)
				String[] partes = valor.split(":");
				String[] inicio = partes[0].split("-");
				String[] fim = partes[1].split("-");
				int anoInicio = Integer.parseInt(inicio[0]);
				int mesInicio = Integer.parseInt(inicio[1]);
				int anoFim = Integer.parseInt(fim[0]);
				int mesFim = Integer.parseInt(fim[1]);
				int total = backfillOnsService.baixarEProcessarIntervalo(anoInicio, mesInicio, anoFim, mesFim);
				System.out.println("BACKFILL_CONCLUIDO total_diarios=" + total);
				System.exit(0);
			}
		};
	}
}

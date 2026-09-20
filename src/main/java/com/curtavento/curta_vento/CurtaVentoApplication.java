package com.curtavento.curta_vento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class CurtaVentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurtaVentoApplication.class, args);
	}

}

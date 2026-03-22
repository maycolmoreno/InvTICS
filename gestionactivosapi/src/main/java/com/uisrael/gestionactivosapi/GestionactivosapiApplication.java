package com.uisrael.gestionactivosapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionactivosapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionactivosapiApplication.class, args);
	}

}

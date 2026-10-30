package com.ruta_limpia.solicitudes_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SolicitudesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolicitudesServiceApplication.class, args);
	}

}

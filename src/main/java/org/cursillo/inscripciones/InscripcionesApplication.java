package org.cursillo.inscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EntityScan("org.cursillo.commons.entities")
@SpringBootApplication(scanBasePackages = "org.cursillo")
public class InscripcionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(InscripcionesApplication.class, args);
	}

}

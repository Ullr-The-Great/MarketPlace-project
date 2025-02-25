package es.ullr.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class MiProyectoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiProyectoApplication.class, args);
	}

}

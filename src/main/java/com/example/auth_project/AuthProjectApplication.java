package com.example.auth_project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AuthProjectApplication {

	public static void main(String[] args) {
		// Load .env file before Spring Boot starts
		try {
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();
			
			// Set system properties from .env
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
				System.out.println("Loaded: " + entry.getKey() + " = " + entry.getValue());
			});
		} catch (Exception e) {
			System.out.println("Warning: Could not load .env file: " + e.getMessage());
		}
		
		SpringApplication.run(AuthProjectApplication.class, args);
	}

}

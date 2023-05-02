package com.shivansh.TCS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcsApplication.class, args);
	}

}
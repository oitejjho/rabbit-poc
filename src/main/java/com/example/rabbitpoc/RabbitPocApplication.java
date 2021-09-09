package com.example.rabbitpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitPocApplication.class, args);
	}

}

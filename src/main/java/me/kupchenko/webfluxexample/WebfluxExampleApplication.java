package me.kupchenko.webfluxexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@EnableCircuitBreaker
@SpringBootApplication
public class WebfluxExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxExampleApplication.class, args);
	}

}


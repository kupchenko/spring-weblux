package me.kupchenko.webfluxexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ThymeleafProperties.class)
public class WebfluxExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxExampleApplication.class, args);
	}

}


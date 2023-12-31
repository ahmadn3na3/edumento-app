package com.edumento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.edumento")
public class EdumentoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdumentoAppApplication.class, args);
	}

}

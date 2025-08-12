package com.example.repositorio_universitario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static software.amazon.awssdk.core.SdkSystemSetting.AWS_REGION;

@SpringBootApplication
public class RepositorioUniversitarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepositorioUniversitarioApplication.class, args);
	}

}

package com.scanly.scanlyBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScanlyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScanlyBackendApplication.class, args);
	}

}

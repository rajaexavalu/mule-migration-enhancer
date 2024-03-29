package com.exavalu.migration.enhancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExavaluMuleMigrationEnhancer {
	private static final Logger log = LoggerFactory.getLogger(ExavaluMuleMigrationEnhancer.class);

	public static void main(String[] args) {
		log.info("Exavalu Mule Migration Enhancer server has been started...");
		SpringApplication.run(ExavaluMuleMigrationEnhancer.class, args);
	}
}
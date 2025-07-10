package com.api.hub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;


@EnableScheduling
@Slf4j
@SpringBootApplication(scanBasePackages = "com.api.hub")
public class KrishnaApiHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(KrishnaApiHubApplication.class, args);
		log.info("application started");
	}
}
package com.api.hub.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class KrishnaApiHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(KrishnaApiHubApplication.class, args);
		log.info("application started");
	}

}

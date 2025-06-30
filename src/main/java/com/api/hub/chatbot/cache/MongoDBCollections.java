package com.api.hub.chatbot.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Singleton
@Data
@Slf4j
public class MongoDBCollections {

	private Map<String, String> collections = new HashMap<String, String>();
	
	@PostConstruct
	public void saveCollections() {
		log.info("saving collections");
		collections.put("20250309-00001-BusinessData", "krishnaapihub");
		collections.put("20250309-00001-ChatHistoryData", "chathistory");
	}
	
	public String getCollection(String uniqueId) {
		return collections.get("20250309-00001-BusinessData");
	}
}

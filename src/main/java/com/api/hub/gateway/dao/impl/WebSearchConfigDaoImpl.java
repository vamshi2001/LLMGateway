package com.api.hub.gateway.dao.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.gateway.dao.WebSearchConfigDao;
import com.api.hub.gateway.model.JsoupExtractionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.annotation.PostConstruct;

@Repository
public class WebSearchConfigDaoImpl implements WebSearchConfigDao{

	@Autowired
	private MongoDatabase db;
	
	@Value("${mongoDB.websearch.collection}")
	private String collectionName;
	
	private MongoCollection<Document> collection;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@PostConstruct
	public void init() {
		collection = db.getCollection(collectionName);
	}

	
	@Override
	public List<JsoupExtractionConfig> get() throws ApiHubException {
		
		try {
			FindIterable<Document> docList = collection.find();
	        List<JsoupExtractionConfig> configList = new ArrayList<>();
	        
	        for(Document doc : docList) {
	        	configList.add(objectMapper.convertValue(doc, JsoupExtractionConfig.class));
	        }

			return configList;
		}catch (Exception e) {
			throw DatabaseException.fromMongoException(e, "Unable to retrieve JsoupExtractionConfig");
		}
		
	}

}

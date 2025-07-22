package com.api.hub.gateway.dao.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.gateway.dao.WebSearchConfigDao;
import com.api.hub.gateway.model.JsoupExtractionConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import jakarta.annotation.PostConstruct;

@Repository
@ConditionalOnProperty(name = "mongoDB.websearch.props.enable", havingValue = "true")
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
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
	
	@Override
	public void save(JsoupExtractionConfig config) {
            Document doc = toDocument(config);
            collection.replaceOne(
		            Filters.eq("host", config.getHost()),  // Filter by modelId
		            doc,                             // New document to insert or replace with
		            new ReplaceOptions().upsert(true)
		        );
    }

    private Document toDocument(JsoupExtractionConfig config) {
        List<Document> ruleDocs = config.getRules().stream()
            .map(rule -> new Document()
                .append("type", rule.getType())
                .append("value", rule.getValue())
                .append("all", rule.isAll()))
            .collect(Collectors.toList());

        return new Document()
            .append("host", config.getHost())
            .append("rules", ruleDocs);
    }

}

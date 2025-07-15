package com.api.hub.gateway.dao.impl;

import java.util.Arrays;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.gateway.dao.PromptVersionDao;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.annotation.PostConstruct;

public class PromptVersionDaoImpl implements PromptVersionDao{

	@Autowired
	private MongoDatabase db;
	
	@Value("${mongoDB.prompt.collection}")
	private String collectionName;
	
	private MongoCollection<Document> collection;
	
	@PostConstruct
	public void init() {
		collection = db.getCollection(collectionName);
	}
	
	@Override
	public String getPrompt(String persona, String phase, String query) throws ApiHubException {
		
		 try {
			 Document searchStage = new Document("$search", new Document()
		                .append("index", "default")
		                .append("compound", new Document()
		                    .append("must", Arrays.asList(
		                        new Document("text", new Document()
		                            .append("query", query)
		                            .append("path", "prompt"))
		                    ))
		                    .append("filter", Arrays.asList(
		                        new Document("equals", new Document()
		                            .append("path", "persona")
		                            .append("value", persona)),
		                        new Document("equals", new Document()
		                            .append("path", "phase")
		                            .append("value", phase))
		                    ))
		                )
		            );
		            // Execute the pipeline
		            AggregateIterable<Document> results = collection.aggregate(Arrays.asList(
		                searchStage,
		                new Document("$limit", 1)
		            ));

		            // Print results
		            for (Document doc : results) {
		                return doc.getString("prompt");
		            }
		 }catch (Exception e) {
			throw DatabaseException.fromMongoException(e, "Unable to retrieve prompt for query " + query);
		 }
	            
		return "";
	}

	
}

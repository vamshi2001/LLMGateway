package com.api.hub.gateway.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.gateway.dao.PromptVersionDao;
import com.api.hub.gateway.model.PromptVersion;
import com.api.hub.gateway.model.TollCallData;
import com.google.common.util.concurrent.AtomicDouble;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.annotation.PostConstruct;

@Repository
@ConditionalOnProperty(name = "mongoDB.prompt.enabled", havingValue = "true")
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
		                .append("text", new Document()
		                		.append("query", query)
	                            .append("path", "prompt"))
						 .append("text", new Document()
			                		.append("query", persona)
		                            .append("path", "persona"))
						.append("text", new Document()
							.append("query", phase)
					        .append("path", "phase"))
		            );
		            
			
			 Document addScoreStage = new Document("$addFields", new Document(
			     "score", new Document("$meta", "searchScore")
			 ));

			 
			 Document sortStage = new Document("$sort", new Document("score", -1));


			 
			 List<Document> pipeline = Arrays.asList(searchStage, addScoreStage, sortStage);
			 AggregateIterable<Document> results = collection.aggregate(pipeline);
			 
			
			 StringBuffer buffer = new StringBuffer();
			 AtomicDouble score = new AtomicDouble(0.0);
			 results.forEach(e -> {
				 if(Double.compare(score.get(), e.getDouble("score")) <= 0) {
					 buffer.delete(0, buffer.length());
					 buffer.append(e.getString("prompt"));
					 score.set(e.getDouble("score"));
				 }
			 });
				 return buffer.toString();
			
		 }catch (Exception e) {
			throw DatabaseException.fromMongoException(e, "Unable to retrieve prompt for query " + query);
		 }
	}

	@Override
	public void save(PromptVersion pojo) {
		Document doc = new Document("persona", pojo.getPersona())
                .append("phase", pojo.getPhase())
                .append("prompt", pojo.getPrompt());

        collection.insertOne(doc);
	}

	@Override
	public List<PromptVersion> get() {
		FindIterable<Document> documents = collection.find();
		
		List<PromptVersion> list = new ArrayList<PromptVersion>();
		for (Document doc : documents) {
	    	
	        String persona = doc.getString("persona");
	        String phase = doc.getString("phase");
	        String prompt = doc.getString("prompt");
	        //String previousData = fileSizeMap.get(toolName);
	        
	        PromptVersion data = new PromptVersion();
        	data.setPersona(persona);
        	data.setPhase(phase);
        	data.setPrompt(prompt);
        	list.add(data);
		}
		return list;
	}
	
}

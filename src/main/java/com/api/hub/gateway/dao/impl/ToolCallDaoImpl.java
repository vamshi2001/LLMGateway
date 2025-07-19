package com.api.hub.gateway.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.api.hub.gateway.dao.ToolCallDao;
import com.api.hub.gateway.model.TollCallData;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import jakarta.annotation.PostConstruct;

@Repository
@ConditionalOnProperty(name = "mongoDB.toolCall.metadata.enable", havingValue = "true")
public class ToolCallDaoImpl implements ToolCallDao {

	@Autowired
	private MongoDatabase db;
	
	@Value("${mongoDB.toolcall.collection}")
	private String collectionName;
	
	private MongoCollection<Document> collection;
	
	@PostConstruct
	public void init() {
		collection = db.getCollection(collectionName);
	}
	
	@Override
	public List<TollCallData> get() {
		FindIterable<Document> documents = collection.find();
		
		List<TollCallData> list = new ArrayList<TollCallData>();
		for (Document doc : documents) {
	    	
	        String toolName = doc.getString("toolName");
	        String toolDescription = doc.getString("toolDescription");
	        List<String> supportedTopics = doc.getList("supportedTopics", String.class, new ArrayList<String>());
	        boolean enabled = doc.getBoolean("enabled", false);
	        String base64EncodedProps = doc.getString("toolArguments");
	        String endPoint = doc.getString("endPoint");
	        //String previousData = fileSizeMap.get(toolName);
	        
	        TollCallData toolData = new TollCallData();
        	toolData.setEnabled(enabled);
        	toolData.setSupportedPersona(supportedTopics);
        	toolData.setToolArguments(base64EncodedProps);
        	toolData.setToolDescription(toolDescription);
        	toolData.setToolName(toolName);
        	toolData.setEndPoint(endPoint);
        	list.add(toolData);
		}
		return list;
	}
	
	@Override
	public void save(TollCallData data) {
		 Document doc = new Document("toolName", data.getToolName())
                 .append("toolDescription", data.getToolDescription())
                 .append("supportedTopics", data.getSupportedPersona())
                 .append("enabled", data.isEnabled())
                 .append("toolArguments", data.getToolArguments())
                 .append("endPoint", data.getEndPoint());
		 

		 collection.replaceOne(
		            Filters.eq("toolName", data.getToolName()),
		            doc,                             // New document to insert or replace with
		            new ReplaceOptions().upsert(true)
		        );
	}

}

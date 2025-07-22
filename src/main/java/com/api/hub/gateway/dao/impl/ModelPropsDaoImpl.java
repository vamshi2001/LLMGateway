package com.api.hub.gateway.dao.impl;

import java.util.Date;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.gateway.dao.ModelPropsDao;
import com.api.hub.gateway.model.ChatHistory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import jakarta.annotation.PostConstruct;

@Repository
@ConditionalOnProperty(name = "mongoDB.model.props.enable", havingValue = "true")
public class ModelPropsDaoImpl implements ModelPropsDao{

	@Autowired
	private MongoDatabase db;
	
	@Value("${mongoDB.modelprops.collection}")
	private String collectionName;
	
	private MongoCollection<Document> collection;
	
	@PostConstruct
	public void init() {
		collection = db.getCollection(collectionName);
	}
	
	@Override
	public FindIterable<Document> get() {
		FindIterable<Document> documents = collection.find();
		return documents;
	}
	
	@Override
	public void save(String modelId, String modelProps)  throws ApiHubException  {
		try {
		 Document doc = new Document("modelId", modelId)
                 .append("modelProps", modelProps);

		 collection.replaceOne(
		            Filters.eq("modelId", modelId),  // Filter by modelId
		            doc,                             // New document to insert or replace with
		            new ReplaceOptions().upsert(true)
		        );
         
		}catch (Exception e) {
			throw DatabaseException.fromMongoException(e, "Unable to save properties ");
		}
		
	}

}

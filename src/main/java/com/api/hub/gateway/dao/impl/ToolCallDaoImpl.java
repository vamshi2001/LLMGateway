package com.api.hub.gateway.dao.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.api.hub.gateway.dao.ToolCallDao;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.annotation.PostConstruct;

@Repository
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
	public FindIterable<Document> get() {
		FindIterable<Document> documents = collection.find();
		return documents;
	}

}

package com.api.hub.gateway.dao;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public interface ModelPropsDao {

	FindIterable<Document> get();
}

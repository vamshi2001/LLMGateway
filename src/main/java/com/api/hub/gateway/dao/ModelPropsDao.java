package com.api.hub.gateway.dao;

import org.bson.Document;

import com.api.hub.exception.ApiHubException;
import com.mongodb.client.FindIterable;

public interface ModelPropsDao {

	FindIterable<Document> get();

	void save(String modelId, String modelProps) throws ApiHubException;
}

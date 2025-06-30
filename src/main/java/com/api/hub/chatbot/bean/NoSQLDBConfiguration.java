package com.api.hub.chatbot.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
public class NoSQLDBConfiguration {

	@Value("${mongoDB.url}")
	private String mongoDBUrl;
	@Value("${mongoDB.DBName}")
	private String mongoDbName;
	@Value("${mongoDB.maxConnections}")
	private Integer mongoMaxConnnections;
	@Value("${mongoDB.minConnections}")
	private Integer mongoMinConnections;
	@Value("${mongoDB.maxWaitTime}")
	private Integer mongoMaxWaitTime;
	@Value("${mongoDB.connectionTimeOut}")
	private Integer mongoConnectionTimeOut;
	@Value("${mongoDB.readTimeOut}")
	private Integer mongoReadTimeOut;
	@Bean
	public MongoDatabase getMongoConnection() {
		MongoClientSettings settings = MongoClientSettings.builder()
	            .applyConnectionString(new com.mongodb.ConnectionString(mongoDBUrl))
	            .applyToConnectionPoolSettings(builder -> builder
	                .maxSize(mongoMaxConnnections.intValue())             // Maximum connections in the pool
	                .minSize(mongoMinConnections.intValue())             // Minimum idle connections
	                .maxWaitTime(mongoMaxWaitTime.intValue(), java.util.concurrent.TimeUnit.MILLISECONDS) // Wait time for an available connection
	            )
	            .applyToSocketSettings(builder -> builder
	                .connectTimeout(mongoConnectionTimeOut, java.util.concurrent.TimeUnit.SECONDS) // Connection timeout
	                .readTimeout(mongoReadTimeOut, java.util.concurrent.TimeUnit.SECONDS)    // Socket read timeout
	            )
	            .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build()) // Optional for MongoDB Atlas
	            .build();

		 MongoClient mongoClient = MongoClients.create(settings);
		 return mongoClient.getDatabase(mongoDbName);
		  
	}
}

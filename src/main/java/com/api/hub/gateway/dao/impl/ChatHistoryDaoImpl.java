package com.api.hub.gateway.dao.impl;

import java.util.Date;
import java.util.LinkedList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.exception.InputException;
import com.api.hub.gateway.dao.ChatHistoryDao;
import com.api.hub.gateway.model.ChatHistory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import jakarta.annotation.PostConstruct;

import static com.mongodb.client.model.Sorts.descending;

@Repository
@ConditionalOnProperty(name = "mongoDB.chathistory.enable", havingValue = "true")
public class ChatHistoryDaoImpl implements ChatHistoryDao{
	
	@Autowired
	private MongoDatabase db;
	
	@Value("${mongoDB.chatHistory.collection}")
	private String collectionName;
	
	private MongoCollection<Document> collection;
	
	@PostConstruct
	public void init() {
		collection = db.getCollection(collectionName);
	}

	@Override
	public LinkedList<ChatHistory> get(String bSessionId, int nChats) throws ApiHubException {
		
		if (bSessionId == null || bSessionId.trim().isEmpty()) {
	        throw new InputException("1002-mongodb-gateway", "bSessionId is required", "Chat history fetch failed: session ID missing");
	    }

	    if (nChats <= 0 ) {
	        throw new InputException("1002-mongodb-gateway", "nChats must be between 1 and 1000", "Invalid number of chat entries requested");
	    }
		try {
			Bson sessionFilter = Filters.eq("bSessionId", bSessionId);
			
			FindIterable<Document> recentChats = collection
					.find(sessionFilter)
					.sort(descending("entryTime"))
					.limit(nChats);
			
			LinkedList<ChatHistory> chatHistories = new LinkedList<>();

	        for (Document doc : recentChats) {
	            ChatHistory history = new ChatHistory(
	                doc.getString("userMessage"),
	                doc.getString("aiMessage"),
	                doc.getDate("entryTime"),
	                doc.getString("bSessionId")
	            );
	            chatHistories.add(history);
	        }
	        
			return chatHistories;
		}catch (Exception e) {
			throw DatabaseException.fromMongoException(e, "Unable to retrieve user Chat History for session " + bSessionId);
		}
		
	}

	@Override
	public void save(ChatHistory currentChat)  throws ApiHubException  {
		try {
		 Document doc = new Document("userMessage", currentChat.getUserMessage())
                 .append("aiMessage", currentChat.getAiMessage())
                 .append("entryTime", new Date())
                 .append("bSessionId", currentChat.getBSessionId());

         collection.insertOne(doc);
         
		}catch (Exception e) {
			throw DatabaseException.fromMongoException(e, "Unable to save user Chat History for session " + currentChat.getBSessionId());
		}
		
	}

}

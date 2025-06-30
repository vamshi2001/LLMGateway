package com.api.hub.chatbot.daoImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.api.hub.chatbot.cache.MongoDBCollections;
import com.api.hub.chatbot.dao.BaseDao;
import com.api.hub.chatbot.entity.BusinessEntity;
import com.api.hub.chatbot.pojo.ChatBotException;
import com.api.hub.chatbot.pojo.ChatDataHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Repository("BusinessData")
public class BusinessDataDaoImpl<T> implements BaseDao<T>{
	
	@Autowired
	private MongoDatabase db;
	
	@Autowired
	private MongoDBCollections collections;
	
	@Value("${mongoDB.vector.name}")
	private String vectorName;
	@Value("${mongoDB.vector.keyName}")
	private String vectorKeyName;
	@Value("${mongoDB.vector.nDocs}")
	private Integer numberOfDocsToSearch;
	@Value("${mongoDB.vector.limit}")
	private Integer resultsLimit;
	
	private static final String taskName = "BusinessData";
	
	@Override
	public List<T> get(T entity) throws Exception {
		try {
			//about|services|products
			String tenentId = ChatDataHolder.get().getTenentId();
			String collectionName = tenentId + "-" + taskName;
			String[] userQuery = ((BusinessEntity) entity).getCategory().split(",");
			List<String> categories = new ArrayList<String>();
			for(String cat : userQuery) {
				categories.add(cat.trim());
			}
			Document filterCriteria = null;
			if(userQuery != null) {
				filterCriteria = new Document("category", 
						new Document("$in", categories));
			}
		
			MongoCollection<Document> collection = db.getCollection(collections.getCollection(collectionName));
			ObjectMapper objectMapper = new ObjectMapper();
			AggregateIterable<Document> result = null;
	        if(filterCriteria != null) {
	        	result = collection.aggregate(Arrays.asList(

		        	    // Step 2: Perform vector search only on filtered documents
		        	    new Document("$vectorSearch", new Document()
		                .append("index", vectorName)  // Name of the vector index
		                .append("path", vectorKeyName)  // Path to the embedding array in documents
		                .append("queryVector", ((BusinessEntity) entity).getEmbedding())  // Input vector
		                .append("numCandidates", numberOfDocsToSearch)  // Number of candidates to consider
		                .append("limit", resultsLimit)  // Return top 5 results
		                .append("filter", filterCriteria) // Step 1: Filter documents based on criteria
		            )
		        ));
	        }else {
	        	result = collection.aggregate(Arrays.asList(
		        		// Step 1: Filter documents based on criteria
		        	     //new Document("$match", filterCriteria),

		        	    // Step 2: Perform vector search only on filtered documents
		        	    new Document("$vectorSearch", new Document()
		                .append("index", vectorName)  // Name of the vector index
		                .append("path", vectorKeyName)  // Path to the embedding array in documents
		                .append("queryVector", ((BusinessEntity) entity).getEmbedding())  // Input vector
		                .append("numCandidates", numberOfDocsToSearch)  // Number of candidates to consider
		                .append("limit", resultsLimit)  // Return top 5 results
		            )
		        ));
	        }
	        
	        
	        List<BusinessEntity> businessData = new ArrayList<BusinessEntity>();
	        for (Document doc : result) {
	        	BusinessEntity data = objectMapper.readValue(doc.toJson(), BusinessEntity.class);
	
	            businessData.add(data);
	        }
	        return (List<T>) businessData;
		}
		catch (Exception e) {
            throw new ChatBotException("Error fetching business data using vector search", e.getMessage(), 500);
        }
	}

	@Override
	public <T> boolean save(T entity) {
		// TODO Auto-generated method stub
		return false;
	}

}

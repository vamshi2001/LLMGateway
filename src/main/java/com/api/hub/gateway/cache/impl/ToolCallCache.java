package com.api.hub.gateway.cache.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.LLMToolCallUtility;
import com.api.hub.gateway.cache.AbstractCacheOperations;
import com.api.hub.gateway.dao.ToolCallDao;
import com.api.hub.gateway.model.TollCallData;
import com.api.hub.gateway.provider.helper.impl.ModelPropertiesHandler;
import com.mongodb.client.FindIterable;

import dev.langchain4j.agent.tool.ToolSpecification;

@Component("ToolCallCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class ToolCallCache  extends AbstractCacheOperations<String,TollCallData>{

	private final Map<String, String> fileSizeMap = new ConcurrentHashMap<>();
	
	@Autowired
	ToolCallDao dao;

	@Override
	public boolean source() {
		
		FindIterable<Document>  files = dao.get();
	    for (Document doc : files) {
	    	
	        String toolName = doc.getString("toolName");
	        String toolDescription = doc.getString("toolDescription");
	        List<String> supportedTopics = doc.getList("supportedTopics", String.class, new ArrayList<String>());
	        boolean enabled = doc.getBoolean("enabled", false);
	        String base64EncodedProps = doc.getString("toolArguments");
	        String endPoint = doc.getString("endPoint");
	        String previousData = fileSizeMap.get(toolName);

	        if (!fileSizeMap.containsKey(toolName) || previousData == null || previousData.equals(base64EncodedProps)) {
	        	
	        	TollCallData toolData = new TollCallData();
	        	toolData.setEnabled(enabled);toolData.setSupportedTopics(supportedTopics);toolData.setToolArguments(base64EncodedProps);
	        	toolData.setToolDescription(toolDescription);
	        	toolData.setToolName(toolName);
	        	toolData.setEndPoint(endPoint);
	        	Base64.Decoder decoder = Base64.getDecoder();
	    		
	            byte[] bytes = decoder.decode(base64EncodedProps);
	            String schemaText = new String(bytes);
	            try {
	            	ToolSpecification toolSpecification= LLMToolCallUtility.toolSpecificationFrom(toolName, toolDescription, schemaText);
	            	toolData.setToolSpecification(toolSpecification);
	                data.put(toolName, toolData);
	                fileSizeMap.put(toolName, base64EncodedProps);
	                
	                System.out.println("Loaded or reloaded: " + toolName);
	            } catch (Exception e) {
	            	 System.err.println("Unexpected error occured while loading properties from: " + toolName);
		             e.printStackTrace();
	            }
	        }
	    }
		return false;
	}

	@Override
	public boolean sink(String key) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean clear() {
		// TODO Auto-generated method stub
		return true;
	}

	@Value("${cache.tollcall.minrefreshtime.ms}")
	private long minrefreshtime;
	
	@Override
	public long minRefreshTime() {
		 return minrefreshtime;
	}
}

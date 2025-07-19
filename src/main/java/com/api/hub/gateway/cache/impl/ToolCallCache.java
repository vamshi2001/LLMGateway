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
import com.api.hub.gateway.service.impl.LLMRequestHelper;
import com.mongodb.client.FindIterable;

import dev.langchain4j.agent.tool.ToolSpecification;

@Component("ToolCallCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class ToolCallCache  extends AbstractCacheOperations<String,TollCallData>{

	private final Map<String, String> fileSizeMap = new ConcurrentHashMap<>();
	
	@Autowired
	ToolCallDao dao;
	
	@Autowired
	LLMRequestHelper helper;

	@Override
	public boolean source() {
		
		boolean changed = false;
		List<TollCallData>  files = dao.get();
	    for (TollCallData doc : files) {
	    	
	        
	        String previousData = fileSizeMap.get(doc.getToolName());

	        if (!fileSizeMap.containsKey(doc.getToolName()) || previousData == null || previousData.equals(doc.getToolArguments())) {
	        	changed = true;
	        	
	        	Base64.Decoder decoder = Base64.getDecoder();
	    		
	            byte[] bytes = decoder.decode(doc.getToolArguments());
	            String schemaText = new String(bytes);
	            try {
	            	ToolSpecification toolSpecification= LLMToolCallUtility.toolSpecificationFrom(doc.getToolName(), doc.getToolDescription(), schemaText);
	            	doc.setToolSpecification(toolSpecification);
	                data.put(doc.getToolName(), doc);
	                fileSizeMap.put(doc.getToolName(), doc.getToolArguments());
	                
	                System.out.println("Loaded or reloaded: " + doc.getToolName());
	            } catch (Exception e) {
	            	 System.err.println("Unexpected error occured while loading properties from: " + doc.getToolName());
		             e.printStackTrace();
	            }
	        }
	    }
	    if(changed) {
	    	helper.compute();
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

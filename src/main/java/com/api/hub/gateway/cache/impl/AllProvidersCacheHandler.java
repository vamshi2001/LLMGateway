package com.api.hub.gateway.cache.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.AbstractCacheOperations;
import com.api.hub.gateway.dao.ModelPropsDao;
import com.api.hub.gateway.provider.helper.impl.ModelPropertiesHandler;
import com.mongodb.client.FindIterable;

@Component("AllLLMCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class AllProvidersCacheHandler extends AbstractCacheOperations<String,Properties>{

	private final Map<String, String> fileSizeMap = new ConcurrentHashMap<>();
	
	@Autowired
	ModelPropertiesHandler handler;
	
	@Autowired
	ModelPropsDao dao;

	@Override
	public boolean source() {
		/*
		 * File folder = new File(pathToScan); File[] files = folder.listFiles((dir,
		 * name) -> name.endsWith(".properties"));
		 * 
		 * if (files == null) return false;
		 */
		FindIterable<Document>  files = dao.get();
	    for (Document doc : files) {
	        String filename = doc.getString("modelId");
	        String base64EncodedProps = doc.getString("modelProps");
	        
	        String previousData = fileSizeMap.get(filename);

	        if (!fileSizeMap.containsKey(filename) || previousData == null || previousData.equals(base64EncodedProps)) {
	        	Base64.Decoder decoder = Base64.getDecoder();
	    		
	            byte[] bytes = decoder.decode(base64EncodedProps);
	            String schemaText = new String(bytes);
	            try {
	                Properties props = new Properties();
	                props.load(new StringReader(schemaText));
	                data.put(filename, props);
	                fileSizeMap.put(filename, base64EncodedProps);
	                handler.update(props, filename);
	                System.out.println("Loaded or reloaded: " + filename);
	            } catch (IOException e) {
	                System.err.println("Failed to load properties from: " + filename);
	                e.printStackTrace();
	            }catch (Exception e) {
	            	 System.err.println("Unexpected error occured while loading properties from: " + filename);
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
	
	@Value("${cache.llm.minrefreshtime.ms}")
	private long minrefreshtime;
	
	@Override
	public long minRefreshTime() {
		 return minrefreshtime;
	}
    
}

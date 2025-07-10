package com.api.hub.gateway.cache.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.AbstractCacheOperations;
import com.api.hub.gateway.dao.ModelDao;
import com.api.hub.gateway.model.Model;

@Component("AllLLMModelsCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class AllModelMetaDataCache extends AbstractCacheOperations<String,Model> {

	@Autowired
	private ModelDao dao;
	
	@Override
	public boolean source() {
		
		List<Model> modelList = dao.get();
		
		for(Model model : modelList) {
			if(data.containsKey(model.getModelId())) {
				data.replace(model.getModelId(), model);
			}else {
				data.put(model.getModelId(), model);
			}
		}
		
		return true;
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

	@Value("${cache.llm.metadata.minrefreshtime.ms}")
	private long minrefreshtime;
	
	@Override
	public long minRefreshTime() {
		 return minrefreshtime;
	}
}

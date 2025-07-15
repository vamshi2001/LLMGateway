package com.api.hub.gateway.cache.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.cache.AbstractCacheOperations;
import com.api.hub.gateway.dao.WebSearchConfigDao;
import com.api.hub.gateway.model.JsoupExtractionConfig;

@Component("JsoupExtractionConfigCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class JsoupExtractionConfigCache  extends AbstractCacheOperations<String,JsoupExtractionConfig>{

	@Autowired
	private WebSearchConfigDao dao;
	
	@Override
	public boolean source() {
		try {
			List<JsoupExtractionConfig> configList = dao.get();
			for(JsoupExtractionConfig config : configList) {
				data.put(config.getHost(), config);
			}
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean sink(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clear() {
		// TODO Auto-generated method stub
		return false;
	}

	@Value("${cache.jsoupextraction.minrefreshtime.ms}")
	private long minrefreshtime;
	
	@Override
	public long minRefreshTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}

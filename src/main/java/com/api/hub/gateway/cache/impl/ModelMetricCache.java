package com.api.hub.gateway.cache.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.AbstractCacheOperations;
import com.api.hub.gateway.dao.ModelMetricDao;
import com.api.hub.gateway.model.ModelMetric;

@Component("AllLLMModelsMetricCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class ModelMetricCache extends AbstractCacheOperations<String,ModelMetric>  {

	@Autowired
	private ModelMetricDao dao;
	
	@Value("${cache.llm.mteric.restTime}")
	private String restTime;
	
	private boolean fistCall = true;
	
	private List<ModelMetric> metricsToSave = new ArrayList<ModelMetric>();
	
	@Override
	public boolean source() {
		if(fistCall) {
			List<ModelMetric> array = dao.get();
			for(ModelMetric model : array) {
				if(data.containsKey(model.getModelId())) {
					data.replace(model.getModelId(), model);
				}else {
					data.put(model.getModelId(), model);
				}
			}
		}
		fistCall = false;
		return false;
	}

	@Override
	public boolean sink(String key) {
		ModelMetric model  = data.get(key);
		model.setCurrentDate(new Date());
		dao.save(model);
		return false;
	}

	@Override
	public boolean clear() {
		
		for(ModelMetric metric : metricsToSave) {
			if(metric.getCurrentActiveRequest().get() == 0) {
				
				dao.save(metric);
				metricsToSave.remove(metric);
			}
		}
		
		LocalTime cutime = LocalTime.parse(restTime);
		LocalTime now = LocalTime.now();
		boolean skip = false;
        if(now.isAfter(cutime)) {
        	for(ModelMetric model : data.values()) {
        		ReentrantReadWriteLock lock = model.getLock();
        		try {
        			lock.writeLock().lock();
        			ModelMetric tempModel = new ModelMetric();
        			
        			model.setCurrentDate(new Date());
        			tempModel.setCurrentInputTokenConsumedPerMonth(model.getCurrentInputTokenConsumedPerMonth().get());
        			tempModel.setCurrentOutputTokenConsumedPerMonth(model.getCurrentOutputTokenConsumedPerMonth().get());
        			tempModel.setRequestPerMonth(model.getRequestPerDay().getAndSet(0));
        			tempModel.setFailureInterval(0);
        			tempModel.setTotalFailuresToday(0);
        			
        			tempModel.setModelId(model.getModelId());
        			
        			data.replace(model.getModelId(), tempModel);
        			
        			if(model.getCurrentActiveRequest().get() == 0) {
        				dao.save(model);
        			}else {
        				metricsToSave.add(model);
        			}
            		//dao.save(tempModel);
        		}finally {
        			lock.writeLock().unlock();
				}
    		}
        	skip = true;
        }
        
        if(!skip) {
	    	for(String key : data.keySet()) {
				sink(key);
			}
        }
        
		return false;
	}
	
	@Value("${cache.llm.mteric.minrefreshtime.ms}")
	private long minrefreshtime;

	@Override
	public long minRefreshTime() {
		// TODO Auto-generated method stub
		return minrefreshtime;
	}

}

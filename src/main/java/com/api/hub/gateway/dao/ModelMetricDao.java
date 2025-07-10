package com.api.hub.gateway.dao;

import java.util.List;

import com.api.hub.gateway.model.ModelMetric;

public interface ModelMetricDao {
	
	void save(ModelMetric metric);
	
	List<ModelMetric> get();
	
}

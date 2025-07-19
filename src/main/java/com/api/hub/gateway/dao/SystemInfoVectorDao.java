package com.api.hub.gateway.dao;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.RagModel;

public interface SystemInfoVectorDao {


	public String get(RagModel model) throws ApiHubException;
	
	void save(GatewayRequest request) throws ApiHubException;
}

package com.api.hub.gateway.service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.GatewayRequest;

public interface LLMGatewayService {

	String getResponse(GatewayRequest req) throws ApiHubException;
}

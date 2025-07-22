package com.api.hub.gateway.service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.TollCallData;

public interface ToolCallService {

	String getResponse(GatewayRequest request, String body, String toolName, String userId) throws ApiHubException;
}

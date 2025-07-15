package com.api.hub.gateway.service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;

public interface ModelSelecter {

	GatewayResponse getResponse(GatewayRequest request) throws ApiHubException;
}

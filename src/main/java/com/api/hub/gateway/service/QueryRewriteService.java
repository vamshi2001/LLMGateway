package com.api.hub.gateway.service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.GatewayRequest;

public interface QueryRewriteService {

	String rewriteQuery(GatewayRequest request) throws ApiHubException;
}

package com.api.hub.gateway.service;

import java.util.List;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;

public interface SearchService {

	String getData(GatewayRequest request) throws ApiHubException;
}

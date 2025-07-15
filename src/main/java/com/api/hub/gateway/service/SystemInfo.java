package com.api.hub.gateway.service;

import java.util.List;
import java.util.concurrent.Future;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;

public interface SystemInfo {

	List<String> getAdditionalInfo(GatewayRequest request) throws ApiHubException;
}

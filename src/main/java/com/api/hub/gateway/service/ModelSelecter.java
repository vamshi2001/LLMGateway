package com.api.hub.gateway.service;

import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.Model;

import dev.langchain4j.data.message.AiMessage;

public interface ModelSelecter {

	AiMessage getResponse(GatewayRequest request);
}

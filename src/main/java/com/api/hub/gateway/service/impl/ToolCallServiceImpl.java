package com.api.hub.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.api.hub.auth.AutheticationHandler;
import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.BackOffFactory;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.TollCallData;
import com.api.hub.gateway.service.ToolCallService;
import com.api.hub.http.HttpHandler;
import com.api.hub.http.url.HostReslover;

import jakarta.annotation.PostConstruct;

@Component
public class ToolCallServiceImpl implements ToolCallService{

	@Autowired
	private Environment env;
    
    @Autowired
    @Qualifier("SpringRestTempletHttpHandlerImpl")
    private HttpHandler handler;
    
    @Autowired
    @Qualifier("SimpleNOPCircularQueueHostHandler")
    private HostReslover hostReslover;
    
    @Autowired
    @Qualifier("NOPAuthenticationHandler")
    private AutheticationHandler autheticationHandler;
    
    @Autowired
	@Qualifier("ToolCallCache")
	Cache<String,TollCallData> toolCallCache;
    
    @PostConstruct
    public void init() {
    	handler.init("toolcall"  );
    	hostReslover.init("toolcall"  );
    	autheticationHandler.init("toolcall"  );
    }

    private HttpHeaders defaultHeaders(String userId) throws ApiHubException {
        HttpHeaders headers = new HttpHeaders();;
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("UserId", userId);
        return headers;
    }
    
	public String getResponse(GatewayRequest request, String body, String toolName, String userId) throws ApiHubException{
		
		TollCallData data = toolCallCache.get(toolName);
		ResponseEntity<String> res = handler.getResponse(
       		 handler.sendRequest(handler.createRequest(String.class, String.class)
       			    	.setPathParams(data.getEndPoint())
       			    	.setSpringHeaders(defaultHeaders(userId))
       			    	.setRequestBody(body)
       			    	.setHttpMethod(HttpMethod.POST)
       			    	.setAuthetication(autheticationHandler)
       			    	.setHostReslover(hostReslover)
       			    	.setBackoff(BackOffFactory.createBackOff(env, "toolcall"))
       				 )
       		 
       		 ).getResponse();
		return res.getBody();
	}
}

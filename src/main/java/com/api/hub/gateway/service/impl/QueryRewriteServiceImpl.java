package com.api.hub.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.GenericException;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.dao.PromptVersionDao;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.PersonaProperties;
import com.api.hub.gateway.service.QueryRewriteService;

@Service
public class QueryRewriteServiceImpl implements QueryRewriteService{

	@Autowired
	@Qualifier("PersonaPropsCache")
	private Cache<String, PersonaProperties> personaProps;
	
	@Autowired
	private PromptVersionDao promptDao;
	
	@Autowired
	private DefaultModelSelecter processor;

	@Override
	public String rewriteQuery(GatewayRequest request) throws ApiHubException {
		try {
			String prompt=  promptDao.getPrompt(request.getPersona(), "QueryRewrite", request.getUserMessage());
			GatewayRequest tempRequest = new GatewayRequest();
			
			tempRequest.setPersona("QueryRewrite");
			tempRequest.setPersonaProps(personaProps.get(tempRequest.getPersona()));
			tempRequest.setUserMessage(request.getUserMessage());
			tempRequest.setSystemMessage(prompt);
			tempRequest.setChatHistory(request.getChatHistory());
			tempRequest.setUseChatHistory(true);
			GatewayResponse response = processor.getResponse(tempRequest);
			
			return response.getChatResponse().aiMessage().text();
		} catch(ApiHubException e) {
			throw e;
		}catch (Exception e) {
			throw new GenericException("9001-queryrewrite-gateway", e.getMessage(), "unable to rewrite user query");
		}
	}

}

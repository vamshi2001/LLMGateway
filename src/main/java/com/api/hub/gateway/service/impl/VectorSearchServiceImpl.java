package com.api.hub.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.GenericException;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.dao.SystemInfoVectorDao;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.PersonaProperties;
import com.api.hub.gateway.model.RagModel;
import com.api.hub.gateway.service.SearchService;

import dev.langchain4j.data.embedding.Embedding;

@Component("vectorSearchService")
public class VectorSearchServiceImpl implements SearchService{

	@Autowired
	@Qualifier("PersonaPropsCache")
	private Cache<String, PersonaProperties> personaProps;
	
	@Autowired
	private SystemInfoVectorDao dao;
	
	@Autowired
	private DefaultModelSelecter processor;
	
	@Override
	public String getData(GatewayRequest request) throws ApiHubException {
		try {
			GatewayRequest tempRequest = new GatewayRequest();
			tempRequest.setUserMessage(request.getUserMessage());
			tempRequest.setEmbed(true);
			tempRequest.setPersona("default");
			tempRequest.setPersonaProps(personaProps.get(tempRequest.getPersona()));
			GatewayResponse response = processor.getResponse(tempRequest);
			String vectorResponse = "";
			for(Embedding embed : response.getEmbeddingResponse().content()) {
				RagModel rag = new RagModel();
				rag.setQueryVector(embed);
				rag.setPersona(request.getPersona());
				String str = dao.get(rag);
				if(str != null) {
					vectorResponse += str;
				}
			}
			
			return vectorResponse;
		}catch (Exception e) {
			throw new GenericException("9001-websearch-gateway", e.getMessage(), "unable to retrieve information from websearch");
		}
	}

}

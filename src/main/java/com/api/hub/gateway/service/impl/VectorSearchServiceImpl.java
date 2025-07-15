package com.api.hub.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.GenericException;
import com.api.hub.gateway.dao.SystemInfoVectorDao;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.RagModel;
import com.api.hub.gateway.service.SearchService;

import dev.langchain4j.data.embedding.Embedding;

@Component("VectorSearchServiceImpl")
public class VectorSearchServiceImpl implements SearchService{

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

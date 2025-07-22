package com.api.hub.gateway.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.GenericException;
import com.api.hub.gateway.Utility;
import com.api.hub.gateway.constants.RagFlowType;
import com.api.hub.gateway.dao.SystemInfoVectorDao;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.RagModel;
import com.api.hub.gateway.service.SearchService;
import com.api.hub.gateway.service.SystemInfo;

@Service
public class SystemInfoImpl implements SystemInfo{

	@Autowired
	@Qualifier("websearch")
	private SearchService search;
	
	@Autowired
	@Qualifier("vectorSearchService")
	private SearchService vectorSearch;
	
	@Override
	public List<String> getAdditionalInfo(GatewayRequest request) throws ApiHubException {
		List<String> systemInfo = new ArrayList<String>();
		try {
			List<RagFlowType> ragFlows = Utility.ragFlowType(request.getPersonaProps().getRagSource());
			/*
			 * List<ChatHistory> data = chatHistory.get(); if(data == null) { data = new
			 * ArrayList<ChatHistory>(); }
			 */
			for(RagFlowType ragFlow : ragFlows) {
				if(ragFlow.equals(RagFlowType.WEB_SEARCH)) {
					systemInfo.add(search.getData(request));
				}else if(ragFlow.equals(RagFlowType.VECTOR_SEARCH)){
					systemInfo.add(vectorSearch.getData(request));
				}
			}
		}catch (ApiHubException e) {
			throw e;
		}catch (Exception e) {
			throw new GenericException("9001-rag-gateway", e.getMessage(), "unable to retrieve information from source" + request.getPersonaProps().getRagSource());
		}
		
		return systemInfo;
	}

}

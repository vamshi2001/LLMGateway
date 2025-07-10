package com.api.hub.gateway.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.hub.gateway.Utility;
import com.api.hub.gateway.constants.RagFlowType;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.service.SearchService;
import com.api.hub.gateway.service.SystemInfo;

@Service
public class SystemInfoImpl implements SystemInfo{

	@Autowired
	@Qualifier("websearch")
	private SearchService search;
	
	@Override
	public List<String> getAdditionalInfo(String ragSource, String userMessage, Future<List<ChatHistory>> chatHistory) throws Exception {
		
		List<String> systemInfo = new ArrayList<String>();
		
		List<RagFlowType> ragFlows = Utility.ragFlowType(ragSource);
		List<ChatHistory> data = chatHistory.get();
		if(data == null) {
			data = new ArrayList<ChatHistory>();
		}
		for(RagFlowType ragFlow : ragFlows) {
			if(ragFlow.equals(RagFlowType.WEB_SEARCH)) {
				systemInfo.add(search.getData(userMessage,data));
			}
		}
			
		
		return systemInfo;
	}

}

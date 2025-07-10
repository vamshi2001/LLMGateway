package com.api.hub.gateway.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.InternalServerException;
import com.api.hub.gateway.LLMToolCallUtility;
import com.api.hub.gateway.Utility;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.TollCallData;
import com.api.hub.gateway.service.ChatHistoryService;
import com.api.hub.gateway.service.LLMGatewayService;
import com.api.hub.gateway.service.SystemInfo;
import com.api.hub.gateway.service.ToolCallService;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;

@Service
public class LLMGatewayServiceImpl implements LLMGatewayService{

	@Autowired
	private ChatHistoryService chatHistory;
	
	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private DefaultModelSelecter processor;
	
	@Autowired
	@Qualifier("ToolCallCache")
	Cache<String,TollCallData> toolCallCache;
	
	@Autowired
	ToolCallService toolService;
	
	@Override
	public String getResponse(GatewayRequest req) throws ApiHubException {
		
		Future<List<ChatHistory>> history = CompletableFuture.supplyAsync(() -> {
			try {
				return chatHistory.getChatHistory(Utility.getChatType(req), req.getBotSessionid(), req.getMaxChatHistory());
			} catch (ApiHubException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		});
		
		req.setChatHistory(history);
		if(req.isUseRAG()) {
			try {
				List<String> info = systemInfo.getAdditionalInfo(req.getRagSource(),req.getUserMessage(),req.getChatHistory());
				if(info != null && info.size() > 0) {
					req.getAdditionalInfo().addAll(info);
				}else {
					throw new InternalServerException("8001-rag-gateway", "in rag, system information is empty", "Failed to fetch System Information");
				}
			} catch (ApiHubException e) {
				throw e;
			}
		}
		AiMessage msg = null;
		do {
			if (msg != null && msg.hasToolExecutionRequests()){
				for( ToolExecutionRequest toolRequest: msg.toolExecutionRequests()) {
					req.getAdditionalInfo().add(handleToolCall(req,toolRequest.arguments(), toolCallCache.get(toolRequest.name()),"vamshi"));
				}
			}
			msg = processor.getResponse(req);
			if(msg != null && msg.text() != null && msg.text().length() > 0) {
				ChatHistory his = new ChatHistory(req.getUserMessage(), msg.text(), null, req.getBotSessionid());
				try {
					chatHistory.save(Utility.getChatType(req), his);
				}catch (ApiHubException e) {
					//loggers
				}
				
			}
		}while(msg != null && msg.hasToolExecutionRequests());
		
		
		return msg!=null? msg.text(): "unable to process request";
	}
	
	private String handleToolCall(GatewayRequest request, String body, TollCallData data, String userId) throws ApiHubException {
		try {
			return toolService.getResponse(request, body, data, userId);
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}

}

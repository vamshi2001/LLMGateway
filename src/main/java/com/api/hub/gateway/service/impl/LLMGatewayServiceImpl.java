package com.api.hub.gateway.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.hub.exception.ApiHubException;
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
	public String getResponse(GatewayRequest req) {
		
		Future<List<ChatHistory>> history = CompletableFuture.supplyAsync(() -> {
			return chatHistory.getChatHistory(Utility.getChatType(req), req.getBotSessionid(), req.getMaxChatHistory());
		});
		
		req.setChatHistory(history);
		if(req.isUseRAG()) {
			try {
				List<String> info = systemInfo.getAdditionalInfo(req.getRagSource(),req.getUserMessage(),req.getChatHistory());
				if(info != null && info.size() > 0) {
					req.getAdditionalInfo().addAll(info);
				}
			} catch (Exception e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		AiMessage msg = null;
		do {
			if (msg != null && msg.hasToolExecutionRequests()){
				for( ToolExecutionRequest toolRequest: msg.toolExecutionRequests()) {
					String toolResult = handleToolCall(req,toolRequest.arguments(), toolCallCache.get(toolRequest.name()),"vamshi");
				}
			}
			msg = processor.getResponse(req);
			if(msg != null && msg.text() != null && msg.text().length() > 0) {
				ChatHistory his = new ChatHistory(req.getUserMessage(), msg.text(), null, req.getBotSessionid());
				chatHistory.save(Utility.getChatType(req), his);
			}
		}while(msg != null && msg.hasToolExecutionRequests());
		
		
		
		return msg.text();
	}
	
	private String handleToolCall(GatewayRequest request, String body, TollCallData data, String userId) {
		try {
			return toolService.getResponse(request, body, data, userId);
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}

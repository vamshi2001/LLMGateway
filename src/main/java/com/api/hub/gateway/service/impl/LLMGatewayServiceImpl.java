package com.api.hub.gateway.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.InternalServerException;
import com.api.hub.gateway.Utility;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.service.ChatHistoryService;
import com.api.hub.gateway.service.LLMGatewayService;
import com.api.hub.gateway.service.QueryRewriteService;
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
	private ToolCallService toolService;
	
	@Autowired
	private QueryRewriteService service;
	
	@Override
	public String getResponse(GatewayRequest req) throws ApiHubException {
		
		if(req.getPersonaProps().isChatHistoryEnabled()) {
			Future<List<ChatHistory>> history = CompletableFuture.supplyAsync(() -> {
				try {
					return chatHistory.getChatHistory(Utility.getChatType(req), req.getBotSessionid());
				} catch (ApiHubException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			});
			req.setChatHistory(history);
		}
		
		if(req.getPersonaProps().isQueryRewriteEnabled()) {
			String finialQuery = service.rewriteQuery(req);
			if(finialQuery != null && !finialQuery.isBlank()) {
				req.setUserMessage(finialQuery);
				req.setUseChatHistory(false);
			}
		}
		
		if(req.getPersonaProps().isRagEnabled()) {
			try {
				List<String> info = systemInfo.getAdditionalInfo(req);
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
					req.getAdditionalInfo().add(handleToolCall(req,toolRequest.arguments(), toolRequest.name(),"vamshi"));
				}
			}
			GatewayResponse res = processor.getResponse(req);
			
			if(res.getChatResponse() != null) {
				if(msg != null && msg.text() != null && msg.text().length() > 0) {
					ChatHistory his = new ChatHistory(req.getUserMessage(), msg.text(), null, req.getBotSessionid());
					try {
						chatHistory.save(Utility.getChatType(req), his);
					}catch (ApiHubException e) {
						//loggers
					}
				}
			}
			msg = res.getChatResponse().aiMessage();
			
		}while(msg != null && msg.hasToolExecutionRequests());
		
		
		return msg!=null? msg.text(): "unable to process request";
	}
	
	private String handleToolCall(GatewayRequest request, String body,String toolName, String userId) throws ApiHubException {
		try {
			return toolService.getResponse(request, body, toolName, userId);
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}

}

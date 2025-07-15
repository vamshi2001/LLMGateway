package com.api.hub.gateway.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.dao.PromptVersionDao;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.TollCallData;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ToolChoice;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LLMRequestHelper {
	
	@Autowired
	PromptVersionDao promptHandler;
	
	@Autowired
	@Qualifier("ToolCallCache")
	Cache<String,TollCallData> toolCallCache;
	
	private volatile Map<String, List<ToolSpecification>> personaMap = new ConcurrentHashMap<String, List<ToolSpecification>>();

	public Request getRequest(GatewayRequest gatewayRequest, ChatType type) throws ApiHubException {
		
		Request req = new Request();
		List<ChatMessage> messages = new ArrayList<>();
		
		if(type.equals(ChatType.CHAT)) {
			if (gatewayRequest.getChatHistory() != null && gatewayRequest.isUseChatHistory()) {
	            try {
	                List<ChatHistory> historyList = gatewayRequest.getChatHistory().get();
	                if (historyList != null && !historyList.isEmpty()) {
	                	for(ChatHistory history : historyList) {
	                		 messages.add(UserMessage.from(history.getUserMessage()));
	                		 messages.add(AiMessage.from(history.getAiMessage()));
	                	}
	                }
	            } catch (InterruptedException | ExecutionException e) {
	                log.error("Error retrieving chat history for OpenAI request {}: {}", gatewayRequest.getRequestId(), e.getMessage());
	                Thread.currentThread().interrupt();
	            }
	        }
			
			messages.add(UserMessage.from(gatewayRequest.getUserMessage()));
			
			if(gatewayRequest.getSystemMessage() != null && !gatewayRequest.getSystemMessage().isBlank()) {
				messages.add(SystemMessage.from(gatewayRequest.getSystemMessage()));
			}else {
				messages.add(SystemMessage.from(promptHandler.getPrompt(gatewayRequest.getPersona(), "Main", gatewayRequest.getUserMessage())));
			}
			 
			if(gatewayRequest.getPersonaProps().isRagEnabled() && gatewayRequest.getAdditionalInfo() != null && gatewayRequest.getAdditionalInfo().size() > 0) {
				for(String str : gatewayRequest.getAdditionalInfo()) {
					 messages.add(SystemMessage.from(str));
				}
			}
			
			if(gatewayRequest.getPersonaProps().isToolCallEnabled()) {
				List<ToolSpecification>  toolSpec = personaMap.get(gatewayRequest.getPersona());
				if(toolSpec != null && toolSpec.size()>0) {
					req.setTools(toolSpec);
					if("auto".equalsIgnoreCase(gatewayRequest.getPersonaProps().getToolChoice())) {
						req.setChoice(ToolChoice.AUTO);
					}else {
						req.setChoice(ToolChoice.REQUIRED);
					}
				}
			}
			
		}
		return null;
	}
	
	public void compute() {
		
		Map<String, List<ToolSpecification>> map = new HashMap<String, List<ToolSpecification>>();
		//List<ToolSpecification> specs = new ArrayList<ToolSpecification>();
        for(String key : toolCallCache.keys()) {
        	
        	for(String persona : toolCallCache.get(key).getSupportedPersona()) {
        		
        		List<ToolSpecification> personaList = map.get(persona);
        		if(personaList == null) {
        			personaList = new ArrayList<ToolSpecification>();
        			map.put(persona, personaList);
        		}
        		
        		personaList.add(toolCallCache.get(key).getToolSpecification());
        	}
        }
        
        personaMap.putAll(map);
	}
	
	@Data
	public class Request{
		
		ToolChoice choice;
		List<ToolSpecification> tools;
		List<ChatMessage> messages;
		
	}
}

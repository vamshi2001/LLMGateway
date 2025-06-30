package com.api.hub.chatbot.integrations.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.hub.chatbot.constants.OllamaModels;
import com.api.hub.chatbot.constants.Prompts;
import com.api.hub.chatbot.dao.BaseDao;
import com.api.hub.chatbot.entity.BusinessEntity;
import com.api.hub.chatbot.integrations.LLMIntegration;
import com.api.hub.chatbot.pojo.Chat;
import com.api.hub.chatbot.pojo.ChatBotException;
import com.api.hub.chatbot.pojo.ChatDataHolder;
import com.api.hub.chatbot.pojo.ChatMetaData;
import com.api.hub.chatbot.pojo.ChatRequest;
import com.api.hub.chatbot.pojo.Message;
import com.api.hub.chatbot.pojo.Vector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("ollama")
public class OllamaAPIHandler implements LLMIntegration{
	
	@Value("${ollamaVectorGeneraterUrl}")
	private String ollamaVectorGeneraterUrl;
	
	@Value("${ollamaInquiryChatUrl}")
	private String ollamaInquiryChatUrl;
	
	@Value("${ollamaInquiryPromptUrl}")
	private String ollamaInquiryPromptUrl;
	
	@Override
	public String messageToUser(List<BusinessEntity> businessdataList, List<Chat> chatHistory, boolean nonBusinessQuestion) throws ChatBotException{
		try {
			StringBuffer dataToBot = new StringBuffer("use this business data to answer user query - ");
			businessdataList.forEach( e -> {
				dataToBot.append(e.getTitle() + " : " + e.getDescription());
			});
			
			List<Message> msgBody = new ArrayList<Message>();
			msgBody.addAll(getSystemPrompts(nonBusinessQuestion));
			msgBody.add(new Message("system", dataToBot.toString()));
			
			chatHistory.forEach( e -> {
				msgBody.add(new Message("user", e.getQuery()));
				if(e.getResponse() != null && !e.getResponse().isBlank()) {
					msgBody.add(new Message("assistant", e.getResponse()));
				}
			});
			
			ChatRequest chatRequest = new ChatRequest(OllamaModels.Lamma3.getDescription(), msgBody, false);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<ChatRequest> requestEntity = new HttpEntity<>(chatRequest, headers);
	        ResponseEntity<String> result = restTemplate.postForEntity(ollamaInquiryChatUrl, requestEntity, String.class);
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<Object,Object> data1 = objectMapper.readValue(result.getBody(), Map.class);
	        
	        String resSTR =(String) ((Map) data1.get("message")).get("content");
	        return resSTR;
		}catch (Exception e) {
			throw new ChatBotException("unable to connect ollama, error while preparing user response", e.getMessage(), 500);
		}
	}

	@Override
	public String getCategories(String query) throws ChatBotException {
		try {
			
			Map<String,Object> requestBody = new HashMap<String, Object>();
			requestBody.put("model", OllamaModels.Lamma3.getDescription());
			requestBody.put("prompt", Prompts.userQueryToCatagotiesPrompt + query);
			requestBody.put("stream", false);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(requestBody, headers);
	        ResponseEntity<String> result = restTemplate.postForEntity(ollamaInquiryPromptUrl, requestEntity, String.class);
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<Object,Object> data1 = objectMapper.readValue(result.getBody(), Map.class);
	        return ((String) data1.get("response")).replace("\r", "").trim().replace("\n", "").replaceAll("[^a-zA-Z\s_,]", "");
		}catch (Exception e) {
			throw new ChatBotException("unable to connect ollama, error while categorizing user query", e.getMessage(), 500);
		}
	}

	@Override
	public String refinePrompt(List<Chat> chatHistory) throws ChatBotException {
		try {
			
			
			/*
			 * List<Message> msgBody = new ArrayList<Message>(); msgBody.add(new
			 * Message("system", Prompts.refinePromptExp)); msgBody.add(new
			 * Message("system", Prompts.userQueryRefinePrompt2));
			 */
			//ChatRequest chatRequest = new ChatRequest(OllamaModels.Lamma3.getDescription(), msgBody, false);
			
			StringBuffer tempPrompt = new StringBuffer();
			if(chatHistory.size()>1) {
				tempPrompt.append("Current Conversation History -\n");
				chatHistory.forEach( e -> {
					
					if(e.getResponse() != null && !e.getResponse().isBlank()) {
						tempPrompt.append("user : " + e.getQuery());
						tempPrompt.append("assistance : " + e.getResponse());
					}else {
						tempPrompt.append("\nUser Query:" + e.getQuery());
					}
				});
			}else {
				tempPrompt.append("User Query:" + chatHistory.get(0).getQuery());
			}
			
			String prompt = Prompts.refinePromptExp + Prompts.userQueryRefinePrompt2 + tempPrompt.toString();
			
			Map<String,Object> requestBody = new HashMap<String, Object>();
			requestBody.put("model", OllamaModels.Lamma3.getDescription());
			requestBody.put("prompt", prompt);
			requestBody.put("stream", false);
			
			
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(requestBody, headers);
	        ResponseEntity<String> result = restTemplate.postForEntity(ollamaInquiryPromptUrl, requestEntity, String.class);
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<Object,Object> data1 = objectMapper.readValue(result.getBody(), Map.class);
	        return ((String) data1.get("response")).replace("\r", "").trim().replace("\n", "").replaceAll("[^a-zA-Z\s_,]", "");
		}catch (Exception e) {
			throw new ChatBotException("unable to connect ollama, error while refining user query", e.getMessage(), 500);
		}
	}
	
	private String getChatResponse(String response) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
        Map<Object,Object> data1 = objectMapper.readValue(response, Map.class);
        
        String resSTR =(String) ((Map) data1.get("message")).get("content");
        return resSTR;
	}
	private List<Message> getSystemPrompts(boolean otherFlow) {
		if(otherFlow) {
			return  Arrays.asList(
					new Message("system", Prompts.systemPrompt1),
					new Message("system", Prompts.systemPrompt2),
					new Message("system", Prompts.systemPrompt3)
					);
		}else {
			return  Arrays.asList(
					new Message("system", Prompts.systemPrompt1),
					new Message("system", Prompts.systemPrompt2)
					
					);
		}
		
	}

	@Override
	public Vector getVector(String text) throws ChatBotException{
		try {
			String jsonInputString = "{ \"model\": \"" +OllamaModels.Vector_Generator.getDescription() + "\", \"prompt\": \"" + text + "\" }";
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<String> requestEntity = new HttpEntity<>(jsonInputString, headers);
	        ResponseEntity<Vector> result = restTemplate.postForEntity(ollamaVectorGeneraterUrl, requestEntity, Vector.class);
			return result.getBody();
		} catch (Exception e) {
            throw new ChatBotException("unable to convet text to vectors", e.getMessage(), 500);
        }
	}
}

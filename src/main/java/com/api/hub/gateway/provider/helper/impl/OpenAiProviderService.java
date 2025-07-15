package com.api.hub.gateway.provider.helper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.hub.exception.APICallException;
import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.ModelExecutionException;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.constants.ProviderType;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.OpenAIAllModelProperties;
import com.api.hub.gateway.model.OpenAIChatModelProperties;
import com.api.hub.gateway.provider.helper.Provider;
import com.api.hub.gateway.service.impl.LLMRequestHelper;
import com.api.hub.gateway.service.impl.LLMRequestHelper.Request;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ToolChoice;
// import dev.langchain4j.model.chat.request.Prompt; // Removed, ChatRequest uses List<ChatMessage>
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel; // New import
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiLanguageModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("openAiProvider")
public class OpenAiProviderService implements Provider {

	@Autowired(required = false)
	private OpenAiChatModel chatModel;
	
	@Autowired(required = false)
	private OpenAiLanguageModel languageModel;
	
	@Autowired(required = false)
	private OpenAiEmbeddingModel embedModel;
	
	@Autowired(required = false)
	private OpenAiModerationModel moderationModel;
	
	@Autowired(required = false)
	private OpenAiImageModel imageModel;
	
	@Autowired(required = false)
	private OpenAIAllModelProperties modelProperties;
	
	@Autowired
	private LLMRequestHelper handler;
	
    @Override
    public String getName() {
        return ProviderType.OPENAI.getLabel();
    }

    @Override
    public GatewayResponse getChatResponse(GatewayRequest gatewayRequest) throws ApiHubException { // Changed return type and param name
        if (chatModel == null) {
            log.error("OpenAI ChatModel is not initialized. Request ID: {}", gatewayRequest.getRequestId());
            throw new ModelExecutionException("5001-ai-gateway", "OpenAI ChatModel is not initialized for request ID: " + gatewayRequest.getRequestId(), "AI model required for this request is not available or not configured correctly.");
        }

        
        // SystemMessage could be added here if there's a global system message for this provider
        // messages.add(SystemMessage.from("Default system message for OpenAI"));

        
        //ObjectProvider<ChatModelListener> listeners;
       
        
        
       
        Request request = handler.getRequest(gatewayRequest, ChatType.CHAT);
        
        OpenAIChatModelProperties chatModelProperties = modelProperties.openAIChatModelProperties(gatewayRequest.getModelName());
        OpenAiChatRequestParameters.Builder  parametersBuilder = OpenAiChatRequestParameters.builder()
        .modelName(chatModelProperties.modelName())
        .temperature(chatModelProperties.temperature())
        .topP(chatModelProperties.topP())
        .stopSequences(chatModelProperties.stop())
        .maxOutputTokens(chatModelProperties.maxTokens())
        .maxCompletionTokens(chatModelProperties.maxCompletionTokens())
        .presencePenalty(chatModelProperties.presencePenalty())
        .frequencyPenalty(chatModelProperties.frequencyPenalty())
        .logitBias(chatModelProperties.logitBias())
        //.responseFormat(chatModelProperties.responseFormat())
       // .supportedCapabilities(chatModelProperties.supportedCapabilities())
       // .strictJsonSchema(chatModelProperties.strictJsonSchema())
        .seed(chatModelProperties.seed())
        .user(chatModelProperties.user())
       // .strictTools(chatModelProperties.strictTools())
        .parallelToolCalls(chatModelProperties.parallelToolCalls())
        .store(chatModelProperties.store())
        .metadata(chatModelProperties.metadata())
        .serviceTier(chatModelProperties.serviceTier());
        //.toolChoice(ToolChoice.AUTO)
       // .toolSpecifications(specs)
       // .defaultRequestParameters(OpenAiChatRequestParameters.builder()
       //         .reasoningEffort(chatModelProperties.reasoningEffort())
       //         .build())
        
        //.listeners(listeners.orderedStream().toList())
       // .build();
        
        if(request.getChoice() != null && request.getTools() != null) {
        	parametersBuilder.toolChoice(request.getChoice());
        	parametersBuilder.toolSpecifications(request.getTools());
        }
        
        OpenAiChatRequestParameters parameters = parametersBuilder.build();
        // Construct ChatRequest with messages
        ChatRequest chatRequest = ChatRequest.builder()
        		.messages(request.getMessages())
        		.parameters(parameters)
				/*
				 * .toolChoice(ToolChoice.REQUIRED) .toolSpecifications(specs)
				 */
        		.build();

        
        try {
            log.info("Sending chat request to OpenAI model: {} (Request ID: {})", gatewayRequest.getModelName(), gatewayRequest.getRequestId());
            // Use doChat as per user feedback and verified ChatModel interface
            ChatResponse response = chatModel.doChat(chatRequest);

            log.info("Received response from OpenAI for Request ID: {}", gatewayRequest.getRequestId());
            return new GatewayResponse(response,null);
        } catch (Exception e) {
            log.error("Error getting chat response from OpenAI (Request ID: {}): {}", gatewayRequest.getRequestId(), e.getMessage(), e);
            // Consider re-throwing a more specific custom exception or returning an error ChatResponse
            throw new APICallException("gateway-3002-api", "Failed to get chat response from OpenAI for request ID: " + gatewayRequest.getRequestId() + ". Error: " + e.getMessage(), "Error communicating with the AI service provider");
        }
    }
}

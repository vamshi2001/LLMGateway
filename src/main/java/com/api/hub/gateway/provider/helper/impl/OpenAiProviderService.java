package com.api.hub.gateway.provider.helper.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.hub.exception.APICallException;
import com.api.hub.exception.ModelExecutionException;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.constants.ProviderType;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.OpenAIAllModelProperties;
import com.api.hub.gateway.model.OpenAIChatModelProperties;
import com.api.hub.gateway.model.TollCallData;
import com.api.hub.gateway.provider.helper.Provider;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
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
	
    @Override
    public String getName() {
        return ProviderType.OPENAI.getLabel();
    }
    
    @Autowired
	@Qualifier("ToolCallCache")
	Cache<String,TollCallData> toolCallCache;

    @Override
    public ChatResponse getChatResponse(GatewayRequest gatewayRequest) { // Changed return type and param name
        if (chatModel == null) {
            log.error("OpenAI ChatModel is not initialized. Request ID: {}", gatewayRequest.getRequestId());
            throw new ModelExecutionException("gateway-5001-ai", "OpenAI ChatModel is not initialized for request ID: " + gatewayRequest.getRequestId(), "AI model required for this request is not available or not configured correctly.");
        }

        List<ChatMessage> messages = new ArrayList<>();
        // SystemMessage could be added here if there's a global system message for this provider
        // messages.add(SystemMessage.from("Default system message for OpenAI"));

        if (gatewayRequest.getChatHistory() != null) {
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
        //ObjectProvider<ChatModelListener> listeners;
        messages.add(UserMessage.from(gatewayRequest.getUserMessage()));
        
        messages.add(SystemMessage.from(gatewayRequest.getSystemMessage()));
        if(gatewayRequest.getAdditionalInfo() != null && gatewayRequest.getAdditionalInfo().size() > 0) {
        	for(String str : gatewayRequest.getAdditionalInfo()) {
        		 messages.add(SystemMessage.from(str));
        	}
        }
        List<ToolSpecification> specs = new ArrayList<ToolSpecification>();
        for(String key : toolCallCache.keys()) {
        	specs.add(toolCallCache.get(key).getToolSpecification());
        }
        
        OpenAIChatModelProperties chatModelProperties = modelProperties.openAIChatModelProperties(gatewayRequest.getModelName());
        OpenAiChatRequestParameters  parameters = OpenAiChatRequestParameters.builder()
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
        .serviceTier(chatModelProperties.serviceTier())
        .toolChoice(ToolChoice.AUTO)
        .toolSpecifications(specs)
       // .defaultRequestParameters(OpenAiChatRequestParameters.builder()
       //         .reasoningEffort(chatModelProperties.reasoningEffort())
       //         .build())
        
        //.listeners(listeners.orderedStream().toList())
        .build();
        
        	
        // Construct ChatRequest with messages
        ChatRequest chatRequest = ChatRequest.builder()
        		.messages(messages)
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
            return response;
        } catch (Exception e) {
            log.error("Error getting chat response from OpenAI (Request ID: {}): {}", gatewayRequest.getRequestId(), e.getMessage(), e);
            // Consider re-throwing a more specific custom exception or returning an error ChatResponse
            throw new APICallException("gateway-3002-api", "Failed to get chat response from OpenAI for request ID: " + gatewayRequest.getRequestId() + ". Error: " + e.getMessage(), "Error communicating with the AI service provider.", e);
        }
    }
}


/**
 * Extends APICallException to include the original cause.
 */
class APICallException extends com.api.hub.exception.APICallException {
    private static final long serialVersionUID = 1L;

    public APICallException(String errorCode, String exceptionMsg, String msgToUser, Throwable cause) {
        super(errorCode, exceptionMsg, msgToUser);
        initCause(cause);
    }
}

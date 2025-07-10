package com.api.hub.gateway.provider.helper;

import com.api.hub.gateway.model.GatewayRequest;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;

public interface Provider {

    /**
     * Handles chat requests.
     * @param request The gateway request object.
     * @return A ChatResponse object from LangChain4j.
     */
    default ChatResponse getChatResponse(GatewayRequest request) {
        throw new UnsupportedOperationException(getName() + " provider does not support Chat");
    }

    /**
     * Handles language model requests (non-chat).
     * @param request The gateway request object.
     * @return A response object, specific to the language model's capabilities.
     */
    default Object getLanguageResponse(GatewayRequest request) {
        throw new UnsupportedOperationException(getName() + " provider does not support Language");
    }

    /**
     * Handles embedding generation requests. (Old method, consider for deprecation or removal)
     * @param request The gateway request object.
     * @return A response object containing embeddings.
     */
    default Object getEmbeddingResponse(GatewayRequest request) {
    	throw new UnsupportedOperationException(getName() + " provider does not support Embedding");
    }

    /**
     * Handles moderation requests.
     * @param request The gateway request object.
     * @return A response object containing moderation results.
     */
    default Object getModerationResponse(GatewayRequest request) {
        throw new UnsupportedOperationException(getName() + " provider does not support Moderation");
    }

    /**
     * Handles image generation requests.
     * @param request The gateway request object.
     * @return A response object containing the generated image or its reference.
     */
    default Object getImageResponse(GatewayRequest request) {
        throw new UnsupportedOperationException(getName() + " provider does not support Image");
    }

    /**
     * Gets the name of the provider.
     * @return The name of the provider (e.g., "OpenAI", "Anthropic").
     */
    String getName(); // Abstract method
}
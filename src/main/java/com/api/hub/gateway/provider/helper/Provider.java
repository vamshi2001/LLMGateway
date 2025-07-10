package com.api.hub.gateway.provider.helper;

import com.api.hub.exception.InternalServerException;
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
        throw new InternalServerException("gateway-8009-ai", getName() + " provider does not support Chat", "Chat functionality is not supported by this provider.");
    }

    /**
     * Handles language model requests (non-chat).
     * @param request The gateway request object.
     * @return A response object, specific to the language model's capabilities.
     */
    default Object getLanguageResponse(GatewayRequest request) {
        throw new InternalServerException("gateway-8009-ai", getName() + " provider does not support Language", "Language functionality is not supported by this provider.");
    }

    /**
     * Handles embedding generation requests. (Old method, consider for deprecation or removal)
     * @param request The gateway request object.
     * @return A response object containing embeddings.
     */
    default Object getEmbeddingResponse(GatewayRequest request) {
	throw new InternalServerException("gateway-8009-ai", getName() + " provider does not support Embedding", "Embedding functionality is not supported by this provider.");
    }

    /**
     * Handles moderation requests.
     * @param request The gateway request object.
     * @return A response object containing moderation results.
     */
    default Object getModerationResponse(GatewayRequest request) {
        throw new InternalServerException("gateway-8009-ai", getName() + " provider does not support Moderation", "Moderation functionality is not supported by this provider.");
    }

    /**
     * Handles image generation requests.
     * @param request The gateway request object.
     * @return A response object containing the generated image or its reference.
     */
    default Object getImageResponse(GatewayRequest request) {
        throw new InternalServerException("gateway-8009-ai", getName() + " provider does not support Image", "Image generation functionality is not supported by this provider.");
    }

    /**
     * Gets the name of the provider.
     * @return The name of the provider (e.g., "OpenAI", "Anthropic").
     */
    String getName(); // Abstract method
}
package com.api.hub.gateway.provider.helper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api.hub.exception.APICallException;
import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.Utility;
import com.api.hub.gateway.constants.ProviderType;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.provider.helper.Provider;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("ollamaProvider")
public class OllamaProviderService implements Provider {
	
	@Autowired(required = false)
	private OllamaEmbeddingModel embedModel;
	
	@Value("${ollama.embedding-model.maxSegmentSizeInChars}")
	private int maxSegmentSizeInChars;
	@Value("${ollama.embedding-model.maxOverlapSizeInChars}")
	private int maxOverlapSizeInChars;
	
	@Override
	public String getName() {
		return ProviderType.OPENAI.getLabel();
	}

	@Override
	public GatewayResponse getEmbeddingResponse(GatewayRequest gatewayRequest) throws ApiHubException {
		try {
			List<String> docs = new ArrayList<String>();
			docs.add(gatewayRequest.getUserMessage());
			List<TextSegment> segment = Utility.generateTextSegments( docs, maxSegmentSizeInChars, maxOverlapSizeInChars);
			gatewayRequest.setSegment(segment);
			Response<List<Embedding>> embeding = embedModel.embedAll(segment);
			
			GatewayResponse res = new GatewayResponse();
			res.setEmbeddingResponse(embeding);
			return res;
		} catch (Exception e) {
	        log.error("Error getting chat response from OpenAI (Request ID: {}): {}", gatewayRequest.getRequestId(), e.getMessage(), e);
	        // Consider re-throwing a more specific custom exception or returning an error ChatResponse
	        throw new APICallException("gateway-3002-api", "Failed to get chat response from OpenAI for request ID: " + gatewayRequest.getRequestId() + ". Error: " + e.getMessage(), "Error communicating with the AI service provider");
	    }
	}
}

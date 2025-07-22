package com.api.hub.gateway.model;

import java.util.List;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayResponse {

	private ChatResponse chatResponse;
	
	private Response<List<Embedding>> embeddingResponse;
}

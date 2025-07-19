package com.api.hub.gateway.model;

import dev.langchain4j.data.embedding.Embedding;
import lombok.Data;

@Data
public class RagModel {

	private Embedding queryVector;
	
	private String persona;
	
	private String text;
}

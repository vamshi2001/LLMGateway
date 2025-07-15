package com.api.hub.gateway.model;

import java.util.List;

import dev.langchain4j.data.embedding.Embedding;
import lombok.Data;

@Data
public class RagModel {

	private Embedding queryVector;
	
	private String persona;
}

package com.api.hub.chatbot.constants;

public enum OllamaModels {
	Lamma3("llama3.2:3b"),
	Phi_Latest("phi"),
	TinyLlama_Latest("tinyllama"),
	Vector_Generator("nomic-embed-text"),
	OpenAPI_gpt3("gpt-3.5-turbo");

	private final String description;

    // Constructor
	OllamaModels(String description) {
        this.description = description;
    }

    // Getter method
    public String getDescription() {
        return description;
    }
}

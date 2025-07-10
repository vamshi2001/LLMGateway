package com.api.hub.gateway.constants;

public enum ProviderType {

    OPENAI("OpenAI");

    private final String label;

    ProviderType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
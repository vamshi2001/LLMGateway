package com.api.hub.gateway.constants;

public enum ChatType {

    CHAT("Chat"),
    LANGUAGE("Language"),
    EMBEDDING("Embedding"),
    MODERATION("Moderation"),
    IMAGE("Image");

    private final String label;

    ChatType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
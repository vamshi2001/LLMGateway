package com.api.hub.gateway.constants;

public enum RagFlowType {

    WEB_SEARCH("websearch"),
    VECTOR_SEARCH("vectorsearch"),
    DOCUMENT_SEARCH("documentsearch");

    private final String label;

    RagFlowType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

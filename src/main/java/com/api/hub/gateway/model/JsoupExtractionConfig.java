package com.api.hub.gateway.model;

import java.util.List;

public class JsoupExtractionConfig {

    private String host;
    private List<ExtractionRule> rules;

    // Getters and Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<ExtractionRule> getRules() {
        return rules;
    }

    public void setRules(List<ExtractionRule> rules) {
        this.rules = rules;
    }

    public static class ExtractionRule {

        private String type;    // e.g., "id", "tag", "class", "selector"
        private String value;   // e.g., "main-content", "p", "article", ".nav > li"
        private boolean all;    // true = extract all matching elements, false = only first

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isAll() {
            return all;
        }

        public void setAll(boolean all) {
            this.all = all;
        }
    }
}
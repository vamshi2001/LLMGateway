package com.api.hub.gateway.service;

import java.util.List;

import com.api.hub.gateway.model.ChatHistory;

public interface SearchService {

	String getData(String userMessage, List<ChatHistory> chatHistory);
}

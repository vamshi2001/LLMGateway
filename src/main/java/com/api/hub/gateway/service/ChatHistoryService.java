package com.api.hub.gateway.service;

import java.util.List;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.model.ChatHistory;

public interface ChatHistoryService {

	List<ChatHistory> getChatHistory(ChatType chatType ,String bSessionId) throws ApiHubException;
	
	void save(ChatType chatType, ChatHistory history) throws ApiHubException;
}

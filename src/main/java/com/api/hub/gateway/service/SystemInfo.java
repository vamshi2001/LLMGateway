package com.api.hub.gateway.service;

import java.util.List;
import java.util.concurrent.Future;

import com.api.hub.gateway.model.ChatHistory;

public interface SystemInfo {

	List<String> getAdditionalInfo(String ragSource, String userMessage, Future<List<ChatHistory>> chatHistory) throws Exception;
}

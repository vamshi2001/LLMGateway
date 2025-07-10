package com.api.hub.gateway.dao;

import java.util.LinkedList;

import com.api.hub.gateway.model.ChatHistory;

public interface ChatHistoryDao {

	LinkedList<ChatHistory> get(String bSessionId, int nChats);
	
	void save(ChatHistory currentChat);
}

package com.api.hub.gateway.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.dao.ChatHistoryDao;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.service.ChatHistoryService;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService{

	@Autowired
	private ChatHistoryDao dao;
	
	@Override
	public List<ChatHistory> getChatHistory(ChatType chatType, String bSessionId, int nChats) {
		
		if(chatType.equals(ChatType.CHAT)) {
			return dao.get(bSessionId, nChats);
		}
		
		return null;
	}
	
	public void save(ChatType chatType, ChatHistory history) {
		
		if(chatType.equals(ChatType.CHAT)) {
			dao.save(history);
		}
		
	}

}

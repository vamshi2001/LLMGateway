package com.api.hub.gateway.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.dao.ChatHistoryDao;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.service.ChatHistoryService;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService{

	@Autowired
	private ChatHistoryDao dao;
	
	@Value("nChats")
	private Integer nChats;
	
	@Override
	public List<ChatHistory> getChatHistory(ChatType chatType, String bSessionId) throws ApiHubException {
		try {
			if(chatType.equals(ChatType.CHAT)) {
				return dao.get(bSessionId, nChats);
			}else {
				return null;
			}
		}catch (ApiHubException e) {
			throw e;
		}
	}
	
	public void save(ChatType chatType, ChatHistory history) throws ApiHubException{
		try {
			if(chatType.equals(ChatType.CHAT)) {
				dao.save(history);
			}
		}catch (ApiHubException e) {
			throw e;
		}
	}

}

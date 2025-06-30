package com.api.hub.chatbot.starter;

import com.api.hub.chatbot.pojo.ChatBotException;

public interface Conversation {

	public String processMessage(String message, String tenentId, String conversationId) throws ChatBotException;
	
}

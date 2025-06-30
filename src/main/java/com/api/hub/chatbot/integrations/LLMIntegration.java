package com.api.hub.chatbot.integrations;

import java.util.List;

import com.api.hub.chatbot.entity.BusinessEntity;
import com.api.hub.chatbot.pojo.Chat;
import com.api.hub.chatbot.pojo.ChatBotException;
import com.api.hub.chatbot.pojo.Vector;

public interface LLMIntegration {

	Vector getVector(String text) throws ChatBotException;
	String messageToUser(List<BusinessEntity> businessdataList, List<Chat> chatHistory, boolean nonBusinessQuestion) throws ChatBotException;
	String refinePrompt(List<Chat> chatHistory) throws ChatBotException;
	String getCategories(String query) throws ChatBotException;
}

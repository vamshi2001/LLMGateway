package com.api.hub.chatbot.starter.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.api.hub.chatbot.cache.LocalCacheStorage;
import com.api.hub.chatbot.daoImpl.ChatHistoryDao;
import com.api.hub.chatbot.entity.ChatHistoryEntity;
import com.api.hub.chatbot.entity.ChatHistoryId;
import com.api.hub.chatbot.handler.ChatHandler;
import com.api.hub.chatbot.pojo.Chat;
import com.api.hub.chatbot.pojo.ChatBotException;
import com.api.hub.chatbot.pojo.ChatDataHolder;
import com.api.hub.chatbot.pojo.ChatMetaData;
import com.api.hub.chatbot.starter.Conversation;

import lombok.extern.slf4j.Slf4j;

@Service("StateLessConversation")
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StateLessConversationImpl implements Conversation{
	
	@Autowired
	private ChatHandler chat;
	
	@Autowired
	private ChatHistoryDao chatHistoryDao;
	
	
	@Override
	public String processMessage(String message, String tenentId, String chatId) throws ChatBotException {
		ChatMetaData data = null;
		try {
			log.info("started processing message");
			data = chat.getChatData(tenentId, chatId);
			
			Chat chatData = new Chat(message, null);
			data.getChatHistory().saveChat(chatData);
			ChatDataHolder.set(data);
			data.setUserIntention(chat.getUserIntentions());
			
			String response  = chat.resloveUserQueryTask();
			
			ChatHistoryEntity history = new ChatHistoryEntity();
			ChatHistoryId id = new ChatHistoryId();
			id.setChatId(chatId);
			history.setQuery(message);
			history.setResponse(response);
			id.setChatTime(new Date());
			history.setChatHistoryId(id);
			//chatHistoryDao.saveChatHistory(history);
			
			chatData.setResponse(response);
			return response;
		} catch (ChatBotException e) {
			// TODO Auto-generated catch block
			if(data!=null) {
				data.getChatHistory().remove();
			}
			throw e;
		}catch (Exception e) {
			if(data!=null) {
				data.getChatHistory().remove();
			}
			throw new ChatBotException("error while processing message", e.getMessage(), 300);
		}finally {
			ChatDataHolder.clear();
		}
		
		
		
	}
	
}

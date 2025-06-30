package com.api.hub.chatbot.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.api.hub.chatbot.pojo.ChatHistory;
import com.api.hub.chatbot.pojo.ChatMetaData;

import jakarta.inject.Singleton;
import lombok.NonNull;

@Component
@Singleton
public class LocalCacheStorage {

	private volatile Map<String,ChatMetaData> chatdata = new HashMap<String,ChatMetaData>();
	
	public volatile Map<String, String> chatIdHolder = new HashMap<String, String>();
	
	@NonNull
	public String getChatIdFromCache(String uniqueId) {
		return chatIdHolder.get(uniqueId);
	}
	
	@NonNull
	public void setChatIdHolder(String uniqueId, String chatId) {
		chatIdHolder.put(uniqueId, chatId);
	}
	public boolean isChatDataPresent(@NonNull String chatId) {
		return chatdata.containsKey(chatId);
	}
	public ChatMetaData getChatData(@NonNull String chatId) {
		return chatdata.get(chatId);
	}
	
	public void saveChatData(@NonNull ChatMetaData data, @NonNull String chatId) {
		chatdata.put(chatId, data);
	}
}

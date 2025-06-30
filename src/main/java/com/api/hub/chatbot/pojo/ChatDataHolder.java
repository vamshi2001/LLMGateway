package com.api.hub.chatbot.pojo;

import java.util.UUID;

public class ChatDataHolder {

	private static ThreadLocal<ChatMetaData> holder = new ThreadLocal<ChatMetaData>();
	
	private static ThreadLocal<ConsoleLoggingPojo> pojoHolder = new ThreadLocal<ConsoleLoggingPojo>();
	
	public static ConsoleLoggingPojo getLoggingPojo() {
		ConsoleLoggingPojo pojo = pojoHolder.get();
		if(pojo == null) {
			pojo = new ConsoleLoggingPojo();
			pojo.setConversationId(UUID.randomUUID().toString());
		}
		pojoHolder.set(pojo);
		return pojo;
	}
	
	public static void clearLoggingPojo() {
		pojoHolder.remove();
	}

	public static ChatMetaData get() {
		ChatMetaData data = holder.get();
		if(data == null) {
			data = new ChatMetaData();
			holder.set(data);
			return data;
		}
		
		return data;
	}
	
	public static void set(ChatMetaData data) {
		if(data != null) {
			holder.set(data);
		}
	}
	public static void clear() {
		holder.remove();
	}
}

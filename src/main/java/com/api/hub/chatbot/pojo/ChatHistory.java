package com.api.hub.chatbot.pojo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ChatHistory {

	List<Chat> chatHistoryArray;
	private int lastChatPointer = 0;
	
	public ChatHistory() {
		chatHistoryArray = new LinkedList<Chat>();
	}
	
	public void saveChat(Chat chat) {
		lastChatPointer++;
		chatHistoryArray.add(chat);
	}
	public Chat getLastChat() {
		return chatHistoryArray.get(lastChatPointer - 1);
	}
	public List<Chat> getLastChats(int nChats) {
		int pointer = lastChatPointer - 1;
		int index = nChats - 1;
		if(nChats >= lastChatPointer) {
			return chatHistoryArray;
					
		}else {
			return chatHistoryArray.subList(pointer - index, pointer + 1);
			
		}
		
	}
	public void remove() {
		chatHistoryArray.remove(lastChatPointer-1);
		lastChatPointer--;
	}
}

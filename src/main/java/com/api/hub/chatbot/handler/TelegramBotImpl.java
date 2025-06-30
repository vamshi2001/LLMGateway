package com.api.hub.chatbot.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.api.hub.chatbot.cache.ChatDataCache;
import com.api.hub.chatbot.entity.CustomerDetailsEntity;
import com.api.hub.chatbot.pojo.ChatBotException;
import com.api.hub.chatbot.pojo.ChatDataHolder;
import com.api.hub.chatbot.pojo.ConsoleLoggingPojo;
import com.api.hub.chatbot.starter.impl.StateLessConversationImpl;

@Service
public class TelegramBotImpl extends TelegramLongPollingBot {

    // Bot token from BotFather

    @Value("${tenentId}")
    String tenentId;
    
    @Value("${telegram.appId}")
    String appId;
    
    @Value("${telegram.bot.name}")
    String botName;
    
    @Value("${telegram.bot.token}")
    String botToken;
    
    @Value("${spring.application.name}")
    String appName;
    
    @Autowired
    ChatHandler chat;
    
    @Autowired
    StateLessConversationImpl conversationHandler;
    
    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
    
    
    @Override
    public void onUpdateReceived(Update update) {
    	ConsoleLoggingPojo pojo = ChatDataHolder.getLoggingPojo();
    	
    	pojo.setAppName(appName);
    	pojo.setSourceApplicationName("telegram");
    	pojo.setTenentId(tenentId);
        if (update.hasMessage() && update.getMessage().hasText()) {
        	
        	String responseText = "unable to process message";
        	String chatId = "";
        	String telegramChatId = "";
        	try {
        		Message message = update.getMessage();
                String userMessage = message.getText().replaceAll("[^a-zA-Z0-9\s.,${}]?", "");
                telegramChatId = message.getChatId().toString();

                User user = message.getFrom();  // Access user details
                String userId = user.getId().toString();
                //chatId = chatIdMap.get(tenentId+"-"+userId);
                chatId = chat.isNewChat(tenentId, userId);
                // Extract user details
                
                if(chatId == null) {
                	String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    String username = user.getUserName();
                    Map<String,Object> socialMediaData = new HashMap<String,Object>();
                    socialMediaData.put("firstName", firstName);
                    socialMediaData.put("lastName", lastName);
                    socialMediaData.put("username", username);
                    socialMediaData.put("userId", userId);
                    socialMediaData.put("appId", appId);
                    socialMediaData.put("source", "telegram");
                    CustomerDetailsEntity existingUserDetails = chat.saveUserDetails(tenentId, socialMediaData);
                    chatId = chat.initChat(tenentId, socialMediaData, existingUserDetails);
                    
                }
                pojo.setChatId(chatId);
                responseText = conversationHandler.processMessage(userMessage, tenentId, chatId);

        	}catch (ChatBotException e) {
        		responseText = e.getCustomMessage();
			}catch (Exception e) {
				// TODO: handle exception
        		e.printStackTrace();
			}
            
            // Send the response
            sendResponse(telegramChatId, responseText);
        }
    }

    // Helper method to send messages
    private void sendResponse(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);  // Executes the Telegram API call
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }finally {
        	ChatDataHolder.clearLoggingPojo();
		}
    }
}

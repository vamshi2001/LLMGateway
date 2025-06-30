package com.api.hub.chatbot.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.api.hub.chatbot.handler.TelegramBotImpl;

@Component
public class BotsConfiguration {

	@Autowired
	private TelegramBotImpl telegramBot;
	
	@Value("${telegram.on}")
	Boolean turnOn;
	
	@EventListener(ApplicationReadyEvent.class)
	public void configureTeleGramBot() {
		if(!turnOn) {
			return;
		}
        try {
        	TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(telegramBot);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

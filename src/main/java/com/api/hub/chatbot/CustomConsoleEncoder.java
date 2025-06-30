package com.api.hub.chatbot;
import com.api.hub.chatbot.pojo.ChatDataHolder;
import com.api.hub.chatbot.pojo.ConsoleLoggingPojo;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

public class CustomConsoleEncoder extends EncoderBase<ILoggingEvent> {

	@Override
	public byte[] headerBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] encode(ILoggingEvent event) {
		
		ConsoleLoggingPojo originalPojo = ChatDataHolder.getLoggingPojo();
		ConsoleLoggingPojo pojo = originalPojo.clone();
		pojo.setLogLevel(event.getLevel().toString());
		pojo.setPackageName(event.getLoggerName());
		pojo.setLogMessage(event.getMessage());
		return pojo.toString().getBytes();
	}

	@Override
	public byte[] footerBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.api.hub.chatbot.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsoleLoggingPojo {

	private String conversationId;
	private String tenentId;
	private String chatId;
	private String sourceApplicationName;
	private String appName;
	private String logLevel;
	private String packageName;
	private String logMessage;
	
	@Override
	public String toString() {
		return "{Conversation ID : " + conversationId + ", Tenent ID : " + tenentId + ", Chat ID : " + chatId
				+ ", Source Application Name : " + sourceApplicationName + ", Current Application Name : " + appName + 
				", Log Level : " + logLevel + ", Log Name : " + packageName + ", Log Message : " + logMessage + "}\n";
	}
	
	public String toStringFileAppender() {
		return "Tenent ID : " + tenentId + "\n Chat ID : " + chatId
				+ "\n Log Message : " + logMessage + "\n===================================================================\n";
	}
	
	public ConsoleLoggingPojo clone() {
		return new ConsoleLoggingPojo(conversationId, tenentId, chatId, sourceApplicationName, appName, logLevel, packageName, logMessage);
	}
}
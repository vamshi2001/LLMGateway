package com.api.hub.chatbot.pojo;


import lombok.Getter;

@Getter
public class ChatBotException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String customMessage;
    private final String errorMessage;
    private final Integer errorCode;

    // Constructor with message, stack trace, and code
    public ChatBotException(String customMessage, String errorMessage, Integer errorCode) {
        super(customMessage);  // Pass the message to RuntimeException
        this.customMessage = customMessage;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    // Overriding toString() for better logging
    @Override
    public String toString() {
        return "ChatBotException{" +
               "error='" + customMessage + '\'' +
               ", errorMessage='" + errorMessage + '\'' +
               ", errorCode=" + errorCode +
               '}';
    }
}

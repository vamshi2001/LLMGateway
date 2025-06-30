package com.api.hub.chatbot.pojo;

public class Constants {

	public enum ChatStatus{
		PENDING,
		COMPLETED;
	}
	public enum ChatFlow{
		INQUIRY,
		COMPLAINT,
		GREET,
		SCHEDULE_CALL,
		END_CHAT,
		OTHER;
	}
	public enum Tasks{
		ANSWER_INQUIRY,
		TAKE_COMPLAINT,
		GREET_AND_NAME,
		SCHEDULE_CALL,
		END_CHAT;
	}
}

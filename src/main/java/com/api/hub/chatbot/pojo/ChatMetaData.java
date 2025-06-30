package com.api.hub.chatbot.pojo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.api.hub.chatbot.pojo.Constants.ChatFlow;
import com.api.hub.chatbot.pojo.Constants.ChatStatus;
import com.api.hub.chatbot.pojo.Constants.Tasks;

import lombok.Data;

@Data
public class ChatMetaData {

	private ChatFlow currFlow = ChatFlow.GREET; //dynamic
	private List<ChatFlow> userIntention = new LinkedList<ChatFlow>(); // dynamic
	private ChatStatus flowStatus; // dynamic
	private String appId; // static
	private String chatId; // static
	private String tenentId; // static
	private Integer customerId; //static
	private Date startTime; // static
	private Date endTime; // static
	private String userName; //static
	private ChatHistory chatHistory; // static
	private CustomerDetails customerDetails; //static
	private List<Tasks> tasks = new LinkedList<Tasks>(); // dynamic
	
}

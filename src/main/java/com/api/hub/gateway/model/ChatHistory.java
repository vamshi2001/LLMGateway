package com.api.hub.gateway.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

	private String userMessage;
	private String aiMessage;
	private Date entryTime;
	private String bSessionId;
}

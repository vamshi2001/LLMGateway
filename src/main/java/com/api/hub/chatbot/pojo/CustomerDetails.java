package com.api.hub.chatbot.pojo;

import java.util.Map;

import lombok.Data;

@Data
public class CustomerDetails {

	private String firstName;
	private String lastName;
	private String emailID;
	private String country;
	private Integer countryCode;
	private String phoneNumber;
	private Map<String,Object> socialMediaData;
}

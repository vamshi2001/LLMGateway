package com.api.hub.gateway.model;

import lombok.Data;

@Data
public class PromptVersion {

	private String prompt;
	private String persona;
	private String phase;
}

package com.api.hub.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaProperties {

	private String persona;
	private boolean chatHistoryEnabled;
	private boolean queryRewriteEnabled;
	private boolean ragEnabled;
	private boolean toolCallEnabled;
	
	private String ragSource;
	private int maxFallBackModels;
	
	private String toolChoice;
}

package com.api.hub.gateway.model;

import java.util.List;

import dev.langchain4j.agent.tool.ToolSpecification;
import lombok.Data;

@Data
public class TollCallData {

	private String endPoint;
	private String toolDescription;
	private String toolName;
	private String toolArguments;
	private boolean enabled;
	private List<String> supportedPersona;
	private ToolSpecification toolSpecification;
}

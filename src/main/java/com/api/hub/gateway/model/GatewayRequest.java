package com.api.hub.gateway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.web.multipart.MultipartFile;

import dev.langchain4j.data.segment.TextSegment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRequest {
	
	//data user sends
	//mandatory
    private String userMessage;
    private MultipartFile userImage;
    private String systemMessage;
    
    //optional
    private String botSessionid;
    private boolean isPrompt = false;
    private boolean isModeration = false;
    private boolean isEmbed = false;
    private String persona;
    
    private PersonaProperties personaProps;
    
    //data we populate
    private boolean useChatHistory = false;
    private List<String> additionalInfo = new ArrayList<String>();
    private String requestId;
    private Future<List<ChatHistory>> chatHistory;
    private String modelName;
    private List<String> skipModels = new ArrayList<String>();
    
    private List<TextSegment> segment;
}
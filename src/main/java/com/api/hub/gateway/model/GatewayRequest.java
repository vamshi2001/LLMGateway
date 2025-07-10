package com.api.hub.gateway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.web.multipart.MultipartFile;

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
    private boolean useRAG = false;
    private String ragSource;
    private Integer ragMaxResults = 3;
    private Integer maxChatHistory = 3;
    
    private Integer maxFallBackModels = 1;
    private boolean isPrompt = false;
    private boolean isModeration = false;
    private boolean isEmbed = false;
    private List<String> topics;
    
    //data we populate
    private List<String> additionalInfo = new ArrayList<String>();
    private String requestId;
    private Future<List<ChatHistory>> chatHistory;
    private String modelName;
    private List<String> skipModels = new ArrayList<String>();
}
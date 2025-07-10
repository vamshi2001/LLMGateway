package com.api.hub.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.InputException;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.Pojo;
import com.api.hub.gateway.service.LLMGatewayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class LLMController {

	// .setHttpClientBuilderFactory(new SpringRestClientBuilderFactory());

	@Autowired
	private LLMGatewayService service;

	@PostMapping(value = "v1/llm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getResponse(@RequestBody GatewayRequest req, HttpServletRequest request) {

		
		// req.setUserMessage("what is the result of 5 + 25");
		try {
			validate(req);
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.badRequest().body(e.getMsgToUser());
		}
		String res = null;
		try {
			res = service.getResponse(req);
		} catch (ApiHubException e) {
			log.error(e.toString());
			return ResponseEntity.internalServerError().body(e.getMsgToUser());
		}
		return ResponseEntity.ok(res);
	}

	@PostMapping(value = "getSumResult", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> healthCheck(@RequestBody Pojo res) {
		int total = 0;
		for(Integer val : res.getArgsToADD()) {
			total += val;
		}
		return ResponseEntity.ok(total);
	}
	
	
	public static void validate(GatewayRequest request) throws ApiHubException{
        if (request == null) {
            throw new InputException("1001-input-gateway", "Request payload is null", "Payload is empty");
        }

        boolean userMsgEmpty = false;
        if (request.getUserMessage() == null || request.getUserMessage().trim().isEmpty()) {
        	userMsgEmpty = true;
            throw new InputException("1002-input-gateway", "userMessage is required", "Missing user message");
        }

        if (request.getSystemMessage() == null || request.getSystemMessage().trim().isEmpty()) {
            throw new InputException("1002-input-gateway", "systemMessage is required", "Missing system message");
        }

        // Required file
        if ( (request.getUserImage() == null || request.getUserImage().isEmpty()) && userMsgEmpty ) {
            throw new InputException("1006-input-gateway", "userImage is required", "Missing msg or image is required");
        }

        // Numeric validations
        if (request.getRagMaxResults() == null || request.getRagMaxResults() < 1) {
            throw new InputException("1004-input-gateway", "ragMaxResults must be >= 1", "Invalid ragMaxResults");
        }

        if (request.getMaxChatHistory() == null || request.getMaxChatHistory() < 0) {
            throw new InputException("1004-input-gateway", "maxChatHistory must be >= 0", "Invalid maxChatHistory");
        }

        if (request.getMaxFallBackModels() == null || request.getMaxFallBackModels() < 0) {
            throw new InputException("1004-input-gateway", "maxFallBackModels must be >= 0", "Invalid maxFallBackModels");
        }

        // Optional list validation
        if (request.getTopics() != null) {
            for (String topic : request.getTopics()) {
                if (topic == null || topic.trim().isEmpty()) {
                    throw new InputException("1001-input-gateway", "Topics must not contain blank values", "Invalid topic in topics list");
                }
            }
        }
    }
	
}

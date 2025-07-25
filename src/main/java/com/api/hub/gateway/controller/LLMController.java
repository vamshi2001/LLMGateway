package com.api.hub.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.InputException;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.dao.SystemInfoVectorDao;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.PersonaProperties;
import com.api.hub.gateway.model.Pojo;
import com.api.hub.gateway.model.RagModel;
import com.api.hub.gateway.provider.helper.Provider;
import com.api.hub.gateway.service.LLMGatewayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class LLMController {
	
	@Autowired
	@Qualifier("PersonaPropsCache")
	private Cache<String, PersonaProperties> personaProps;
	
	@Autowired
	SystemInfoVectorDao dao;

	@Autowired
	@Qualifier("ollamaProvider")
	Provider ollama;
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
			req.setPersonaProps(personaProps.get(req.getPersona()));
			res = service.getResponse(req);
		} catch (ApiHubException e) {
			log.error(e.toString());
			return ResponseEntity.internalServerError().body(e.getMsgToUser());
		}
		return ResponseEntity.ok(res);
	}
	
	@PostMapping(value = "v1/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> save(@RequestBody String doc, HttpServletRequest request) {
		// req.setUserMessage("what is the result of 5 + 25");
		
		try {
			GatewayRequest req = new GatewayRequest();
			req.setUserMessage(doc);
			GatewayResponse res = ollama.getEmbeddingResponse(req);
			
			RagModel rag = new RagModel();
			rag.setEmbedings(res.getEmbeddingResponse().content());
			rag.setSegment(req.getSegment());
			rag.setPersona("default");
			dao.save(rag);
		} catch (ApiHubException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.badRequest().body(e.getMsgToUser());
		}
		return ResponseEntity.ok("suucess");
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

        // Required file
        if ( (request.getUserImage() == null || request.getUserImage().isEmpty()) && userMsgEmpty ) {
            throw new InputException("1006-input-gateway", "userImage is required", "Missing msg or image is required");
        }
    }
	
}

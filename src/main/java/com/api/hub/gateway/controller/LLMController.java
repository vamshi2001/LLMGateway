package com.api.hub.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.Pojo;
import com.api.hub.gateway.service.LLMGatewayService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class LLMController {

	// .setHttpClientBuilderFactory(new SpringRestClientBuilderFactory());

	@Autowired
	private LLMGatewayService service;

	@PostMapping(value = "v1/llm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getResponse(@RequestBody GatewayRequest req, HttpServletRequest request) {

		
		// req.setUserMessage("what is the result of 5 + 25");
		String res = service.getResponse(req);
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
	
	
}

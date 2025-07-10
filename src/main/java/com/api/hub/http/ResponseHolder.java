package com.api.hub.http;

import org.springframework.http.ResponseEntity;

import com.api.hub.exception.ApiHubException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseHolder<Res>{
	private boolean success;
	private ApiHubException exp;
	private ResponseEntity<Res> response;
}
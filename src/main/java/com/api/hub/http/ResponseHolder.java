package com.api.hub.http;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseHolder<Res>{
	private boolean success;
	private Exception exp;
	private ResponseEntity<Res> response;
}
package com.api.hub.http.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestConfigAndHeadersHandler {

	public HttpHeaders getHeaders(String service) {
		return null;
	}
	
	public RestTemplate getRestTemplet(String service) {
		return null;
	}
}

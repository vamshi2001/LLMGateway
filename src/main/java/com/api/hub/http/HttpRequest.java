package com.api.hub.http;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.backoff.BackOff;

import com.api.hub.auth.AutheticationHandler;
import com.api.hub.http.url.HostReslover;

import lombok.NonNull;

public class HttpRequest<Res,Req>{
	
	private String pathParams;
	private Map<String,String> queryParams;
	private AutheticationHandler authetication;
	private HostReslover hostReslover;
	
	private Class<Res> responseClass;
	private Class<Req> requestType;
	private Req requestBody;
	
	private Map<String,String> headers;
	private HttpHeaders springHeaders = new HttpHeaders();
	
	private BackOff backoff;
	private boolean backOffEnabled = false;
	
	private HttpMethod httpMethod;

	public String getPathParams() {
		return pathParams;
	}

	public HttpRequest<Res,Req> setPathParams(@NonNull String pathParams) {
		this.pathParams = pathParams.trim();
		return this;
	}

	public Req getRequestBody() {
		return requestBody;
	}

	public HttpRequest<Res,Req> setRequestBody(Req requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	public HttpHeaders getSpringHeaders() {
		return springHeaders;
	}

	public HttpRequest<Res,Req> setHeaders(Map<String, String> headers) {
		this.headers = headers;
		
		headers.forEach(springHeaders::add);
		
		return this;
	}
	public HttpRequest<Res,Req> setSpringHeaders(HttpHeaders springHeaders) {
		this.springHeaders.putAll(springHeaders);
		
		return this;
	}

	public Class<Res> getResponseClass() {
		return responseClass;
	}

	public HttpRequest<Res,Req> setResponseClass(Class<Res> responseClass) {
		this.responseClass = responseClass;
		return this;
	}

	public BackOff getBackoff() {
		return backoff;
	}

	public HttpRequest<Res,Req> setBackoff(@NonNull BackOff backoff) {
		this.backoff = backoff;
		backOffEnabled = true;
		return this;
	}

	public boolean isBackOffEnabled() {
		return backOffEnabled;
	}

	public HttpRequest(Class<Res> responseClass, Class<Req> requestType) {
		super();
		this.responseClass = responseClass;
		this.requestType = requestType;
	}

	public Class<Req> getRequestType() {
		return requestType;
	}

	public HttpRequest<Res,Req> setRequestType(Class<Req> requestType) {
		this.requestType = requestType;
		return this;
	}

	public HostReslover getHostReslover() {
		return hostReslover;
	}

	public HttpRequest<Res,Req> setHostReslover(HostReslover hostReslover) {
		this.hostReslover = hostReslover;
		return this;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public HttpRequest<Res,Req> setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
		return this;
	}

	public AutheticationHandler getAuthetication() {
		return authetication;
	}

	public HttpRequest<Res,Req> setAuthetication(AutheticationHandler authetication) {
		this.authetication = authetication;
		return this;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public HttpRequest<Res,Req> setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
		return this;
	}
}

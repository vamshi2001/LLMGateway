package com.api.hub.http;

import java.util.concurrent.Future;

import com.api.hub.exception.ApiHubException;

public interface HttpHandler {

	void init(String name);
	<Res,Req> HttpRequest<Res,Req> createRequest(Class<Res> responseType, Class<Req> requestType) throws ApiHubException;
	<Res,Req> Future<ResponseHolder<Res>> sendRequest(HttpRequest<Res,Req> request) throws ApiHubException;
	<Res> ResponseHolder<Res> getResponse(Future<ResponseHolder<Res>> future) throws ApiHubException;
	
}

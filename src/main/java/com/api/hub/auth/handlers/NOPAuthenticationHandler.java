package com.api.hub.auth.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.api.hub.auth.AutheticationHandler;

@Component("NOPAuthenticationHandler")
@Scope(value = "prototype")
public class NOPAuthenticationHandler implements AutheticationHandler{

	@Override
	public void init(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getHeaders() {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}

}

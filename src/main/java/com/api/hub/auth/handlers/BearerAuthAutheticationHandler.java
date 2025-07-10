package com.api.hub.auth.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.api.hub.auth.AutheticationHandler;

@Component("BearerAuthAutheticationHandler")
@Scope(value = "prototype")
public class BearerAuthAutheticationHandler implements AutheticationHandler{

	private char[] auth;
	
	@Autowired
	private Environment env;
	
	@Override
	public void init(String name) {
		auth = env.getProperty("auth."+name+".bearerToken").toCharArray();
	}

	@Override
	public Map<String, String> getHeaders() {
		Map<String, String> var = new HashMap<String, String>();
		var.put(HttpHeaders.AUTHORIZATION, "Bearer " + auth.toString());
		return var;
	}
}

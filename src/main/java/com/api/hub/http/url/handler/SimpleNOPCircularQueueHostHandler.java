package com.api.hub.http.url.handler;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.api.hub.http.url.CircularQueueHostIterator;
import com.api.hub.http.url.HostReslover;

import lombok.extern.slf4j.Slf4j;

@Component("SimpleNOPCircularQueueHostHandler")
@Scope(value = "prototype")
@Slf4j
@ConditionalOnProperty(name = "simpleNOPCircularQueueHostHandler", havingValue = "true")
public class SimpleNOPCircularQueueHostHandler implements HostReslover{

	@Autowired
	private Environment env;
	private List<String> urls;
	
	@Override
	public void init(String name) {
		
		String hosts = env.getProperty("http."+name+".hosts");
		urls = Arrays.asList(hosts.split(","));
	}

	@Override
	public String getAllURLS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> getIter() {
		return new CircularQueueHostIterator(urls);
	}

}

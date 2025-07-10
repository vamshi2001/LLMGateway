package com.api.hub.gateway.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class OpenAIAllModelProperties {

	private final Map<String,AtomicReference<OpenAIChatModelProperties>> openAIChatModelProperties = new HashMap<String, AtomicReference<OpenAIChatModelProperties>>();
	
	public AtomicReference<OpenAIChatModelProperties> openAIChatModelPropertiesAtomicRef(String id) {
		AtomicReference<OpenAIChatModelProperties> prop = openAIChatModelProperties.getOrDefault(id, new AtomicReference<OpenAIChatModelProperties>());
		return prop;
	}
	public OpenAIChatModelProperties openAIChatModelProperties(String id) {
		AtomicReference<OpenAIChatModelProperties> prop = openAIChatModelProperties.get(id);
		return prop!=null ? prop.get() : null;
	}
	public void openAIChatModelProperties(String id, AtomicReference<OpenAIChatModelProperties> prop) {
		openAIChatModelProperties.put(id, prop);
	}
}

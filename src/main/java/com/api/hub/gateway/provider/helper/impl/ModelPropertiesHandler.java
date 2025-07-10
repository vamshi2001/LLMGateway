package com.api.hub.gateway.provider.helper.impl;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.constants.ProviderType;
import com.api.hub.gateway.model.OpenAIAllModelProperties;
import com.api.hub.gateway.model.OpenAIChatModelProperties;


@Component
public class ModelPropertiesHandler {
	
	private static final String PREFIX = "llm";

	@Autowired
	private OpenAIAllModelProperties properties;
	
	public void update(Properties prop, String modelId) {
		String provider = prop.getProperty(PREFIX+".provider");
		if(provider != null && provider.equals(ProviderType.OPENAI.getLabel())) {
			updateOpenAPIData(prop, modelId);
		}
	}

	private void updateOpenAPIData(Properties prop, String modelId) {
		String modelType = prop.getProperty(PREFIX+".modelType");
		if(modelType != null && modelType.equals(ChatType.CHAT.getLabel())) {
			OpenAIChatModelProperties modelProps = fromProperties(prop);
			AtomicReference<OpenAIChatModelProperties> ref = properties.openAIChatModelPropertiesAtomicRef(modelId);
			ref.set(modelProps);
			properties.openAIChatModelProperties(modelId, ref);
		}
		
	}
	
	public static OpenAIChatModelProperties fromProperties(Properties props) {
	    
	    PropertySource<Map<String, Object>> propertySource = new PropertiesPropertySource(PREFIX, props);
	    
	    //MutablePropertySources propertySources = new MutablePropertySources();
	    //propertySources.addLast(propertySource);
	    
	    Environment env = new StandardEnvironment();
	    ((ConfigurableEnvironment) env).getPropertySources().addFirst(propertySource);
	    
	    Binder binder = Binder.get(env);
	    BindResult<OpenAIChatModelProperties> result = binder.bind(PREFIX, OpenAIChatModelProperties.class);

	    return result.orElseThrow(() -> new RuntimeException("Could not bind properites"));
	}
}

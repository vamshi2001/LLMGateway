package com.api.hub.gateway.cache.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.AbstractCacheOperations;
import com.api.hub.gateway.dao.PersonaDao;
import com.api.hub.gateway.model.PersonaProperties;

@Component("PersonaPropsCache")
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class PersonaPropsCache extends AbstractCacheOperations<String,PersonaProperties>  {

	@Autowired
	private PersonaDao dao;
	
	@Override
	public boolean source() {
		List<PersonaProperties> array = dao.get();
		for(PersonaProperties prop : array) {
			if(data.containsKey(prop.getPersona())) {
				data.replace(prop.getPersona(), prop);
			}else {
				data.put(prop.getPersona(), prop);
			}
		}
		return false;
	}

	@Override
	public boolean sink(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clear() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Value("${cache.persona.props.minrefreshtime.ms}")
	private long minrefreshtime;

	@Override
	public long minRefreshTime() {
		// TODO Auto-generated method stub
		return minrefreshtime;
	}

}

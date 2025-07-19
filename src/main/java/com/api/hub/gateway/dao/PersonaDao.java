package com.api.hub.gateway.dao;

import java.util.List;

import com.api.hub.gateway.model.PersonaProperties;

public interface PersonaDao {

	List<PersonaProperties> get();

	int save(PersonaProperties persona);
}

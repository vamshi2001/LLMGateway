package com.api.hub.gateway.dao;

import java.util.List;

import com.api.hub.gateway.model.Model;

public interface ModelDao {

	List<Model> get();
	void save(Model model);
}

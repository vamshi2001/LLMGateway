package com.api.hub.gateway.dao;

import java.util.List;

import com.api.hub.gateway.model.TollCallData;

public interface ToolCallDao {

	List<TollCallData> get();

	void save(TollCallData data);
}

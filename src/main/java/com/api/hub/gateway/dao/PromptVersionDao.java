package com.api.hub.gateway.dao;

import java.util.List;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.PromptVersion;

public interface PromptVersionDao {

	String getPrompt(String persona, String phase, String query) throws ApiHubException;

	void save(PromptVersion pojo);

	List<PromptVersion> get();
}

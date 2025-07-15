package com.api.hub.gateway.dao;

import com.api.hub.exception.ApiHubException;

public interface PromptVersionDao {

	String getPrompt(String persona, String phase, String query) throws ApiHubException;
}

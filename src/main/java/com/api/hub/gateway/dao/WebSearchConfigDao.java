package com.api.hub.gateway.dao;

import java.util.List;

import com.api.hub.exception.ApiHubException;
import com.api.hub.gateway.model.JsoupExtractionConfig;

public interface WebSearchConfigDao {

	List<JsoupExtractionConfig> get() throws ApiHubException;
}

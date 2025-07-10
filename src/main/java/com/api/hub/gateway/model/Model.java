package com.api.hub.gateway.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Model {

	private String modelId;
	private String provider;
	private String modelName;
	
	private String type;
	private float rank;
	
	private long maxTokenDay;
	private long maxTokenMonth;
	private long maxRequestDay;
	private long maxRequestMonth;
	
	private boolean enable;
	
	private List<String> topics;

	public void setTopicsFromString(String topicsStr) {
        this.topics = topicsStr != null && !topicsStr.isEmpty()
                ? Arrays.stream(topicsStr.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList())
                : List.of();
    }

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getRank() {
		return rank;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}

	public long getMaxTokenDay() {
		return maxTokenDay;
	}

	public void setMaxTokenDay(long maxTokenDay) {
		this.maxTokenDay = maxTokenDay;
	}

	public long getMaxTokenMonth() {
		return maxTokenMonth;
	}

	public void setMaxTokenMonth(long maxTokenMonth) {
		this.maxTokenMonth = maxTokenMonth;
	}

	public long getMaxRequestDay() {
		return maxRequestDay;
	}

	public void setMaxRequestDay(long maxRequestDay) {
		this.maxRequestDay = maxRequestDay;
	}

	public long getMaxRequestMonth() {
		return maxRequestMonth;
	}

	public void setMaxRequestMonth(long maxRequestMonth) {
		this.maxRequestMonth = maxRequestMonth;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
}

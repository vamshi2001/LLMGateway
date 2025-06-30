package com.api.hub.chatbot.dao;

import java.util.List;

public interface BaseDao<T> {

	@SuppressWarnings("hiding")
	public List<T> get(T entity) throws Exception;
	public <T> boolean save(T entity);
}

package com.api.hub.gateway.cache.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.AbstractCacheOperations;

/**
 * A simple default cache handler implementation for the AI-Agent framework
 * that extends {@link AbstractCacheOperations}.
 * 
 * <p>
 * This cache handler acts as a no-op or placeholder implementation where
 * cache source and sink operations do nothing and always succeed.
 * It can be used as a default or fallback cache handler when no actual
 * external cache synchronization is required.
 * </p>
 * 
 * <p>
 * The {@code SimpleCacheHandler} is registered as a Spring prototype-scoped bean,
 * allowing multiple independent instances to be created and managed by the Spring
 * container.
 * </p>
 * 
 * <p>
 * Note: This implementation currently does not clear the cache on {@link #clear()}
 * and returns {@code false}, indicating no cache clearing logic is applied.
 * </p>
 * 
 * <p>
 * This class is useful during initial development, testing, or scenarios
 * where in-memory caching without external synchronization is sufficient.
 * </p>
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 * 
 * @see AbstractCacheOperations
 */
@Component("SimpleCacheHandler")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class SimpleCacheHandler<K, V> extends AbstractCacheOperations<K,V> {

	/**
	 * No-op source operation.
	 * 
	 * @return {@code true} indicating the source operation is considered successful.
	 */
	@Override
	public boolean source() {
		return true;
	}

	/**
	 * No-op sink operation for the given key.
	 * 
	 * @param key the key to synchronize to the external cache source
	 * @return {@code true} indicating the sink operation is considered successful.
	 */
	@Override
	public boolean sink(K key) {
		return true;
	}

	/**
	 * Clear operation which currently performs no action.
	 * 
	 * @return {@code false} indicating no cache clearing logic is implemented.
	 */
	@Override
	public boolean clear() {
		return false;
	}
	
	@Override
	public long minRefreshTime() {
		 return Long.MAX_VALUE;
	}

}

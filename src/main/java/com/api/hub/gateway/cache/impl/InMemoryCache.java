package com.api.hub.gateway.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.cache.CacheOperations;

/**
 * Abstract base class providing an in-memory cache implementation combining
 * both {@link Cache} and {@link CacheOperations} interfaces for the AI-Agent
 * Execution and Orchestration Platform.
 * 
 * <p>
 * This class maintains cache entries in a {@link HashMap} stored in RAM for
 * fast access and implements basic cache operations such as save, get, put,
 * and delete.
 * </p>
 * 
 * <p>
 * Upon modification of cache data (save, put, delete), it notifies the
 * registered cache handlers by invoking {@link #notifyCacheHandler(Object)}.
 * This enables synchronization or additional processing hooks.
 * </p>
 * 
 * <p>
 * The {@link #get(Object)} method attempts to retrieve the value from the
 * in-memory map and, if the key is not found, triggers a cache population
 * from the underlying data source by calling {@link #source()}. It then
 * retries to return the fresh value.
 * </p>
 * 
 * <p>
 * Subclasses should implement the abstract methods of {@link CacheOperations}
 * such as {@link #refresh()}, {@link #clear()}, {@link #getLastRefreshTime()},
 * and provide logic for cache synchronization with external or persistent
 * cache servers.
 * </p>
 * 
 * <p>
 * This class forms a core part of the caching layer in the AI-agent framework,
 * enabling high-performance, memory-based caching combined with pluggable
 * synchronization mechanisms.
 * </p>
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 * 
 * @see Cache
 * @see CacheOperations
 */
public abstract class InMemoryCache<K,V> implements CacheOperations<K,V>, Cache<K,V> {
	
	/**
	 * The internal in-memory data store holding cache entries for fast access.
	 */
	protected Map<K,V> data = new ConcurrentHashMap<K,V>();
	
	/**
	 * Saves a new cache entry or overwrites an existing one for the given key.
	 * Notifies cache handlers after saving.
	 * 
	 * @param key   the key under which the value is saved
	 * @param value the value to be cached
	 * @return {@code true} indicating the save operation succeeded
	 */
	public boolean save(K key, V value) {
		data.put(key, value);
		notifyCacheHandler(key);
		return true;
	}
	
	/**
	 * Replaces the value for an existing key in the cache.
	 * Notifies cache handlers after the update.
	 * 
	 * @param key   the key whose value is to be replaced
	 * @param value the new value to replace the old one
	 * @return {@code true} indicating the put operation succeeded
	 */
	public boolean put(K key, V value) {
		data.replace(key, value);
		notifyCacheHandler(key);
		return true;
	}
	
	/**
	 * Deletes the cache entry associated with the given key.
	 * Notifies cache handlers after deletion.
	 * 
	 * @param key the key whose entry is to be removed
	 * @return {@code true} indicating the delete operation succeeded
	 */
	public boolean delete(K key) {
		data.remove(key);
		notifyCacheHandler(key);
		return true;
	}
	
	/**
	 * Retrieves the cached value for the specified key.
	 * If the value is not found in memory, triggers {@link #source()} to
	 * refresh cache from the underlying source, then retries to fetch the value.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the cached value associated with the key, or {@code null} if not found
	 */
	public V get(K key) {
		V result = data.get(key);
		if (result == null) {
			source();
			result = data.get(key);
		}
		return result;
	}
	
	public Set<K> keys(){
		return data.keySet();
	}
}

package com.api.hub.gateway.cache;

import java.util.List;
import java.util.Set;

/**
 * Cache interface designed for fast in-memory access and modular extension
 * within the Enterprise AI Agent Execution and Orchestration Platform.
 * <p>
 * This interface provides basic cache operations and acts as the primary
 * abstraction used by agent-level or environment-level logic to store,
 * retrieve, and manage cached data.
 * </p>
 *
 * <p>
 * Implementations of this interface are typically backed by an in-memory
 * store (e.g., {@code HashMap}) for fast read/write access, and optionally
 * support syncing with an external cache source such as Redis, NoSQL stores,
 * or file systems via {@link CacheOperations}.
 * </p>
 *
 * <p>
 * This design is crucial for maintaining isolated and efficient state management
 * across multiple AI agents or chat sessions in a scalable, multi-tenant system.
 * </p>
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @see CacheOperations
 * @see com.api.hub.ai.handler.impl.EnvironmentState
 */
public interface Cache<K, V> {

    /**
     * Save a new key-value pair into the cache.
     * 
     * @param key   the key to be stored
     * @param value the value to be associated with the key
     * @return {@code true} if the operation was successful, {@code false} otherwise
     */
    boolean save(K key, V value);

    /**
     * Retrieve the value associated with the given key.
     * 
     * @param key the key whose associated value is to be returned
     * @return the cached value, or {@code null} if no mapping exists
     */
    V get(K key);

    /**
     * Update the value associated with the given key.
     * If the key does not exist, it behaves like {@code save}.
     *
     * @param key   the key whose value is to be updated
     * @param value the new value
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean put(K key, V value);

    /**
     * Remove the entry for the specified key from the cache.
     * 
     * @param key the key whose mapping is to be removed
     * @return {@code true} if the key existed and was removed, {@code false} otherwise
     */
    boolean delete(K key);
    
    Set<K> keys();
    
    boolean close();
}
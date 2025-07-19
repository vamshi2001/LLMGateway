package com.api.hub.gateway.cache;

/**
 * Defines operations for synchronizing cache data with an underlying
 * persistent or distributed cache source in the AI-Agent Execution and Orchestration Platform.
 * 
 * <p>
 * This interface separates the responsibilities of the low-level cache
 * synchronization logic from the business-level cache usage represented by
 * {@link Cache}. It is primarily used to handle interaction with the actual
 * cache backend or external cache servers (e.g., Redis, NoSQL, distributed caches).
 * </p>
 * 
 * <p>
 * Implementations of this interface are responsible for:
 * </p>
 * <ul>
 *   <li>Loading data from the source cache system into memory.</li>
 *   <li>Writing updates back to the source cache or persistent store.</li>
 *   <li>Refreshing cached data based on configurable policies or triggers.</li>
 *   <li>Clearing or resetting cache content when required.</li>
 *   <li>Maintaining timestamps for the last refresh to optimize sync frequency.</li>
 *   <li>Notifying handlers about changes to specific cache keys.</li>
 * </ul>
 * 
 * <p>
 * Within the AI-agent framework, the cache synchronization mechanism ensures
 * that agent environment states, histories, and variables remain consistent
 * and performant across multiple distributed components or instances.
 * </p>
 * 
 * @param <K> the type of keys maintained by this cache operation
 * @param <V> the type of mapped values
 * 
 * @see Cache
 * @see com.api.hub.ai.cache.impl.InMemoryCache
 */
public interface CacheOperations<K,V> {

    /**
     * Loads or initializes the cache data from the external source.
     * 
     * @return true if the source load was successful; false otherwise
     */
    public boolean source();

    /**
     * Persists or syncs the cache entry identified by the given key
     * back to the external cache or persistent store.
     * 
     * @param key the key identifying the cache entry to sync
     * @return true if the sink operation was successful; false otherwise
     */
    public boolean sink(K key);

    /**
     * Refreshes the cache content, possibly by reloading or syncing
     * data from the external source.
     * 
     * @return true if the refresh was successful; false otherwise
     */
    public boolean refresh();

    /**
     * Clears or resets the cache contents completely.
     * 
     * @return true if the clear operation was successful; false otherwise
     */
    public boolean clear();

    /**
     * Returns the timestamp (in milliseconds) of the last successful
     * cache refresh operation.
     * 
     * @return last refresh time as a long value representing epoch millis
     */

    /**
     * Notifies or triggers cache handlers or listeners about an update
     * to the cache entry identified by the specified key.
     * 
     * @param key the key whose change should be notified
     */
    public void notifyCacheHandler(K key);
    
    public long minRefreshTime();

}
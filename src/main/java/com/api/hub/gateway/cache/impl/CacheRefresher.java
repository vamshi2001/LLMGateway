package com.api.hub.gateway.cache.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.CacheOperations;
import com.api.hub.gateway.constants.MarkerConstants;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A scheduled component responsible for managing and refreshing multiple cache instances
 * within the AI-Agent Execution and Orchestration Platform.
 * 
 * <p>
 * This class maintains a registry of caches implementing {@link CacheOperations} interface
 * and periodically triggers their refresh processes based on configurable time intervals.
 * It ensures that all registered caches stay synchronized and up-to-date with their
 * external or underlying cache sources, improving cache consistency across the framework.
 * </p>
 * 
 * <p>
 * Refresh operations are performed only if the minimum configured refresh interval
 * has elapsed since the last refresh of each cache instance, preventing excessive refreshes.
 * </p>
 * 
 * <p>
 * This class is enabled conditionally via Spring properties:
 * </p>
 * <ul>
 *   <li><code>cache.enabled=true</code></li>
 *   <li><code>cache.default.refresher.enabled=true</code></li>
 * </ul>
 * <p>
 * If these properties are not set, the refresher will not be initialized.
 * </p>
 * 
 * <p>
 * Configuration properties:
 * </p>
 * <ul>
 *   <li><code>cache.min.refreshTime.ms</code> - minimum time in milliseconds between successive refreshes for a cache (default: 5000 ms)</li>
 *   <li><code>cache.refreshTime.sec</code> - interval in seconds at which the scheduled refresh runs (default: 300 seconds)</li>
 * </ul>
 * 
 * <p>
 * Typical usage involves calling {@link #registerCache(CacheOperations)} during cache initialization
 * to include the cache instance in the periodic refresh cycle.
 * </p>
 * 
 * @see CacheOperations
 */
@Component
@ConditionalOnProperty(
        name = {"cache.enabled", "cache.default.refresher.enabled"},
        havingValue = "true"
)
@Slf4j
public class CacheRefresher implements MarkerConstants {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    /**
     * Internal list of registered caches to be refreshed periodically.
     */
    private List<CacheOperations<?,?>> cacheList = new LinkedList<>();
    private Map<CacheOperations<?,?>, ScheduledFuture<?>> cacheMap = new HashMap<CacheOperations<?,?>, ScheduledFuture<?>>();

    /**
     * Registers a cache instance implementing {@link CacheOperations} for periodic refresh.
     * 
     * @param cache the cache instance to register, must not be null
     */
    public void registerCache(@NonNull CacheOperations<?,?> cache) {
    	
    	cacheMap.put(cache, scheduler.scheduleWithFixedDelay(() -> cache.refresh(), cache.minRefreshTime(), cache.minRefreshTime(), TimeUnit.MILLISECONDS));
    }
    
    public void removeCache(@NonNull CacheOperations<?,?> cache) {
    	ScheduledFuture<?>  future = cacheMap.remove(cache);
    	future.cancel(true);
    }
}
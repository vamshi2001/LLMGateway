package com.api.hub.gateway.cache;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.api.hub.gateway.cache.impl.CacheRefresher;
import com.api.hub.gateway.cache.impl.InMemoryCache;

import jakarta.annotation.PostConstruct;

public abstract class AbstractCacheOperations<K,V> extends InMemoryCache<K,V> {

	@Autowired
	protected CacheRefresher refresher;
	
	@Value("${cache.syncOnChange:false}")
	protected boolean syncOnChange;
	
	protected Queue<K> keysToUpdate = new LinkedBlockingQueue<K>();
	
	@PostConstruct
	public void intit() {
		refresher.registerCache(this);
		source();
	}
	
	@Override
	public synchronized boolean refresh() {
		
		
		while(!keysToUpdate.isEmpty()) {
		
			K key = keysToUpdate.poll();
			sink(key);
		}
		clear();
		source();
			
		return true;
	}

	@Override
	public void notifyCacheHandler(K key) {
		
		if(syncOnChange) {
			sink(key);
		}else {
			keysToUpdate.add(key);
		}
	}
	
	
	@Override
	public boolean close() {
		refresher.removeCache(this);
		data.clear();
		return true;
	}
	
}

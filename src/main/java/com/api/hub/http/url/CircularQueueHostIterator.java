package com.api.hub.http.url;

import java.util.Iterator;
import java.util.List;

import lombok.NonNull;

public class CircularQueueHostIterator implements Iterator<String>{

	private List<String> urls;
	private int lastIndex;
	private int currentIndex = 0;
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String next() {
		if(currentIndex >= lastIndex) currentIndex = 0;
		String url = urls.get(currentIndex);
		currentIndex++;
		return url;
	}

	public CircularQueueHostIterator(@NonNull List<String> urls) {
		super();
		this.urls = urls;
		lastIndex = urls.size();
	}

}

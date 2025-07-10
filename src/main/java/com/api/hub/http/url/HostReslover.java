package com.api.hub.http.url;

import java.util.Iterator;

public interface HostReslover {

	void init(String name);
	Iterator<String> getIter();
	String getAllURLS();
}

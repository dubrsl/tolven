/*
 * Copyright (C) 2010 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author John Churin
 * @version $Id$
 */  

package org.tolven.restful.client;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultCookieStore implements CookieStore {

	Map<String, HttpCookie> cookieMap;

	public DefaultCookieStore() {
		reset();
	}
	
	protected void reset() {
		cookieMap = new HashMap<String, HttpCookie>();
	}
	
	@Override
	public void add(URI uri, HttpCookie cookie) {
		cookieMap.put(uri.getHost()+cookie.getPath(), cookie);
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		String target = uri.getHost()+uri.getPath();
		for (Entry<String, HttpCookie> entry : cookieMap.entrySet()) {
			if (target.startsWith(entry.getKey())) {
				cookies.add(entry.getValue());
			}
		}
		return cookies;
	}

	@Override
	public List<HttpCookie> getCookies() {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		for (Entry<String, HttpCookie> entry : cookieMap.entrySet()) {
			cookies.add(entry.getValue());
		}
		return cookies;
	}

	@Override
	public List<URI> getURIs() {
		List<URI> uris = new ArrayList<URI>();
		for (Entry<String, HttpCookie> entry : cookieMap.entrySet()) {
			URI uri;
			try {
				uri = new URI(entry.getKey());
				uris.add(uri);
			} catch (URISyntaxException e) {
			}
		}
		return uris;
	}

	@Override
	public boolean remove(URI uri, HttpCookie cookie) {
		return cookieMap.remove(uri) != null;
	}

	@Override
	public boolean removeAll() {
		boolean deleted = cookieMap.size() > 0;
		reset();
		return deleted;
	}
}

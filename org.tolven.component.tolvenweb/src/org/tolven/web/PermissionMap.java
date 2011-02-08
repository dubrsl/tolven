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
 * @author <your name>
 * @version $Id: PermissionMap.java,v 1.1 2010/03/20 05:37:57 jchurin Exp $
 */  

package org.tolven.web;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class PermissionMap implements Map<String, Boolean> {

	HttpServletRequest request;
	
	public PermissionMap(HttpServletRequest request) {
		super();
		this.request = request;
	}

	@Override
	public Boolean get(Object key) {
		return request.isUserInRole(key.toString());
	}

	@Override
	public void clear() {
		
	}

	@Override
	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Boolean>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean put(String key, Boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Boolean> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<Boolean> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

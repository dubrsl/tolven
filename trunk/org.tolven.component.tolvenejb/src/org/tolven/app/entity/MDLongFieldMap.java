/*
 * Copyright (C) 2009 Tolven Inc

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
 * @version $Id: MDLongFieldMap.java,v 1.1 2009/11/06 20:01:10 jchurin Exp $
 */  

package org.tolven.app.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MDLongFieldMap extends MDFieldMap implements Map<String, Long> {

	public MDLongFieldMap(MenuData md) {
		super(md);
	}

	@Override
	public Set<java.util.Map.Entry<String, Long>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long get(Object key) {
		return (Long) this.getField(key.toString());
	}

	@Override
	public Long put(String key, Long value) {
		return (Long) putField( key, value );
	}

	@Override
	public void putAll(Map<? extends String, ? extends Long> m) {

	}

	@Override
	public Collection<Long> values() {
		return null;
	}
	@Override
	public Long remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

}

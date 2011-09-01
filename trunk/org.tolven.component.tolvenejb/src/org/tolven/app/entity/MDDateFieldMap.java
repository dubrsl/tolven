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
 * @version $Id: MDDateFieldMap.java,v 1.1 2009/11/06 20:01:10 jchurin Exp $
 */  

package org.tolven.app.entity;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class MDDateFieldMap extends MDFieldMap implements Map<String, Date> {

	public MDDateFieldMap(MenuData md) {
		super(md);
	}

	@Override
	public Date get(Object key) {
		return (Date) this.getField(key.toString());
	}

	@Override
	public Date put(String key, Date value) {
		return (Date) putField( key, value );
	}

	@Override
	public void putAll(Map<? extends String, ? extends Date> m) {

	}

	@Override
	public Collection<Date> values() {
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<String, Date>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Date remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

}

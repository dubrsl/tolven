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
 * @version $Id: PlaceholderIDMap.java,v 1.1 2009/03/24 14:34:12 jchurin Exp $
 */  

package org.tolven.app.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Treat a placeholder id set as a map with the root being the key and the extension being the value.
 * @author John Churin
 *
 */
public class PlaceholderIDMap implements Map<String, PlaceholderID> {
	Set<PlaceholderID> placeholderIDSet;
	
	public PlaceholderIDMap( Set<PlaceholderID> placeholderIDSet) {
		this.placeholderIDSet = placeholderIDSet;
	}
	
	@Override
	public void clear() {
		placeholderIDSet.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		for (PlaceholderID id : placeholderIDSet) {
			if (id.getRoot().equals(key)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Return true if the supplied extension is present. This method is not practical in that
	 * an extension normally has no meaning without the root.
	 */
	@Override
	public boolean containsValue(Object value) {
		for (PlaceholderID id : placeholderIDSet) {
			if (id.getExtension().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, PlaceholderID>> entrySet() {
		Set<java.util.Map.Entry<String, PlaceholderID>> set = new HashSet<java.util.Map.Entry<String, PlaceholderID>>();
		for (PlaceholderID id : placeholderIDSet) {
			java.util.Map.Entry<String, PlaceholderID> entry = new java.util.AbstractMap.SimpleEntry<String, PlaceholderID>(id.getRoot(), id);
			set.add(entry);
		}
		return set;
	}

	@Override
	public PlaceholderID get(Object key) {
		for (PlaceholderID id : placeholderIDSet) {
			if (id.getRoot().equals(key)) {
				return id;
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return placeholderIDSet.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return null;
	}

	@Override
	public PlaceholderID put(String key, PlaceholderID value) {
		throw new RuntimeException( "put not supported in PlaceholderIDMap");
	}

	@Override
	public void putAll(Map<? extends String, ? extends PlaceholderID> m) {
		throw new RuntimeException( "put not supported in PlaceholderIDMap");
	}

	@Override
	public PlaceholderID remove(Object key) {
		for (PlaceholderID id : placeholderIDSet) {
			if (id.getRoot().equals(key)) {
				placeholderIDSet.remove(id);
				return id;
			}
		}
		return null;
	}

	@Override
	public int size() {
		return placeholderIDSet.size();
	}

	@Override
	public Collection<PlaceholderID> values() {
		return placeholderIDSet;
	}

}

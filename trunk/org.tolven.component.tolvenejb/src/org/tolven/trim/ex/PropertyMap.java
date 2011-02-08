package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.Compute.Property;
/**
 * Return the first property of a Compute with the given name
 * 
 * @author John Churin
 */
@SuppressWarnings("serial")
public class PropertyMap implements Map<String, Object> {
	private ComputeEx computeEx;
	
	public PropertyMap( ComputeEx computeEx) {
		this.computeEx = computeEx;
	}

	@Override
	public Object put(String key, Object value) {
		for (Property property : computeEx.getProperties()) {
			if(property.getName().equals(key)) {
				Object prevValue = property.getValue();
				property.setValue(value);
				return prevValue;
			}
		}
		// Add a new field
		Property property = new Property();
		property.setName(key);
		property.setValue(value);
		computeEx.getProperties().add(property);
		return null;
	}

	@Override
	public Object get(Object key) {
		return computeEx.getPropertyValue(key.toString());
	}

	
	//*************************************
	// The methods below contain just default stuff
	//*************************************

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
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
	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

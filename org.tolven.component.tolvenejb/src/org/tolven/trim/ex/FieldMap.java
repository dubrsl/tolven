package org.tolven.trim.ex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.Field;
/**
 * Provide simple access to a map of generic field values in a trim.
 * @author John Churin
 */
public class FieldMap implements Map<String, Object> {
	private TrimEx trimEx;
	private static final TrimFactory trimFactory = new TrimFactory();
	
	public FieldMap( TrimEx trimEx) {
		this.trimEx = trimEx;
	}
	@Override
	public boolean containsKey(Object key) {
		for (Field field : trimEx.getFields()) {
			if(field.getName().equals(key)) return true;
		}
		return false;
	}


	@Override
	public Object put(String key, Object value) {
		for (Field field : trimEx.getFields()) {
			if(field.getName().equals(key)) {
				Object prevValue = field.getValue();
				field.setValue(value);
				return prevValue;
			}
		}
		// Add a new field
		Field field = trimFactory.createField();
		field.setName(key);
		field.setValue(value);
		trimEx.getFields().add(field);
		return null;
	}

	@Override
	public Object get(Object key) {
		return trimEx.getFieldValue(key.toString());
	}

	
	//*************************************
	// The methods below contain just default stuff
	//*************************************

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Map<String,Object> fields = new HashMap<String, Object>();
		for (Field field : trimEx.getFields()) {
			fields.put(field.getName(),field.getValue());
		}
		return fields.entrySet();
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
		Collection<Object> values = new ArrayList<Object>();
		for (Field field : trimEx.getFields()) {
			values.add(field.getValue());
		}
		return values;
	}	
	
}

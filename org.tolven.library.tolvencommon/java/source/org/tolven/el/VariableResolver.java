package org.tolven.el;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

public class VariableResolver extends ELResolver {
	private Stack<Map<Object,Object>> mapStack = new Stack<Map<Object,Object>>();

	public VariableResolver() {
		mapStack.push( new HashMap<Object,Object>() );
	}
	
	/**
	 * Get a value from one of the maps in out stack of maps.
	 * @param key
	 * @return value or null
	 */
	public Object get( Object key) {
		// Must iterate from last to first
		for( int x = mapStack.size()-1; x >= 0; x--) {
			Map<Object,Object> map = mapStack.get(x);
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return null;
	}
	
	/**
	 * See if the key is found in one of the maps in out stack of maps.
	 * @param key
	 * @return value or null
	 */
	public boolean containsKey( Object key) {
		// Must iterate from last to first
		for( int x = mapStack.size()-1; x >= 0; x--) {
			Map<Object,Object> map = mapStack.get(x);
			if (map.containsKey(key)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Pop the most recent map from the stack
	 * @return
	 */
	public Map<Object,Object> popMap( ) {
		if (mapStack.size() < 2) {
			throw new IllegalStateException( "Stack underflow in variable maps" );
		}
		return mapStack.pop();
	}
	
	/**
	 * Create a new map and push it onto the stack of maps, return the variable map just created.
	 * The variable maps are searched starting with the last one to be pushed on the stack.
	 */ 
	public Map<Object,Object> pushMap( ) {
		Map<Object,Object> map = new HashMap<Object,Object>();
		mapStack.push(map);
		return map;
	}
	
	public Map<Object,Object> getMap( ) {
		return mapStack.peek();
	}
	
	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		if (base==null) {
			context.setPropertyResolved(true);
			return Object.class;
		}
		return null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		if (base==null) {
			context.setPropertyResolved(true);
			return Object.class;
		}
		return null;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		if (base==null) {
			context.setPropertyResolved(true);
			return get( property );
		}
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		return false;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (base==null) {
			context.setPropertyResolved(true);
			Map<Object,Object> map = mapStack.peek();
			map.put(property, value);
		}
	}


}

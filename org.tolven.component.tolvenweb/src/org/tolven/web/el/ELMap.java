package org.tolven.web.el;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
/**
 * An implementation of Map that does nothing except to allow overriding of the get method.
 * This is typically used to implement xxx[yyyy] el semantics.
 * @author John Churin
 *
 */
public abstract class ELMap implements Map<String, String> {

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
	public Set<java.util.Map.Entry<String, String>> entrySet() {
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
	public String put(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> values() {
		// TODO Auto-generated method stub
		return null;
	}


}

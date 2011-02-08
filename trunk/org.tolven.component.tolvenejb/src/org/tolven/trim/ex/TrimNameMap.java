package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.Trim;
/**
 * This class return the trim object if the name matches, otherwise null. The key can be a regular expression.
 * @author John Churin
 */
public class TrimNameMap implements Map<String, Trim> {
	private Trim trim;
	
	public TrimNameMap( Trim trim) {
		this.trim = trim;
	}

	@Override
	public Trim get(Object key) {
		// No name = no match
		if( trim.getName()==null) return null;
		// Straight string match
		if (key.equals(trim.getName())) return trim;
		// Allow Regular expression as well
		if (trim.getName().matches(((String)key))) return trim;
		// No match
		return null;
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
	public Set<java.util.Map.Entry<String, Trim>> entrySet() {
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
	public Trim put(String key, Trim value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Trim> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Trim remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<Trim> values() {
		// TODO Auto-generated method stub
		return null;
	}


}

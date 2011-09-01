package org.tolven.trim.ex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolven.app.el.ELFunctions;
import org.tolven.core.entity.Account;
import org.tolven.trim.II;

public class ForIIMap implements Map<String, II> {
	SETIISlotEx slot;
	
	public ForIIMap( SETIISlotEx slot ) {
		super();
		this.slot = slot;
	}
	protected String computeRoot( Object key ) {
		if (key instanceof Account) {
			return ELFunctions.computeIDRoot( (Account) key );
		} else {
			return key.toString();
		}
	}

	@Override
	public II get(Object key) {
		String root = computeRoot( key );
		List<II> iis = slot.getIIS();
		for (II ii : iis) {
			if (root.equals(ii.getRoot())) { 
				return ii;
			}
		}
		II ii = new II( );
		ii.setRoot( root );
		iis.add(ii);
		return ii;
	}
	
	@Override
	public boolean containsKey(Object key) {
		String root = computeRoot( key );
		List<II> iis = slot.getIIS();
		for (II ii : iis) {
			if (root.equals(ii.getRoot())) { 
				return true;
			}
		}
		return false;
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
	public Set<java.util.Map.Entry<String, II>> entrySet() {
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
	public II put(String key, II value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends II> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public II remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<II> values() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

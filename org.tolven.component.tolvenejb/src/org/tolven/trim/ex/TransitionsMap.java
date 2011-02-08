package org.tolven.trim.ex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.Transition;

/**
 * This class presents an underlying list of participations as map.
 * @author John Churin
 *
 */
public class TransitionsMap implements Map<String, Transition> {

	private List<Transition> list;
	public TransitionsMap( List<Transition> list ) {
		this.list = list;
	}
	
	@Override
	public Transition get(Object key) {
    	for (Transition trans : list) {
    		if (key.equals(trans.getName())) return trans;
    	}
    	return null;
	}
	
	@Override
	public Transition put(String key, Transition value) {
		if (containsValue( value)) return null;
		Transition removed = remove( key );
		list.add(value);
		return removed;
	}

	@Override
	public boolean containsValue(Object value) {
    	for (Transition ap : list) {
    		if (value == ap) return true;
    	}
		return false;
	}
	
	@Override
	public boolean containsKey(Object key) {
		if (get( key )==null) return false;
		return true;
	}

	@Override
	public Transition remove(Object key) {
    	for (Transition ap : list) {
    		if (key.equals(ap.getName())) {
    			list.remove(ap);
    			return ap;
    		}
    	}
		return null;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Set<java.util.Map.Entry<String, Transition>> entrySet() {
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
	public void putAll(Map<? extends String, ? extends Transition> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Transition> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

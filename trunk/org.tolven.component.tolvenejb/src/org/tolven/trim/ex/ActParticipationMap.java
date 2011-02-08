package org.tolven.trim.ex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.ActParticipation;

/**
 * This class presents an underlying list of participations as map.
 * @author John Churin
 *
 */
public class ActParticipationMap implements Map<String, ActParticipation> {

	private List<ActParticipation> list;
	public ActParticipationMap( List<ActParticipation> list ) {
		this.list = list;
	}
	@Override
	public ActParticipation get(Object key) {
    	for (ActParticipation ap : list) {
    		if (key.equals(ap.getName())) return ap;
    	}
    	return null;
	}
	
	@Override
	public ActParticipation put(String key, ActParticipation value) {
		if (containsValue( value)) return null;
		ActParticipation removed = remove( key );
		list.add(value);
		return removed;
	}

	@Override
	public boolean containsValue(Object value) {
    	for (ActParticipation ap : list) {
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
	public ActParticipation remove(Object key) {
    	for (ActParticipation ap : list) {
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
	public Set<java.util.Map.Entry<String, ActParticipation>> entrySet() {
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
	public void putAll(Map<? extends String, ? extends ActParticipation> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<ActParticipation> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

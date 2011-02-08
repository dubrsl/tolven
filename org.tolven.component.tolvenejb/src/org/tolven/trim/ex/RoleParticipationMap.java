package org.tolven.trim.ex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.RoleParticipation;

/**
 * This class presents an underlying list of participations as map.
 * @author John Churin
 *
 */
public class RoleParticipationMap implements Map<String, RoleParticipation> {

	private List<RoleParticipation> list;
	public RoleParticipationMap( List<RoleParticipation> list ) {
		this.list = list;
	}
	@Override
	public RoleParticipation get(Object key) {
    	for (RoleParticipation rp : list) {
    		if (key.equals(rp.getName())) return rp;
    	}
    	return null;
	}
	
	@Override
	public RoleParticipation put(String key, RoleParticipation value) {
		if (containsValue( value)) return null;
		RoleParticipation removed = remove( key );
		list.add(value);
		return removed;
	}

	@Override
	public boolean containsValue(Object value) {
    	for (RoleParticipation ap : list) {
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
	public RoleParticipation remove(Object key) {
    	for (RoleParticipation ap : list) {
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
	public Set<java.util.Map.Entry<String, RoleParticipation>> entrySet() {
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
	public void putAll(Map<? extends String, ? extends RoleParticipation> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<RoleParticipation> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

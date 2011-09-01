package org.tolven.trim.ex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolven.trim.ActRelationship;

/**
 * This class presents an underlying list of participations as map.
 * @author John Churin
 *
 */
public class ActRelationshipMap implements Map<String, ActRelationship> {

	private List<ActRelationship> list;
	public ActRelationshipMap( List<ActRelationship> list ) {
		this.list = list;
	}
	@Override
	public ActRelationship get(Object key) {
        Pattern pattern = Pattern.compile("(\\S*)\\[(\\d*)\\]");
        Matcher matcher = pattern.matcher((String)key);
        if (matcher.find()) {
            String name = matcher.group(1).trim();
            int sequence = Integer.parseInt(matcher.group(2).trim());
                for (ActRelationship ap : list) {
                    if (name.equals(ap.getName()) && sequence == ap.getSequenceNumber()) return ap;
                }

            } else {
                for (ActRelationship ap : list) {
                    if (key.equals(ap.getName())) return ap;
                }
        }
        return null;
    }	
	@Override
	public ActRelationship put(String key, ActRelationship value) {
		if (containsValue( value)) return null;
		ActRelationship removed = remove( key );
		list.add(value);
		return removed;
	}

	@Override
	public boolean containsValue(Object value) {
    	for (ActRelationship ap : list) {
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
	public ActRelationship remove(Object key) {
    	for (ActRelationship ap : list) {
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
	public Set<java.util.Map.Entry<String, ActRelationship>> entrySet() {
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
	public void putAll(Map<? extends String, ? extends ActRelationship> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<ActRelationship> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

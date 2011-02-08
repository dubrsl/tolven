package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.AD;
import org.tolven.trim.AddressUse;
import org.tolven.trim.EN;
import org.tolven.trim.EntityNameUse;
/**
 * This class returns an EntityName (EN) with the use code specified in the map parameter.
 * <pre>
 * ...name.EN['L']
 * </pre>
 * returns the EN with a use code of L (legal name) or null if none exists. 
 * 
 * @author John Churin
 */
public class ENSlotMap implements Map<String, EN> {
	private ENSlotEx enSlotEx;
	
	public ENSlotMap( ENSlotEx enSlotEx) {
		this.enSlotEx = enSlotEx;
	}

	@Override
	public EN get(Object key) {
		String keyStr = (String)key;
		int bracket = keyStr.indexOf('[');
		String use;
		int index;
		if (bracket >=0) {
			use = keyStr.substring(0, bracket).trim();
			if (!keyStr.endsWith("]")) {
				throw new IllegalArgumentException( "Missing closing square bracket in array index");
			}
			String indexString = keyStr.substring(bracket+1, keyStr.length()-1);
			index = Integer.parseInt(indexString);
		} else {
			use = keyStr.trim();
			index = 0;
		}
		int indexCount = 0;

		for (EN en  : enSlotEx.getENS()) {
			for (EntityNameUse useItem : en.getUses()) {
				if (useItem.value().equalsIgnoreCase(use)) {
					if ( indexCount>=index) return en;
					indexCount++;
				}
			}
		}
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
	public Set<java.util.Map.Entry<String, EN>> entrySet() {
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
	public EN put(String key, EN value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends EN> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EN remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<EN> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

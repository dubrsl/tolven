package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.ENXPSlot;
/**
 * This class returns a formatted string based on the parts of the name.
 * <pre>
 * ...name.part['GIV']
 * </pre>
 * returns the first Given name parts. 
 * <pre>
 * ...name.part['GIV[0]']
 * </pre>
 * returns the first Given name found in the list of name parts. 
 * 
 * @author John Churin
 */
public class ENPartsMap implements Map<String, ENXPSlot> {
	private ENEx enex;
	
	public ENPartsMap( ENEx enex) {
		this.enex = enex;
	}

	@Override
	public ENXPSlot get(Object key) {
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
		for( ENXPSlot enxp : enex.getParts()) {
			if (use.equalsIgnoreCase(enxp.getType().toString())) {
				// See if this part qualifies by index
				if (indexCount >= index) return enxp;
				indexCount++;
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
	public Set<java.util.Map.Entry<String, ENXPSlot>> entrySet() {
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
	public ENXPSlot put(String key, ENXPSlot value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends ENXPSlot> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ENXPSlot remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<ENXPSlot> values() {
		// TODO Auto-generated method stub
		return null;
	}



}

package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.ADXPSlot;
import org.tolven.trim.ENXPSlot;
/**
 * This class returns a formatted string based on the parts of the address.
 * <pre>
 * ...addr.formattedParts['SAL']
 * </pre>
 * returns a concatenated list of the street address parts. 
 * If more than one address part exists, a space is added between them.
 * <pre>
 * ...addr.formattedParts['SAL CTY']
 * </pre>
 * returns a concatenated list of the street address parts followed by a list of concatenated and city address parts. 
 * <pre>
 * ...addr.formattedParts['SAL[0]']
 * </pre>
 * returns the first street address found in the list of address parts. 
 * 
 * @author John Churin
 */
public class ADPartsMap implements Map<String, ADXPSlot> {
	private ADEx adex;
	
	public ADPartsMap( ADEx adex) {
		this.adex = adex;
	}

	@Override
	public ADXPSlot get(Object key) {
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
		for( ADXPSlot adxp : adex.getParts()) {
			if (use.equalsIgnoreCase(adxp.getType().toString())) {
				// See if this part qualifies by index
				if (indexCount >= index) return adxp;
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
	public Set<java.util.Map.Entry<String, ADXPSlot>> entrySet() {
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
	public ADXPSlot put(String key, ADXPSlot value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends ADXPSlot> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ADXPSlot remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<ADXPSlot> values() {
		// TODO Auto-generated method stub
		return null;
	}



}

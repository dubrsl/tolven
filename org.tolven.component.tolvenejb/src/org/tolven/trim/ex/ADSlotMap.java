package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.AD;
import org.tolven.trim.AddressUse;
import org.tolven.trim.DataType;
import org.tolven.trim.TEL;
import org.tolven.trim.TelecommunicationAddressUse;
/**
 * This class returns an EntityName (EN) with the use code specified in the map parameter.
 * <pre>
 * ...name.AD['L']
 * </pre>
 * returns the AD with a use code of L (legal address) or null if none exists. 
 * 
 * @author John Churin
 */
public class ADSlotMap implements Map<String, AD> {
	private ADSlotEx adSlotEx;
	
	public ADSlotMap( ADSlotEx adSlotEx) {
		this.adSlotEx = adSlotEx;
	}

	@Override
	public AD get(Object key) {
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

		for (AD ad : adSlotEx.getADS()) {
			for (AddressUse useItem : ad.getUses()) {
				if (useItem.value().equalsIgnoreCase(use)) {
					if ( indexCount>=index) return ad;
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
	public Set<java.util.Map.Entry<String, AD>> entrySet() {
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
	public AD put(String key, AD value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends AD> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AD remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<AD> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

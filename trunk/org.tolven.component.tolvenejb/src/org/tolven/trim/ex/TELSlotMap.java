package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.DataType;
import org.tolven.trim.TEL;
import org.tolven.trim.TelecommunicationAddressUse;
/**
 * This class returns an A Telecommunication Address (TEL) with the use code specified in the map parameter.
 * <pre>
 * ...name.TEL['H']
 * </pre>
 * returns the TEL with a use code of H (Home phone) or null if none exists. 
 * 
 * @author John Churin
 */
public class TELSlotMap implements Map<String, TEL> {
	private TELSlotEx telSlotEx;
	
	public TELSlotMap( TELSlotEx telSlotEx) {
		this.telSlotEx = telSlotEx;
	}

	@Override
	public TEL get(Object key) {
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

		for (DataType datatype : telSlotEx.getValues()) {
			if (datatype instanceof TELEx) {
				TELEx tel = (TELEx)datatype;
				if (use.equals(tel.getScheme())) {
					return tel;
				}
				for (TelecommunicationAddressUse useItem : tel.getUses()) {
					if (useItem.value().equalsIgnoreCase(use)) {
						if ( indexCount>=index) return tel;
						indexCount++;
					}
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
	public Set<java.util.Map.Entry<String, TEL>> entrySet() {
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
	public TEL put(String key, TEL value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends TEL> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TEL remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<TEL> values() {
		// TODO Auto-generated method stub
		return null;
	}
	


}

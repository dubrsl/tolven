package org.tolven.trim.ex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.trim.ENXPSlot;
import org.tolven.trim.Field;
import org.tolven.trim.ValueSet;
/**
 * This class returns a formatted string based on the parts of the name.
 * <pre>
 * ...name.formattedParts['GIV']
 * </pre>
 * returns a concatenated list of the Given name name parts. 
 * If more than one name part exists, a space is added between them.
 * <pre>
 * ...name.formattedParts['GIV FAM']
 * </pre>
 * returns a concatenated list of the Given name name parts followed by a list of concatenated family name parts. 
 * <pre>
 * ...name.formattedParts['GIV[0]']
 * </pre>
 * returns the first Given name found in the list of name parts. 
 * 
 * @author John Churin
 */
public class ValueSetMap implements Map<String, ValueSet> {
	private TrimEx trimEx;
	
	public ValueSetMap( TrimEx trimEx) {
		this.trimEx = trimEx;
	}
	
	@Override
	public boolean containsKey(Object key) {
		for (ValueSet vs : trimEx.getValueSets()) {
			if(vs.getName().equals(key)) return true;
		}
		return false;
	}

	@Override
	public ValueSet put(String key, ValueSet value) {
		if (!containsKey( key )) {
			trimEx.getValueSets().add(value);
		}
		return null;
	}

	@Override
	public ValueSet get(Object key) {
		for (ValueSet valueSet : trimEx.getValueSets()) {
			if(valueSet.getName().equals(key)) {
				return valueSet;
			}
		}
		return null;
	}

	
	//*************************************
	// The methods below contain just default stuff
	//*************************************

	@Override
	public ValueSet remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

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
	public Set<java.util.Map.Entry<String, ValueSet>> entrySet() {
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
	public void putAll(Map<? extends String, ? extends ValueSet> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<ValueSet> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

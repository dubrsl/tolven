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
public class ADFormattedPartsMap implements Map<String, String> {
	private ADEx adex;
	
	public ADFormattedPartsMap( ADEx adex) {
		this.adex = adex;
	}

	@Override
	public String get(Object key) {
		StringBuffer sb = new StringBuffer( 128 );
		String parts[] = ((String)key).split( " " );
		for (String partSpec : parts ) {
			int bracket = partSpec.indexOf('[');
			String part;
			int indexLow;
			int indexHigh;
			if (bracket >=0) {
				part = partSpec.substring(0, bracket);
				if (!partSpec.endsWith("]")) {
					throw new IllegalArgumentException( "Missing closing square bracket in array index");
				}
				String indexString = partSpec.substring(bracket+1, partSpec.length()-1);
				indexLow = Integer.parseInt(indexString);
				indexHigh = indexLow;
			} else {
				part = partSpec;
				indexLow = 0;
				indexHigh = 9999999;
			}
			int index = 0;
			for( ADXPSlot adxp : adex.getParts()) {
				if (adxp.getType()!=null && part.equalsIgnoreCase(adxp.getType().toString())) {
					// See if this part qualifies by index
					if (index >= indexLow && index <= indexHigh) {
						// Append space if needed
						if (sb.length()>0 && sb.charAt(sb.length()-1)!=' ') sb.append(" ");
						// Append the part
						sb.append(adxp.getST().getValue());
					}
					index++;
				}
 			}
		}
		return sb.toString();
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
	public Set<java.util.Map.Entry<String, String>> entrySet() {
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
	public String put(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> values() {
		// TODO Auto-generated method stub
		return null;
	}



}

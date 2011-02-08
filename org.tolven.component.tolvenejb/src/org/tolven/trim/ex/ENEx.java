package org.tolven.trim.ex;

import org.tolven.trim.EN;
import org.tolven.trim.ENXPSlot;

public class ENEx extends EN {
	private static final long serialVersionUID = 1L;

	/**
	 * Return a map that will return an array of matching parts
	 * @return
	 */
	public ENFormattedPartsMap getFormattedParts( ) {
		return new ENFormattedPartsMap( this );
	}

	/**
	 * Return a map that will return an array of matching parts
	 * @return
	 */
	public ENPartsMap getPart( ) {
		return new ENPartsMap( this );
	}
	/**
	 * This HL7 property is computed from the Name parts.
	 * @return
	 */
	public String getFormatted() {
		StringBuffer sb = new StringBuffer( 100 );
		for (ENXPSlot part : getParts()) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(part.getST().toString());
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getFormatted();
	}
	
}

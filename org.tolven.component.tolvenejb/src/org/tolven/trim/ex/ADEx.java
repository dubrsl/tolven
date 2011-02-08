package org.tolven.trim.ex;

import org.tolven.trim.AD;
import org.tolven.trim.ADXPSlot;
import org.tolven.trim.ENXPSlot;
@SuppressWarnings("serial")
public class ADEx extends AD {

	/**
	 * Return a map that will return an array of matching parts
	 * @return
	 */
	public ADFormattedPartsMap getFormattedParts( ) {
		return new ADFormattedPartsMap( this );
	}
	
	/**
	 * Return a map that will return an array of matching parts
	 * @return
	 */
	public ADPartsMap getPart( ) {
		return new ADPartsMap( this );
	}

	/**
	 * This HL7 property is computed from the Address parts.
	 * @return
	 */
	public String getFormatted() {
		StringBuffer sb = new StringBuffer( 100 );
		for (ADXPSlot part : getParts()) {
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

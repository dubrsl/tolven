package org.tolven.trim.ex;

import java.util.List;

import org.tolven.trim.DataType;
import org.tolven.trim.TELSlot;
import org.tolven.trim.URL;

public class TELSlotEx extends TELSlot {
	private static final long serialVersionUID = 1L;

	public List<DataType> getValues() {
		return this.getNullsAndURLSAndTELS();
	}
	
	public TELSlotMap getTEL() {
		return new TELSlotMap( this );
	}
	/**
	 * Look for the first URL in the list of values in the slot.
	 * @return URL or null if no URL found
	 */
	public URL getURL() {
		for (DataType dataType : getValues()) {
			if (dataType instanceof URL ) return (URL)dataType;
		}
		return null;
	}
	
	@Override
	public String getDatatype() {
		return "TEL";
	}
	
}

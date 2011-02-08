package org.tolven.trim.ex;

import java.text.ParseException;
import java.util.Date;

import org.tolven.trim.TSSlot;

public class TSSlotEx extends TSSlot {
	private static final long serialVersionUID = 1L;

	public Date getDate( ) throws ParseException {
		if (this.getTS()!=null) {
			return ((TSEx) getTS()).getDate();
		}
		return null;
	}
	
	@Override
	public String getDatatype() {
		return "TS";
	}

}

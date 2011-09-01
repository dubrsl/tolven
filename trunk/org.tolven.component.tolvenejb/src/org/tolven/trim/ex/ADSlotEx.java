package org.tolven.trim.ex;

import org.tolven.trim.ADSlot;

public class ADSlotEx extends ADSlot {
	private static final long serialVersionUID = 1L;
	
	public ADSlotMap getAD() {
		return new ADSlotMap( this );
	}
	@Override
	public String getDatatype() {
		return "AD";
	}

}

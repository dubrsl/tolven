package org.tolven.trim.ex;

import org.tolven.trim.ENSlot;

public class ENSlotEx extends ENSlot {
	private static final long serialVersionUID = 1L;
	public ENSlotMap getEN() {
		return new ENSlotMap( this );
	}

	@Override
	public String getDatatype() {
		return "EN";
	}
}

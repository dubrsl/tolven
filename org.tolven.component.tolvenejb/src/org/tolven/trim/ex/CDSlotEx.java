package org.tolven.trim.ex;

import org.tolven.trim.CD;
import org.tolven.trim.CDSlot;
import org.tolven.trim.CE;
import org.tolven.trim.DataType;
import org.tolven.trim.NullFlavor;

public class CDSlotEx extends CDSlot {
	
	public DataType getValue( ) {
		if (getCD()!=null) {
			return getCD();
		}
		if (getCE()!=null) {
			return getCE();
		}
		if (getFlavor()!=null) {
			return getFlavor();
		}
		return null;
	}

	public void setValue( DataType value ) {
		setCD( null );
		setCE( null );
		setFlavor( null );
		if (value instanceof CE ) {
			setCE( (CE) value );
		} else if ( value instanceof CD) {
			setCD( (CD)value);
		} else if ( value instanceof NullFlavor) {
			setFlavor( (NullFlavor)value);
		}
	}

	@Override
	public String getDatatype() {
		return "CD";
	}
	
}

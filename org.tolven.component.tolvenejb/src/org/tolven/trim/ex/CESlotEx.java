package org.tolven.trim.ex;

import org.tolven.trim.CE;
import org.tolven.trim.CESlot;
import org.tolven.trim.DataType;
import org.tolven.trim.NullFlavor;

public class CESlotEx extends CESlot {

	@Override
	public String toString() {
		if (this.getCE()!=null) return getCE().toString();
		if (this.getNull()!=null) return getNull().toString();
		return null;
	}

	@Override
	public String getDatatype() {
		return "CE";
	}
	public DataType getValue( ) {
		if (getCE()!=null) {
			return getCE();
		}
		if (getNull()!=null) {
			return getNull();
		}
		return null;
	}

	public void setValue( DataType value ) {
		setCE( null );
		setNull( null );
		if (value instanceof CE ) {
			setCE( (CE) value );
		} else if ( value instanceof NullFlavor) {
			setNull( (NullFlavor)value);
		}
	}
	
}

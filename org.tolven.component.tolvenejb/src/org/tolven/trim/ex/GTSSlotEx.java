package org.tolven.trim.ex;

import org.tolven.trim.DataType;
import org.tolven.trim.GTSSlot;
import org.tolven.trim.IVLTS;
import org.tolven.trim.NullFlavor;
import org.tolven.trim.TS;

public class GTSSlotEx extends GTSSlot {

	@Override
	public String getDatatype() {
		if (this.ts!=null) return "TS";
		if (this.ivlts!=null) return "IVL_TS";
		if (this.setts!=null) return "SET_TS";
		if (this._null!=null) return "NULL";
		return "GTS";
	}

	public DataType getValue( ) {
		if (getTS()!=null) {
			return getTS();
		}
		if (getIVLTS()!=null) {
			return getIVLTS();
		}
		// TODO: BUG in trim4 - HL7 SETs need to be datatypes?
//		if (getSETTS()!=null) {
//			return getSETTS();
//		}
		if (getNull()!=null) {
			return getNull();
		}
		return null;
	}

	public void setValue( DataType value ) {
		setTS( null );
		setIVLTS( null );
		getSETTS().clear();
		setNull( null );
		if (value instanceof TS ) {
			setTS( (TS) value );
		} else if ( value instanceof IVLTS) {
			setIVLTS( (IVLTS)value);
		} else if ( value instanceof NullFlavor) {
			setNull( (NullFlavor)value);
		}
	}

}

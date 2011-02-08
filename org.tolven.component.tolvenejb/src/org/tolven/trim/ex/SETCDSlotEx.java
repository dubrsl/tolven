package org.tolven.trim.ex;

import org.tolven.trim.CD;
import org.tolven.trim.CE;
import org.tolven.trim.DataType;
import org.tolven.trim.NullFlavor;
import org.tolven.trim.SETCDSlot;

@SuppressWarnings("serial")
public class SETCDSlotEx extends SETCDSlot {
	/**
	 * For this method, treat SETCD like a scalar and we only need the CEs collection.
	 * @return
	 */
	public DataType getValue( ) {
		if (getCES().size() > 0) {
			return getCES().get(0);
		}
		if (getFlavor()==null) {
			return getFlavor();
		}
		return null;
	}

	public void setValue( DataType value ) {
		getCES( ).clear();
		setFlavor( null );
		if (value instanceof CE ) {
			getCES().add((CE) value );
		} else if ( value instanceof CD) {
			getCES().add((CD)value);
		} else if ( value instanceof NullFlavor) {
			setFlavor( (NullFlavor)value);
		}
	}
	@Override
	public String getDatatype() {
		return "SET_CD";
	}

}

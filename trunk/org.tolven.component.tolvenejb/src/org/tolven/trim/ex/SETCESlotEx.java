package org.tolven.trim.ex;

import java.util.List;

import org.tolven.trim.CE;
import org.tolven.trim.DataType;
import org.tolven.trim.SETCESlot;

public class SETCESlotEx extends SETCESlot {
	private static final long serialVersionUID = 1L;

	/**
	 * For this method, treat SETCE like a scalar and we only need the CEs collection.
	 * @return
	 */
	public DataType getValue( ) {
		if (getCES().size() > 0) {
			return getCES().get(0);
		}
		return null;
	}

	public void setValue( DataType value ) {
		getCES( ).clear();
		if (value instanceof CE ) {
			getCES().add((CE) value );
		}
	}
	
	public List<CE> getValues( ) {
		return getCES(); 
	}

	public void setValues( List<DataType> values ) {
		getCES( ).clear();
		for ( DataType value : values) {
			if (value instanceof CE ) {
				getCES().add( (CE)value );
			}
		}
	}
	
	@Override
	public String getDatatype() {
		return "SET_CE";
	}

	@Override
	public String toString() {
		if (this.getOriginalText()!=null) return this.getOriginalText();
		StringBuffer sb = new StringBuffer( 100 );
		for (CE ce : getCES() ) {
			if (sb.length() > 0) sb.append( ", " );
			sb.append( ce.toString());
		}
		return sb.toString();
	}

}

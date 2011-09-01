package org.tolven.trim.ex;

import java.io.Serializable;

import org.tolven.trim.ED;

@SuppressWarnings("serial")
public class EDEx extends ED implements Serializable {
	/**
	 * Store a simple string in the value (stored as a byte[]
	 * @param value
	 */
	public void setStringValue(String value) {
		if (value!=null) {
			setValue( value.getBytes());
		} else {
			setValue( null );
		}
	}
	
	/**
	 * Return the byte[] array as a string.
	 * TODO: This should complain if the media type is not text/*.
	 * @return
	 */
	public String getStringValue() {
		if (getValue()==null) return null;
		return new String( getValue() );
	}

	@Override
	public String toString() {
		return getStringValue();
	}
	
	
}

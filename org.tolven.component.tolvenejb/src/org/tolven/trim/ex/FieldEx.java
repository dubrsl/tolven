/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author <your name>
 * @version $Id: FieldEx.java,v 1.3 2009/12/12 18:25:58 jchurin Exp $
 */  

package org.tolven.trim.ex;

import org.tolven.trim.Field;

public class FieldEx extends Field {
	transient private static final TrimFactory trimFactory = new TrimFactory();
	
	/**
	 * If the value to be stored is an array, then store the array
	 */
	@Override
	public void setValue(Object value) {
		if (value !=null && value.getClass().isArray()) {
			super.setValue(null);
			Field.Values fv = trimFactory.createFieldValues();
			Object[] array = (Object[])value;
			for (Object val : array) {
				fv.getValues().add(val);
			}
			setValues(fv);
		} else {
			super.setValue(value);
		}
	}
	
	/**
	 * If the stored value is an array, then return the array
	 */
	@Override
	public Object getValue() {
		if (getValues()!=null) {
			return getValues().getValues().toArray();
		}
		// Default behavior
		return super.getValue();
	}

	@Override
	public String toString() {
		return getName() + "=" + getValue();
	}

}

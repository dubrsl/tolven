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
 * @version $Id: TELEx.java,v 1.3 2009/03/17 11:45:34 jchurin Exp $
 */  

package org.tolven.trim.ex;

import org.tolven.trim.TEL;

public class TELEx extends TEL {
	/**
	 * Return the scheme portion of the telecom address. For example:
	 * tel:1234567 returns tel.
	 * If the TEL does not contain a colon, then null is returned.
	 * @return
	 */
	public String getScheme() {
		String value = getValue();
		if (value==null) return null;
		int colon = value.indexOf(":");
		if (colon < 0) return null;
		return value.substring(0,colon);
	}
	
	/**
	 * Return the scheme portion of the telecom address. For example:
	 * tel:1234567 returns 1234567.
	 * If the TEL does not contain a colon, then the entire value is returned.
	 * @return
	 */
	public String getAddress() {
		String value = getValue();
		if (value==null) return null;
		int colon = value.indexOf(":");
		if (colon < 0) return value;
		String address = value.substring(colon+1);
		if (address.length()==0) {
			return null;
		} else {
			return address;
		}
	}
	
	public void setScheme(String scheme) {
		if (scheme==null) {
			setValue(getAddress());
		} else {
			String address = getAddress();
			if (address!=null) {
				setValue( scheme + ":" + getAddress() );
			} else {
				setValue( scheme + ":" );
			}
		}
	}
	
	public void setAddress(String address) {
		String scheme = getScheme();
		if (scheme!=null) {
			setValue( getScheme() + ":" + address );
		} else {
			setValue( address );
		}
	}
	
}

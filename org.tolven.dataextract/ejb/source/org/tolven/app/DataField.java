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
 * @author John Churin
 * @version $Id: DataField.java,v 1.2 2009/08/21 04:27:51 jchurin Exp $
 */  

package org.tolven.app;

import java.io.Serializable;

/**
 * A compact representation of fields to be queried including both internal and external fields. 
 * The natural sort order is by external field name, which can have dot separated segments.
 * A field is also assigned a presentation order which can be used to change the order in which
 * fields are later returned.
 * @author John Churin
 *
 */
@SuppressWarnings("serial")
public class DataField implements Serializable, Comparable<DataField> {

	private String internal[];
	private String external[];
	private boolean enabled;
	
	/**
	 * Construct a new DataField. the field is initially enabled and assigned a presentationOrder of zero.
	 * @param external
	 * @param internal
	 */
	public DataField( String external, String internal, boolean enabled) {
		this.internal = internal.split("\\.");
		this.external = external.split("\\.");
		this.enabled = enabled;
	}
	
	public String[] getInternalSegments() {
		return internal;
	}
	/**
	 * Return the final field, irrespective of the path to that field. For example, 
	 * parent01.string01 means return string01.
	 * @return The field name
	 */
	public String getInternalField() {
		return internal[internal.length-1];
	}
	
	public String[] getExternalSegments() {
		return external;
	}
	
	public String getInternal() {
		StringBuffer sb = new StringBuffer();
		for (int x = 0; x < internal.length; x++) {
			if (x > 0) {
				sb.append('.');
			}
			sb.append(internal[x]);
		}
		return sb.toString();
	}

	public String getExternal() {
		StringBuffer sb = new StringBuffer();
		for (int x = 0; x < external.length; x++) {
			if (x > 0) {
				sb.append('.');
			}
			sb.append(external[x]);
		}
		return sb.toString();
	}
	
	/**
	 * Compare a DataField which means comparing each string segment of the internal name.
	 * id sorts first in the group. Level of indirection controls major sort.
	 */
	@Override
	public int compareTo(DataField other) {
		if (other==null) {
			throw new RuntimeException( "Cannot compare null to a " + this.getClass().getSimpleName()); 
		}
		int thisLength = external.length;
		if ("id".equals(external[thisLength-1])) {
			thisLength--;
		}
		int otherLength = other.external.length;
		if ("id".equals(other.external[otherLength-1])) {
			otherLength--;
		}
		int lenDiff = thisLength - otherLength;
		if (lenDiff!=0) {
			return lenDiff;
		}
		for (int x = 0; x < thisLength; x++) {
			int r = external[x].compareToIgnoreCase(other.external[x]);
			if (r!=0) return r;
		}
		return thisLength - otherLength;
	}

	@Override
	public boolean equals(Object obj) {
		return (getLabel().equals(((DataField)obj).getLabel()));
	}

	@Override
	public int hashCode() {
		return internal.hashCode();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getLabel() {
		return getExternal() + " (" + getInternal() + ")";
	}
	@Override
	public String toString() {
		return getLabel();
	}

}

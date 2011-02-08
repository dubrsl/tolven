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
 * @version $Id: SimpleProperty.java,v 1.2 2010/03/17 20:10:24 jchurin Exp $
 */  

package org.tolven.core;

import java.io.Serializable;
/**
 * An entity representing a simply property stored in the database
 */
public class SimpleProperty implements Serializable {
	
	private static final long serialVersionUID = 1L;
    private String name;
    private String value;

	@Override
	public boolean equals(Object obj) {
    	if (obj instanceof SimpleProperty) {
    		return name.equals(((SimpleProperty)obj).name);
    	}
    	return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Property: " + getName() + "=" + getValue();
	}

    /** Creates a new instance of Property */
    public SimpleProperty() {
    }

    public SimpleProperty(String name, String value) {
    	this.name = name;
    	this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

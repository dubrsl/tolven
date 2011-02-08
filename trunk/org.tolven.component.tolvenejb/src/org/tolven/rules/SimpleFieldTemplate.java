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
 * @version $Id: SimpleFieldTemplate.java,v 1.2 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.rules;

import java.io.Serializable;

import org.drools.base.SimpleValueType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.ObjectFactory;
import org.drools.facttemplates.FieldTemplate;
import org.tolven.app.entity.MenuData;
/**
 * This class represents a field as defined by Tolven metadata. 
 * @author John Churin
 *
 */
public class SimpleFieldTemplate implements FieldTemplate, Serializable {
	private static final long serialVersionUID = 1L;
	private SimpleFieldTemplate fieldTemplate;
	private int index;
	private String name;
    private final ValueType valueType;
    public SimpleFieldTemplate(String name, int index, Class<?> clazz) {
    	this.name = name;
    	this.index = index;
    	this.fieldTemplate = null;
    	if (org.tolven.app.entity.MenuData.class.isAssignableFrom(clazz)) {
    		this.valueType = new ValueType("MenuData", MenuData.class, SimpleValueType.OBJECT, ObjectFactory.getInstance() );
    	} else {
    		this.valueType = ValueType.determineValueType( clazz );
    	}
    }
    public SimpleFieldTemplate(String name, int index, SimpleFieldTemplate fieldTemplate) {
    	this.name = name;
    	this.index = index;
    	this.fieldTemplate = fieldTemplate;
        this.valueType = null;
    }
	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Return either the valueType if the indirect fieldTemplate or our valueType.
	 */
	@Override
	public ValueType getValueType() {
		if (fieldTemplate!=null) {
			return fieldTemplate.getValueType();
		}
		return valueType;
	}
	
	@Override
	public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null || getClass() != obj.getClass() ) {
            return false;
        }
        SimpleFieldTemplate other = (SimpleFieldTemplate) obj;
        return name.equals( other.name );
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	@Override
	public String toString() {
		return "Field: " + getName() + " (" + getValueType() + ")";
	}

}

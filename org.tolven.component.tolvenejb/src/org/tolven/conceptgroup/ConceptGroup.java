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
 * @version $Id: ConceptGroup.java,v 1.1 2009/11/07 06:03:53 jchurin Exp $
 */

package org.tolven.conceptgroup;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.tolven.trim.CD;

/**
 * A concept group holds a named set of concept descriptors (CD datatype) for some purpose.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ConceptGroup")
@XmlRootElement
public class ConceptGroup implements Serializable {
	private static ObjectFactory factory = new ObjectFactory();
    @XmlElement
    protected Set<CD> codes;
    
    @XmlAttribute
    protected String name;

	@Override
	public String toString() {
		return "ConceptGroup: " + getName() + " "+ getCodes().toString();
	}
	
	public void addCode( String codeSystem, String code ) {
		CD cd = factory.createCD();
		cd.setCodeSystem(codeSystem);
		cd.setCode(code);
		addCode( cd );
	}
	
	public void addCode( CD code ) {
		getCodes().add( code );
	}
	
	public Set<CD> getCodes() {
		if (codes==null) {
			codes = new HashSet<CD>();
		}
		return codes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
		if (other instanceof ConceptGroup) {
			return name.equals(((ConceptGroup)other).name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}


}

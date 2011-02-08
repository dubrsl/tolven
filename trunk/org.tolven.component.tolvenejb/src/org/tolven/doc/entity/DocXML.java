/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.doc.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("XML")
public class DocXML extends DocBase implements Serializable{

    /**
	 * Version number used for serialization
	 */
	private static final long serialVersionUID = 2L;

    @Column
    private String xmlNS;
    
    @Column
    private String xmlName;
    
    public DocXML() {
    }

    /**
     * Create a new XML-based document specifying the name and namespace of the
     * XSD defining the document.
     * @param name An informal name for the type of document such as CCR or TRIM.
     * @param namespace the formal name of the document type
     */
    public DocXML(String name, String namespace) {
    	this.setXmlName(name);
    	this.setXmlNS(namespace);
    }


    /**
     * When non-null, specifies the XML to Java binding context. For example, "org.tolven.ccr"
     * or "org.tolven.trim".
     * The XmlNamespace attribute must already be available.
     * @return
     */
	public String getBindingContext() {
		String ns =getXmlNS();
		if (ns==null) throw new IllegalArgumentException( "Namespace required to determine binding context" ); 
		if ("urn:astm-org:CCR".equals(ns)) return "org.tolven.ccr";
		if ("urn:tolven-org:trim:4.0".equals(ns)) return "org.tolven.trim";
		return null;
	}

	/**
	 * An informal name for the type of document such as CCR or TRIM. This name should not be used
	 * for any precise determination as to the type of XML contained in this document.
	 * @return
	 */
	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	public String getXmlNS() {
		return xmlNS;
	}

	public void setXmlNS(String xmlNS) {
		this.xmlNS = xmlNS;
	}
    
}

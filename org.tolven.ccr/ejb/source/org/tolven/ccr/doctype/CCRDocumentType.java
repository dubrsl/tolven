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
 * @version $Id: CCRDocumentType.java,v 1.1 2009/06/29 06:35:45 jchurin Exp $
 */  

package org.tolven.ccr.doctype;

import org.tolven.core.entity.Status;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocCCR;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocumentTypeAbstract;
import org.tolven.el.ExpressionEvaluator;

public class CCRDocumentType extends DocumentTypeAbstract {
	public final static String MEDIA_TYPE = "text/xml";
	public final static String NAMESPACE = "urn:astm-org:CCR";

	public CCRDocumentType() {
		super();
	}

	/**
	 * Return true if this document is one that this DocumentType can handle
	 */
	public boolean matchDocumentType(String mediaType, String namespace) {
			return (MEDIA_TYPE.equals(mediaType) && NAMESPACE.equals(namespace));
	}

	public void prepareEvaluator( ExpressionEvaluator ee ) {
		
	}
	public String getVariableName() {
		return "ccr";
	}

	@Override
	public Object getParsed() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Create a new document appropriate to this documentType
	 * @return The document
	 */
	public DocBase createNewDocument(String mediaType, String namespace) {
        DocXML doc = new DocCCR();
        doc.setStatus(Status.NEW.value());
        //String oid = System.getProperty("tolven.repository.oid");
        doc.setMediaType(mediaType);
        doc.setXmlNS(namespace); 
        doc.setXmlName("ContinuityOfCareRecord"); 
        doc.setDocumentType(this);
        setDocument( doc );
        return doc;
	}
	
	@Override
	public String toString() {
		return "DocumentType: " + getVariableName() + " " + MEDIA_TYPE + " " + NAMESPACE;
	}
	
}

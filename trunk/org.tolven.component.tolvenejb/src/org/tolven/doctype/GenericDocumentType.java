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
 * @version $Id: GenericDocumentType.java,v 1.5 2009/08/26 07:33:21 jchurin Exp $
 */  

package org.tolven.doctype;

import org.tolven.core.entity.Status;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocImage;
import org.tolven.doc.entity.DocXML;
import org.tolven.el.ExpressionEvaluator;

public class GenericDocumentType extends DocumentTypeAbstract {


	@Override
	public String getVariableName() {
		return "generic";
	}
	
	/**
	 * For generic document types, we always return true
	 */
	@Override
	public boolean matchDocumentType(String mediaType, String namespace) {
		return true;
	}

	@Override
	public void prepareEvaluator(ExpressionEvaluator ee) {
		// TODO Auto-generated method stub

	}

	@Override
	public DocBase createNewDocument(String mediaType, String namespace) {
        DocBase doc;
		if (mediaType.startsWith("image/")) {
	        doc = new DocImage();
		} else {
	        doc = new DocBase();
		}
        doc.setStatus(Status.NEW.value());
        //String oid = System.getProperty("tolven.repository.oid");
        doc.setMediaType(mediaType);
//        doc.setXmlNS(namespace); 
        doc.setDocumentType(this);
        setDocument( doc );
        return doc;
	}

	@Override
	public String toString() {
		return "Generic Document type";
	}

	@Override
	public Object getParsed() {
		return null;
	}

	
}

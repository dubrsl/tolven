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
 * @version $Id: CDADocumentType.java,v 1.1 2009/07/14 13:44:46 jchurin Exp $
 */  

package org.tolven.cda.doctype;

import java.io.ByteArrayInputStream;

import javax.ejb.EJB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Status;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocumentTypeAbstract;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapperFactory;
import org.w3c.dom.Document;

public class CDADocumentType extends DocumentTypeAbstract {
	public final static String MEDIA_TYPE = "text/xml";
	public final static String NAMESPACE = "urn:hl7-org:v3";
	 @EJB
	   private TolvenPropertiesLocal propertyBean;
	

	/**
	 * In the case of CDA, we return a DOM
	 */
	@Override
	public Object getParsed() {
		try {
			String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
			ByteArrayInputStream bis = new ByteArrayInputStream(getPayload(TolvenSessionWrapperFactory.getInstance().getUserPrivateKey(keyAlgorithm)));
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = documentBuilder.parse(bis);
			return dom;
		} catch (Exception e) {
			throw new RuntimeException( "Error parsing CDA content", e);
		}
	}

	@Override
	public String getVariableName() {
		return "cda";
	}

	@Override
	/**
	 * Return true if this document is one that this DocumentType can handle
	 */
	public boolean matchDocumentType(String mediaType, String namespace) {
			return (MEDIA_TYPE.equals(mediaType) && namespace !=null && 
					namespace.equals(NAMESPACE));
	}

	@Override
	public void prepareEvaluator(ExpressionEvaluator ee) {
		// TODO Auto-generated method stub

	}

	@Override
	public DocBase createNewDocument(String mediaType, String namespace) {
        DocBase doc = new DocXML();
        doc.setStatus(Status.NEW.value());
        //String oid = System.getProperty("tolven.repository.oid");
        doc.setMediaType(mediaType);
        doc.setSchemaURI(namespace); 
        doc.setDocumentType(this);
        ((DocXML)doc).setXmlNS(namespace);
        setDocument( doc );
        return doc;
	}

}

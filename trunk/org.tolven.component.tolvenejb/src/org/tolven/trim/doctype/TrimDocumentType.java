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
 * @version $Id: TrimDocumentType.java,v 1.4.26.1 2010/12/05 09:34:11 joseph_isaac Exp $
 */  

package org.tolven.trim.doctype;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.tolven.core.entity.Status;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocumentTypeAbstract;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.trim.ex.TrimFactory;

public class TrimDocumentType extends DocumentTypeAbstract {
	
	public final static String MEDIA_TYPE = "text/xml";
	public final static String NAMESPACE = "urn:tolven-org:trim:4.0";
	public final static String bindingContext = "org.tolven.trim";
    private static JAXBContext jc = null;
	private static final TrimFactory trimFactory = new TrimFactory();
//    @EJB  private DocProtectionLocal docProtectionBean;
	
	public TrimDocumentType() {
	}
	
//	@PostConstruct
//	public void init( InvocationContext ctx) {
//		DocumentBean documentBean = (DocumentBean)ctx.getTarget();
//		documentBean.addDocumentType(this);
//	}
	/**
	 * Return true if this document is one that this DocumentType can handle
	 */
	public boolean matchDocumentType(String mediaType, String namespace) {
			return (MEDIA_TYPE.equals(mediaType) && NAMESPACE.equals(namespace));
	}
	
	/**
	 * Create a new document appropriate to this documentType
	 * @return The document
	 */
	public DocBase createNewDocument(String mediaType, String namespace) {
        DocXML doc = new DocXML();
        doc.setStatus(Status.NEW.value());
        //String oid = System.getProperty("tolven.repository.oid");
        doc.setMediaType(mediaType);
        doc.setXmlNS(namespace); 
        doc.setDocumentType(this);
        setDocument( doc );
        return doc;
	}
	
	/**
	 * This method must be called before evaluating the contents of a document.
	 */
	@Override
	public void prepareEvaluator( ExpressionEvaluator ee ) {
		// Make sure we have a DocType-specific variable for the document
		Object variable = ee.get(getVariableName());
		if (variable==null) {
			Object parsed = getParsed( );
			ee.addVariable(getVariableName(), parsed);
		}
	}
	
	public JAXBContext getJAXBContext( ) {
		if (jc==null) {
			try {
				jc = JAXBContext.newInstance( bindingContext, TrimDocumentType.class.getClassLoader());
			} catch (JAXBException e) {
				throw new RuntimeException( "Error setting up JAXB binding context: " + bindingContext );
			}
		}
		return jc;
	}
	
	
	public Object unmarshal(byte[] payload) throws JAXBException {
		if (payload==null) return null;
        Unmarshaller u = getJAXBContext().createUnmarshaller();
        
       	u.setProperty("com.sun.xml.bind.ObjectFactory", trimFactory);
       	
	    return u.unmarshal( new ByteArrayInputStream( payload ) );
	    
	}

	public String getVariableName() {
		return "trim";
	}
	
	@Override
	public String toString() {
		return "DocumentType: " + getVariableName() + " " + MEDIA_TYPE + " " + NAMESPACE;
	}

	@Override
	public Object getParsed() {
		return null;
	}


}

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
 * @version $Id: CreateTest.java,v 1.1 2009/03/29 05:32:58 jchurin Exp $
 */  

package test.org.tolven.doctype;

import junit.framework.TestCase;

import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocTypeFactory;
import org.tolven.doctype.DocumentType;

public class CreateTest extends TestCase {
	DocTypeFactory factory;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = new DocTypeFactory();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		factory = null;
	}
	
	/**
	 * Create a TRIM document (without persistence which is inappropriate for a unit test).
	 */
	public void testInstantiateTrim() {
		DocBase doc = factory.createNewDocument("text/xml", "urn:tolven-org:trim:4.0" );
		assertNotNull( doc);
		DocumentType documentType = doc.getDocumentType();
		assertNotNull( documentType );
		assertNotNull( documentType.getDocument() );
		assertEquals( "text/xml", doc.getMediaType());
		assertTrue( doc instanceof DocXML );
		assertEquals( "urn:tolven-org:trim:4.0", ((DocXML)doc).getXmlNS());
	}
	
	/**
	 * Create a TRIM document (without persistence which is inappropriate for a unit test).
	 */
	public void testInstantiateCCR() {
		DocBase doc = factory.createNewDocument("text/xml", "urn:astm-org:CCR" );
		assertNotNull( doc);
		DocumentType documentType = doc.getDocumentType();
		assertNotNull( documentType );
		assertNotNull( documentType.getDocument() );
		assertEquals( "text/xml", doc.getMediaType());
		assertTrue( doc instanceof DocXML );
		assertEquals( "urn:astm-org:CCR", ((DocXML)doc).getXmlNS());
	}
}

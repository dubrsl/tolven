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
 * @version $Id: DocTypeFactoryTest.java,v 1.2 2009/06/02 03:11:46 jchurin Exp $
 */  

package test.org.tolven.doctype;

import junit.framework.TestCase;

//import org.tolven.ccr.doctype.CCRDocumentType;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocTypeFactory;
import org.tolven.doctype.DocumentType;
import org.tolven.doctype.GenericDocumentType;
import org.tolven.trim.doctype.TrimDocumentType;

public class DocTypeFactoryTest extends TestCase {

	public void testTrimFactory1() {
		DocTypeFactory factory = new DocTypeFactory();
		DocXML doc = new DocXML();
		doc.setMediaType("text/xml");
		doc.setXmlNS("urn:tolven-org:trim:4.0");
		DocumentType documentType = factory.associateDocumentType(doc);
		assertNotNull( documentType );
		assertTrue( documentType instanceof TrimDocumentType );
	}
	
	public void testTrimFactory2() {
		DocTypeFactory factory = new DocTypeFactory();
		DocumentType documentType = factory.createDocumentType("text/xml", "urn:tolven-org:trim:4.0" );
		assertNotNull( documentType );
		assertTrue( documentType instanceof TrimDocumentType );
	}
	
//	public void testCCRFactory() {
//		DocTypeFactory factory = new DocTypeFactory();
//		DocXML doc = new DocXML();
//		doc.setMediaType("text/xml");
//		doc.setXmlNS("urn:astm-org:CCR");
//		DocumentType documentType = factory.associateDocumentType(doc);
//		assertNotNull( documentType );
//		assertTrue( documentType instanceof CCRDocumentType );
//	}
	
	public void testDocTypeGeneric() {
		DocTypeFactory factory = new DocTypeFactory();
		DocXML doc = new DocXML();
		doc.setMediaType("text/xml");
		doc.setXmlNS("urn:xxxx-org:dddd");
		DocumentType documentType = factory.associateDocumentType(doc);
		assertNotNull( documentType );
		assertTrue( documentType instanceof GenericDocumentType );
	}
}

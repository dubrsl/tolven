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
 * @version $Id: CDTests.java,v 1.2 2010/01/20 20:10:51 jchurin Exp $
 */

package test.org.tolven.trim.xml;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.tolven.conceptgroup.ConceptGroup;
import org.tolven.trim.CD;
import org.tolven.trim.ex.TrimFactory;


public class CDTests extends XMLTestBase {
	private Logger logger = Logger.getLogger(this.getClass());
	protected static final TrimFactory factory = new TrimFactory( );

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testCDValue() throws JAXBException, IOException, XMLStreamException {
		JAXBContext jc = this.setupJAXBContext("org.tolven.conceptgroup");
		// Create an object graph
		CD cd = factory.createCD();
		cd.setCodeSystem("1.2.3.4");
		cd.setCode("345678");
		ConceptGroup cg = new ConceptGroup();
		cg.getCodes().add(cd);
		cg.setName("aConceptGroup");
		// Marshal to XML.
//		String xml = marshal( new JAXBElement<Datatypes>( new QName("urn:tolven-org:trim:4.0","Datatypes"), Datatypes.class, datatypes), Boolean.TRUE );
		String xml = marshal( jc, cg, true );
		logger.info( xml);
		
		// Now unmarshal back to objects
		Object rslt = unmarshal(jc, xml, factory);
		logger.info(rslt);
	}
}

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
 * @version $Id: CDAXPath.java,v 1.1 2009/05/30 00:34:37 jchurin Exp $
 */  

package test.org.tolven.cda;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

public class CDAXPath extends TestCase {
	
	XPath xpath = XPathFactory.newInstance().newXPath();
	XPathExpression titlePath = path("/ClinicalDocument/title/text()");
	XPathExpression patientNamePath = path("/ClinicalDocument/recordTarget/patientRole/patient/name");
	XPathExpression givenNamePath = path("given/text()");
	XPathExpression familyNamePath = path("family/text()");
	XPathExpression patientIdPath = path("/ClinicalDocument/recordTarget/patientRole/id");
	XPathExpression rootPath = path("@root");
	XPathExpression extensionPath = path("@extension");
	
	Document document;
	
	protected XPathExpression path( String expression ) {
		try {
			return xpath.compile(expression);
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Invalue XPath Expression", e );
		}
	}
	@Override
	protected void setUp() throws Exception {
		// parse the XML as a W3C Document
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = builder.parse(getClass().getResourceAsStream("cda1.xml" ));
	}
	
	public void testNodeList( ) throws Exception {
		NodeList nameList = (NodeList) patientNamePath.evaluate(document, XPathConstants.NODESET );
		String givenName = givenNamePath.evaluate(nameList.item(0));
		assertEquals("Harry", givenName );
		String familyName = familyNamePath.evaluate(nameList.item(0));
		assertEquals("OTTERSPEER", familyName );
		System.out.println("Given: " + givenName + " family: " + familyName);
	}
	
	public void testPatientId( ) throws Exception {
		NodeList idList = (NodeList) patientIdPath.evaluate(document, XPathConstants.NODESET );
		for (int i = 0; i < idList.getLength(); i++) {
			String root = rootPath.evaluate(idList.item(i));
			String extension = extensionPath.evaluate(idList.item(i));
			System.out.println("Root: " + root + " extension: " + extension);
		}
	}
	
	public void testSimpleFields( ) throws Exception {
		assertEquals("Test Document", titlePath.evaluate(document) );
		System.out.println(titlePath.evaluate(document));
		
//		NodeList titleList = (NodeList) xpath.evaluate(titlePath, document, XPathConstants.NODESET);
//		for (int x = 0; x < titleList.getLength(); x++ ) {
//			Node titleNode = titleList.item(x);
//			System.out.println(titleNode.getNodeValue());
//			NodeList mediaTypeList = (NodeList) xpath.evaluate(valuePath, titleNode, XPathConstants.NODESET);
//			for (int y = 0; y < mediaTypeList.getLength(); y++ ) {
//				Node mediaTypeNode = mediaTypeList.item(x);
//				System.out.println(mediaTypeNode.getNodeValue());
//			}
//		}

	}
}

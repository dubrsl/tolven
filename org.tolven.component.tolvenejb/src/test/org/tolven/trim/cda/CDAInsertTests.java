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
 * @version $Id: CDAInsertTests.java,v 1.2 2010/02/19 00:00:28 jchurin Exp $
 */  

package test.org.tolven.trim.cda;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

public class CDAInsertTests extends TestCase {
	private Logger logger = Logger.getLogger(this.getClass());
	
	XPath xpath;
	DocumentBuilder builder;
	
	@Override
	protected void setUp() throws Exception {
		// parse the XML as a W3C Document
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xpath = XPathFactory.newInstance().newXPath();
	}

	public void testwipTrim() throws Exception {
		Document wipTrim = builder.parse(getClass().getResourceAsStream("wipDoc.trim.xml" ));
		assertEquals("docclin", xpath.evaluate("trim/name", wipTrim));
		assertEquals("DOCCLIN", xpath.evaluate("trim/act/@classCode", wipTrim));
	}
	
	public void testInsertTrim() throws Exception {
		Document insertTrim = builder.parse(getClass().getResourceAsStream("entry1.trim.xml" ));
		assertEquals("weight", xpath.evaluate("trim/name", insertTrim));
		assertEquals("DOCCLIN", xpath.evaluate("trim/act/@classCode", insertTrim));
		NodeList actList = (NodeList) xpath.evaluate("trim/act/relationship[@typeCode='COMP']/act", insertTrim, XPathConstants.NODESET );
		for (int i = 0; i < actList.getLength(); i++) {
			String actClass = xpath.evaluate("@classCode", actList.item(i));
			System.out.println(actClass);
		}
	}
	
	public void displaySection( Node section ) {
		String code;
		String codeSystem;
		String name;
		try {
			code = xpath.evaluate("code/CE/code", section );
			codeSystem = xpath.evaluate("code/CE/codeSystem", section );
			name = xpath.evaluate("code/CE/displayName", section);
			logger.info("Section name: " + name + ", code: " + code + ", codeSystem: " + codeSystem);
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Exception parsing Section", e);
		}
	}
	
	public void displayEntry( Node entry ) {
		try {
			String classCode = xpath.evaluate("act/@classCode", entry );
			String moodCode = xpath.evaluate("act/@moodCode", entry);
			logger.info("Entry " + "class: " + classCode + ", mood: " + moodCode);
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Exception parsing Entry", e);
		}
	}
	
	/**
	 * Look for each section in the document
	 * @throws Exception
	 * @return The list of sections (Act nodes)
	 */
	protected NodeList findSections(Document insertTrim) {
		NodeList sectionList;
		try {
			sectionList = (NodeList) xpath.evaluate("trim/act/relationship[@typeCode='COMP']/act/relationship[@typeCode='COMP']/act", insertTrim, XPathConstants.NODESET );
			return sectionList;
		} catch (Exception e) {
			throw new RuntimeException( "Exception parsing Input Section", e);
		}
	}

	/**
	 * Find a matching section in the WIP document. Sections are matched based on the code attribute.
	 * @return
	 */
	protected Node findSection(Document wipTrim, Node section) {
		String code;
		String codeSystem;
		try {
			code = xpath.evaluate("code/CE/code", section );
			codeSystem = xpath.evaluate("code/CE/codeSystem", section );
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Exception parsing Section", e);
		}
		if (code==null || codeSystem==null) {
			throw new RuntimeException( "Section must have code and codeSystem");
		}
		// Get all sections in the wip document
		try {
			NodeList sectionList = (NodeList) xpath.evaluate("trim/act/relationship[@typeCode='COMP']/act/relationship[@typeCode='COMP']/act", wipTrim, XPathConstants.NODESET );
			// Now look for the section matching this section
			for (int i = 0; i < sectionList.getLength(); i++) {
				if (code.equals(xpath.evaluate("code/CE/code", sectionList.item(i))) &&
					codeSystem.equals(xpath.evaluate("code/CE/codeSystem", sectionList.item(i)))) {
					return sectionList.item(i);
				}
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Exception parsing Section list", e);
		}
		throw new RuntimeException( "Missing section "+ code + " [" + codeSystem + "] in CDA");
	}
	
	public void printWIPDocument( Document wipTrim, OutputStream out ) throws Exception {
		StreamResult result = new StreamResult( out);
		DOMSource source = new DOMSource( wipTrim ); 
		javax.xml.transform.Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, result);
	}
	
	protected void mergeEntries( Document wipTrim, Document insertTrim ) {
		// Find the list of input sections
		NodeList sectionList = findSections(insertTrim);
		// Iterate each section
		for (int i = 0; i < sectionList.getLength(); i++) {
			Node inputSection = sectionList.item(i);
			// Find this section in the WIP
			Node wipSection = findSection(wipTrim, inputSection);
			// Now add each entry from input to the WIP document
			try {
				NodeList entryList = (NodeList) xpath.evaluate("relationship[@typeCode='COMP']", inputSection, XPathConstants.NODESET );inputSection.getChildNodes();
				for (int e = 0; e < entryList.getLength(); e++) {
					// Get a copy of the input entry node
					Node newEntry = wipTrim.importNode(entryList.item(e),true);
					// Add it to the wipDocument
					wipSection.appendChild(newEntry);
				}
			} catch (XPathExpressionException e) {
				throw new RuntimeException( "Exception parsing entry list", e);
			}
		}
	}
	protected NodeList findInputValueSets( Document insertTrim ) {
		NodeList valueSetList;
		try {
			valueSetList = (NodeList) xpath.evaluate("trim/valueSet", insertTrim, XPathConstants.NODESET );
			return valueSetList;
		} catch (Exception e) {
			throw new RuntimeException( "Exception parsing Input ValueSets", e);
		}
	}
	
	protected Node findValueSet( Document trim, String name ) {
		NodeList valueSetList = findInputValueSets( trim );
		for (int v = 0; v < valueSetList.getLength(); v++) {
			Node valueSet = valueSetList.item(v);
			if (name.equals(valueSet.getAttributes().getNamedItem("name").getTextContent())) {
				return valueSet;
			}
		}
		return null;
	}
	
	protected void mergeValueSets( Document wipTrim, Document insertTrim ) {
		NodeList insertValueSetList = findInputValueSets( insertTrim );
		for (int v = 0; v < insertValueSetList.getLength(); v++) {
			Node valueSet = insertValueSetList.item(v);
			// See if the valueSet already exists. If it does, quietly ignore
			if (null==findValueSet( wipTrim, valueSet.getAttributes().getNamedItem("name").getTextContent())) {
				// Get a copy of the input entry node
				Node newValueSet = wipTrim.importNode(valueSet,true);
				// Add it to the wipDocument
				wipTrim.getFirstChild().appendChild(newValueSet);
			}
		}
	}
	/**
	 * Merge the insertDocument into the WIP document
	 * @param wipTrim
	 * @param insertTrim
	 */
	public void merge( Document wipTrim, Document insertTrim ) {
		mergeEntries(wipTrim, insertTrim);
		mergeValueSets(wipTrim, insertTrim);
	}
	
	public void merge( Document wipTrim, InputStream insertStream ) {
		try {
			Document insertTrim = parse(insertStream);
			merge( wipTrim, insertTrim);
		} catch (Exception e) {
			throw new RuntimeException( "Error merging trim", e );
		}
	}

	public Document parse( InputStream inputStream ) {
		try {
			Document trim = builder.parse(inputStream);
			return trim;
		} catch (Exception e) {
			throw new RuntimeException( "Error parsing trim", e );
		}
	}
	
	public Document parseResource( String file ) {
		InputStream stream = getClass().getResourceAsStream( file );
		if (stream==null ) {
			throw new RuntimeException( "Error opening trim xml file " + file );
		}
		return parse( stream );
	}
	
	public void testMerge( ) throws Exception {
		Document wipTrim = parseResource("wipDoc.trim.xml");
		merge( wipTrim, getClass().getResourceAsStream( "entry1.trim.xml"));
		merge( wipTrim, getClass().getResourceAsStream( "entry2.trim.xml"));
		
//		printWIPDocument( wipTrim, new FileOutputStream("src/test/org/tolven/trim/cda/output.xml"));
		printWIPDocument( wipTrim, System.out);
	}
}

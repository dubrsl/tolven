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
 * @version $Id: TransformTests.java,v 1.3 2009/06/23 12:55:16 jchurin Exp $
 */  

package test.org.tolven.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;
import org.tolven.xml.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TransformTests extends TestCase {
	private Logger logger = Logger.getLogger(this.getClass());
	public void testTransform1( ) throws Exception {
		TolvenLogger.defaultInitialize();
		Transformer transformer = new Transformer( getClass().getResourceAsStream("CDAtoTRIM.xsl" ));
		Reader reader = new InputStreamReader( getClass().getResourceAsStream("cda1.xml" ) );
		Writer writer = new StringWriter( );
		transformer.transform( reader, writer );
		logger.info( writer.toString() );
	}
	
	public void testDOMtoXML() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		StringReader reader = new StringReader( "<node><child>xxx</child></node>");
		InputSource is = new InputSource( reader );
		Document document = builder.parse(is);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult( writer);
		DOMSource source = new DOMSource( document ); 
		try {
			javax.xml.transform.Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new RuntimeException("Error creating XML output", e);
		}
		System.out.println( writer.toString() );
		assertEquals("<node>\r\n    <child>xxx</child>\r\n</node>\r\n", writer.toString()); 
	}
	
	static class Variables extends HashMap<QName, String> implements XPathVariableResolver {

		@Override
		public Object resolveVariable(QName arg0) {
			Object rslt = get(arg0);
			return rslt;
		}
	}
	
	public void testVariable() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		XPath xpath = XPathFactory.newInstance().newXPath();
		Variables variables = new Variables();
		xpath.setXPathVariableResolver(variables);
		variables.put(new QName("var"), "xxx");
		XPathExpression expr = xpath.compile("/node[child=$var]");
		StringReader reader = new StringReader( "<node><child>xxx</child></node>");
		InputSource is = new InputSource( reader );
		Document document = builder.parse(is);
		NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET  );
//		NodeList nodeList = (NodeList) xpath.evaluate("/node[child=$var]", document, XPathConstants.NODESET  );
		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
		}
	}


}

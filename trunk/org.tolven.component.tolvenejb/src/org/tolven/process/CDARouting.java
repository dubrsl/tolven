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
 * @version $Id: CDARouting.java,v 1.2 2009/04/15 09:37:22 jchurin Exp $
 */  

package org.tolven.process;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.log4j.Logger;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.trim.Act;
import org.tolven.trim.ST;
import org.tolven.trim.ex.TrimFactory;

/**
 * Provide routing instructions for the processing of a CDA document attachment
 * @author John Churin
 *
 */
public class CDARouting extends ComputeBase {
	private Logger logger = Logger.getLogger(this.getClass());
	private static final TrimFactory trimFactory = new TrimFactory();
	private Element extract;
	
	@Override
	public void compute() throws Exception {
		List<DocAttachment> attachments = getDocumentType().getAttachments();
		StringBuffer sb = new StringBuffer(200);
		if (attachments!=null) {
			for (DocAttachment attachment : attachments) {
				String text = "Attachment (" + attachment.getId() + ") " + attachment.getDescription();
				logger.info(text);
				sb.append(text);
				DocBase doc = attachment.getAttachedDocument();
				DocumentType documentType = doc.getDocumentType();
				documentType.setAccountUser(getAccountUser()); 
				Document dom = (Document) documentType.getParsed();
//				NodeList nodeList = getExtract().getChildNodes();
//				for ( int n = 0; n < nodeList.getLength(); n++) {
//					Node child = nodeList.item(n);
//					sb.append(" Node: [");
//					sb.append(child.getNodeName());
//					sb.append("]");
//				}
			}
		}
		ST value = trimFactory.createST();
		value.setValue(sb.toString());
		Act act = getAct();
		act.getText().setST(value);
	}
	
	public Element getExtract() {
		return extract;
	}
	public void setExtract(Element extract) {
		this.extract = extract;
	}

}

/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
/**
 * This file contains ShareCommonBean.
 *
 * @package org.tolven.app.bean
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app.bean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.ShareCommonLocal;
import org.tolven.app.ShareCommonRemote;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageAttachment;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.trim.Trim;
import org.tolven.xml.ParseXML;

/**
 * This bean contains common tasks used in sharing process.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No: |  Created/Updated Date |    Created/Updated By   |     Method name/Comments            
==============================================================================================================================================
1   | 06/14/2010           	| Valsaraj Viswanathan 	  | Initial Version.
2	| 03/20/2011			| Unnikrishnan Skandappan | Provided look up for documentBean(getDocumentBean()) when NULL is returned.
==============================================================================================================================================
*/
@Stateless()
@Local(ShareCommonLocal.class)
@Remote(ShareCommonRemote.class)
public class ShareCommonBean {
	
	static final String TRIM_NS = "urn:tolven-org:trim:4.0";
	@EJB private DocumentLocal documentBean;
	private Logger logger = Logger.getLogger(this.getClass());
	private TrimMarshaller marshal = new TrimMarshaller();
	
	/**
	 * @return the documentBean
	 */
	public DocumentLocal getDocumentBean() {
		return documentBean;
	}

	/**
	 * @param documentBean the documentBean to set
	 */
	public void setDocumentBean(DocumentLocal documentBean) {
		this.documentBean = documentBean;
	}

	/**
	 * Sends a trim payload to destination account.
	 * 
	 * added on 06/14/2010
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
	 * @param trim - trim object
	 * @param app - AppEvalAdaptor instance
	 * @param tm - TolvenMessage instance
	 * @param destinationAccountId - destination account id
	 */
	public void sendMessage(Trim trim, AppEvalAdaptor app, TolvenMessage tm, Long destinationAccountId) {
		/*  Don't forward to ourself */
		if (destinationAccountId != null && destinationAccountId != app.getAccount().getId()) {
			try {
				logger.info("Forward to account" + destinationAccountId);
			   	TolvenMessage tmNew;
                 
			   	if (tm instanceof TolvenMessageWithAttachments) {
			   		List<DocAttachment> docAttList = getDocumentBean().findAttachments(app.getDocument());
                  	DocAttachment docAtt;
                  	tmNew = new TolvenMessageWithAttachments();
                  	((TolvenMessageWithAttachments)tmNew).setAttachments(new ArrayList<TolvenMessageAttachment>());
                  	int index=-1;
                  	TolvenMessageAttachment tmAttNew;
                  	
                  	for (TolvenMessageAttachment tmAtt : ((TolvenMessageWithAttachments)tm).getAttachments()) {
                  		index++;
                  		docAtt = docAttList.get(index);
                  		
                  		if (! docAtt.getDescription().equals("Original message")) {
                  			tmAttNew = new TolvenMessageAttachment();
                  			tmAttNew.setDescription(docAtt.getDescription());
                  			tmAttNew.setPayload(tmAtt.getPayload());
                  			tmAttNew.setMediaType(tmAtt.getMediaType());
                  			tmAttNew.setVersion(tmAtt.getVersion());
	                  	  	((TolvenMessageWithAttachments)tmNew).getAttachments().add(tmAttNew);
                  		}
                  	}
			   	} else {
			   		tmNew   = new TolvenMessage();
			   	}
	            
			   	tmNew.setAccountId(destinationAccountId);
	            tmNew.setAuthorId(tm.getAuthorId());
	            tmNew.setFromAccountId(tm.getAccountId());
	            tmNew.setXmlNS(TRIM_NS);
                tmNew.setXmlName( tm.getXmlName() );
                /* Creating new PayLoad to be added to the tolven message */
                tmNew.setPayload(marshal.addTrimAsPayload(trim));
                getDocumentBean().queueTolvenMessage(tmNew);
			} catch (Exception e) {
				throw new RuntimeException( "Error forwarding message", e);
			}
		}
	}
	
	/**
	 * Class for marshalling Trim objects.
	 * 
	 * added on 06/14/2010
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
	 */
	public class TrimMarshaller extends ParseXML {
		@Override
		protected String getParsePackageName() {
			return "org.tolven.trim";
		}
		
		/**
		 * Method to convert Trim to a payload.
		 * 
		 * added on 06/14/2010
		 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
		 */
		private byte[] addTrimAsPayload( Trim trim) throws JAXBException {
	        ByteArrayOutputStream output = new ByteArrayOutputStream( );
	        Marshaller m = getMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	        m.marshal( trim, output );
	        
	        return output.toByteArray();
		}
	}
}
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
 * This file contains TrimCreatorBean.
 *
 * @package org.tolven.app.bean
 * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.PrivateKey;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimCreatorLocal;
import org.tolven.app.TrimCreatorRemote;
import org.tolven.app.TrimLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.TrimHeader;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLProtectedLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.trim.BindPhase;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimEx;

/**
 * This bean handles create new trim, submit and other trim related operations.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 12/12/2009           	| Valsaraj Viswanathan 	| Initial Version.
2		| 03/16/2011			|  Valsaraj Viswanathan | Removed unused methods and added trim related methods.
==============================================================================================================================================
*/
@Stateless()
@Local(TrimCreatorLocal.class)
@Remote(TrimCreatorRemote.class)
public class TrimCreatorBean implements TrimCreatorLocal, TrimCreatorRemote {
	@EJB CreatorLocal creatorLocal;
	@EJB TrimLocal trimBean;
	@EJB MenuLocal menuBean;
	@EJB DocumentLocal documentBean;
	@EJB XMLProtectedLocal xmlProtectedBean;
	Logger logger = Logger.getLogger(this.getClass());
	static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	/**
	 * Creates trim.
	 * 
	 * added on 12/12/2009
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
     * @param accountUser - account user
     * @param trimPath - trim path
     * @param context - context
     * @param now - current date
     * @throws JAXBException, TRIMException
	 */
	public TrimEx createTrim(AccountUser accountUser, String trimPath, String context, Date now) 
			throws JAXBException, TRIMException {
		MenuData mdTrim = null;
		TrimHeader trimHeader = trimBean.findOptionalTrimHeader(trimPath);
		
		if (trimHeader == null) {
			// Get the TRIM template as XML
			// If the account doesn't know about this, then we'll allow access to the accountTemplate for this account type.
			mdTrim = menuBean.findDefaultedMenuDataItem(accountUser.getAccount(), trimPath);
		
			if (mdTrim == null) 
				throw new IllegalArgumentException( "No TRIM item found for " + trimPath);
			
			trimHeader = mdTrim.getTrimHeader();
		}
		
		if (trimHeader == null) 
			throw new IllegalArgumentException( "No TRIM found for " + trimPath);
		
		TrimEx trim = null;
		
		try {
			trim = trimBean.parseTrim(trimHeader.getTrim(), accountUser, context, now, null );
		} catch (RuntimeException e) {
			throw new RuntimeException( "Error parsing TRIM '" + trimHeader.getName() + "'", e );
		}
		
		return trim;
	}

	/**
	 * Submits the trim.
	 * 
	 * added on 12/12/2009
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
	 * @param trim - trim object
	 * @param context - context path
     * @param accountUser - account user
     * @param now - current date
     * @throws Exception
	 */
	public void submitTrim(TrimEx trim, String context, AccountUser accountUser, Date now) throws Exception {
		MenuData mdTrim = null;		
		TrimHeader trimHeader = trimBean.findOptionalTrimHeader(trim.getName());
		
		if (trimHeader == null) {
			// Get the TRIM template as XML
			// If the account doesn't know about this, then we'll allow access to the accountTemplate for this account type.
			mdTrim = menuBean.findDefaultedMenuDataItem(accountUser.getAccount(), trim.getName());
		
			if (mdTrim == null) 
				throw new IllegalArgumentException( "No TRIM item found for " + trim.getName());
			
			trimHeader = mdTrim.getTrimHeader();
		
		}
		
		MenuPath contextPath = new MenuPath(context);
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.putAll(contextPath.getNodeValues());
		{
			String assignedPath = null;
			
			try {
				assignedPath = accountUser.getProperty().get("assignedAccountUser");
			} catch (Exception e) {
				
			}
			
			if (assignedPath != null) {
				MenuData assigned = menuBean.findMenuDataItem(accountUser.getAccount().getId(), assignedPath);
				variables.put("assignedAccountUser", assigned);
			}
		}
		variables.put("trim", trim);

		// Create an event to hold this trim document
		DocXML docXML = documentBean.createXMLDocument(TRIM_NS, accountUser
				.getUser().getId(), accountUser.getAccount().getId());
		docXML.setSignatureRequired(creatorLocal.isSignatureRequired(trim, accountUser
				.getAccount().getAccountType().getKnownType()));
		logger.info("Document (placeholder) created, id: " + docXML.getId());
		// Call computes for the first time now
		creatorLocal.computeScan(trim, accountUser, contextPath, now, docXML.getDocumentType());
		// Bind to placeholders
		creatorLocal.placeholderBindScan(accountUser.getAccount(), trim, mdTrim,contextPath, now, BindPhase.CREATE, docXML);
		// Create an event
		MenuData mdEvent = creatorLocal.establishEvent( accountUser.getAccount(), trim, now, variables);
		
		if (mdEvent == null) {
			throw new RuntimeException( "Unable to create instance of event for " + trim.getName());
		}
		
		mdEvent.setDocumentId(docXML.getId());		
		// insert message data to trim
		creatorLocal.marshalToDocument( trim, docXML );		
		// Make sure this item shows up on the activity list
		creatorLocal.addToWIP(mdEvent, trim, now, variables );		
		creatorLocal.submit(mdEvent, accountUser, null);
    }
	
	/**
     * This method gets the TRIM template as XML, creates event to hold the trim,
     * inserts message data to trim and adds the trim to activity list.
     *
     * added on 12/29/2010
     * @author vineetha
     * @param trim - trim object
     * @param context - context path
     * @param accountUser - account user
     * @param now - current date
     * @throws Exception
     */
	public String addTrimToActivityList(TrimEx trim, String context, AccountUser accountUser, Date now) 
			throws Exception {
		MenuData mdTrim = null;
		TrimHeader trimHeader = trimBean.findOptionalTrimHeader(trim.getName());
		
		if (trimHeader == null) {
			// Get the TRIM template as XML
			// If the account doesn't know about this, then we'll allow access to the accountTemplate for this account type.
			mdTrim = menuBean.findDefaultedMenuDataItem(accountUser.getAccount(), trim.getName());
		
			if (mdTrim == null) 
				throw new IllegalArgumentException( "No TRIM item found for " + trim.getName());
			
			trimHeader = mdTrim.getTrimHeader();
		}
		
		MenuPath contextPath = new MenuPath(context);
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.putAll(contextPath.getNodeValues());
		{
			String assignedPath = null;
			
			try {
				assignedPath = accountUser.getProperty().get("assignedAccountUser");
			} catch (Exception e) {
				
			}
			
			if (assignedPath != null) {
				MenuData assigned = menuBean.findMenuDataItem(accountUser.getAccount().getId(), assignedPath);
				variables.put("assignedAccountUser", assigned);
			}
		}
		
		variables.put("trim", trim);
		// Create an event to hold this trim document
		DocXML docXML = documentBean.createXMLDocument(TRIM_NS, accountUser
				.getUser().getId(), accountUser.getAccount().getId());
		docXML.setSignatureRequired(creatorLocal.isSignatureRequired(trim, accountUser
				.getAccount().getAccountType().getKnownType()));
		logger.info("Document (placeholder) created, id: " + docXML.getId());
		// Call computes for the first time now
		creatorLocal.computeScan(trim, accountUser, contextPath, now, docXML.getDocumentType());
		// Bind to placeholders
		creatorLocal.placeholderBindScan(accountUser.getAccount(), trim, mdTrim,
				contextPath, now, BindPhase.CREATE, docXML);
		// Create an event
		MenuData mdEvent = creatorLocal.establishEvent(accountUser.getAccount(), trim, now,
				variables);
		
		if (mdEvent == null) {
			throw new RuntimeException( "Unable to create instance of event for " + trim.getName());
		}
		
		mdEvent.setDocumentId(docXML.getId());
		// insert message data to trim
		creatorLocal.marshalToDocument(trim, docXML);		
		// Make sure this item shows up on the activity list
		creatorLocal.addToWIP(mdEvent, trim, now, variables);
		
		return mdEvent.getPath();
    }

	/**
     * Returns the Trim for the MenuData which can be revised.
     * 
     * added on 02/21/2011
     * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
     * @param mdPlaceholder - place holder
     * @param accountUser - account user
     * @return TrimEx - trim object
     * @throws JAXBException
     */
	public TrimEx getTrimForRevision(MenuData mdPlaceholder,
			AccountUser accountUser, PrivateKey userPrivateKey) throws JAXBException {
		if (mdPlaceholder == null) {
			throw new IllegalArgumentException("[" + this.getClass().getSimpleName() + "]Missing placeholder for transition");
		}
		
		DocXML docXMLFrom = (DocXML) documentBean.findDocument(mdPlaceholder.getDocumentId());
		Object obj = xmlProtectedBean.unmarshal(docXMLFrom, accountUser, userPrivateKey);
		
		if (obj instanceof TrimEx) {
			return (TrimEx)obj;
		} else {
			return null;
		}
	}
	
	/**
     * Returns the MenuData corresponding to the Trim provided.
     * 
     * added on 02/21/2011
     * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
     * @param trim - trim object
     * @param mdPlaceholder - place holder
     * @param accountUser - account user
     * @param transitionName - transition name
     * @param now - current date
     * @return MenuData - menudata
     * @throws JAXBException
     * @throws TRIMException
     */
	public MenuData reviseTrimEvent(TrimEx trim, MenuData mdPlaceholder,
			AccountUser accountUser, String transitionName, Date now)
			throws JAXBException, TRIMException {
		DocXML docXMLFrom = (DocXML) documentBean.findDocument(mdPlaceholder
				.getDocumentId());
		DocXML docXMLNew = documentBean.createXMLDocument(TRIM_NS, accountUser
				.getUser().getId(), accountUser.getAccount().getId());
		// Carry forward attachments
		documentBean.copyAttachments(docXMLFrom, docXMLNew );
        logger.info( "Document (event) created, id: " + docXMLNew.getId() );
		// Adjust status based on selected transition
		String status = creatorLocal.calculateTransition( trim, transitionName);
		// If we have not stored a tolvenId for placeholder yet, do it now.
		// This can happen when the placeholder started from an external message
		if (trim.getTolvenId(accountUser.getAccount().getId()) != null) {
			trim.addTolvenId(creatorLocal.createTolvenId(mdPlaceholder, null, now, status,
					accountUser.getUser().getLdapUID()));
		}
		
		// Setup up context variables for substitution
		Map<String, Object> variables = new HashMap<String, Object>(10);
		// New menu data event
		String instancePath = creatorLocal.getInstancePath(trim, "echr");
		// Create an event to hold this transaction
		MenuData mdEvent = creatorLocal.createEvent( accountUser.getAccount(), instancePath, trim, now, variables);
		mdEvent.setDocumentId(docXMLNew.getId());
		mdEvent.setActStatus(status);
		// Reference the event in the Trim (add to event history)
		trim.addTolvenEventId(creatorLocal.createTolvenId( mdEvent, null, now, status, accountUser.getUser().getLdapUID() ));
		
		// OK, now marshal the finished TRIM into XML and store in the document.
		creatorLocal.marshalToDocument( trim, docXMLNew );
		
		return mdEvent;
	}
}
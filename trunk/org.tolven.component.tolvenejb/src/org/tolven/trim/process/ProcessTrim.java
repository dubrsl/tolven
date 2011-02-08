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
 * @author John Churin
 * @version $Id: ProcessTrim.java,v 1.12 2010/04/16 20:36:00 jchurin Exp $
 */  

package org.tolven.trim.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.drools.StatefulSession;
import org.drools.facttemplates.FactTemplate;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.CreatorLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.bean.Plan;
import org.tolven.app.bean.ShareBean;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.Status;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.rules.PlaceholderFact;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.BindPhase;
import org.tolven.trim.II;
import org.tolven.trim.Message;
import org.tolven.trim.Trim;
import org.tolven.trim.api.ProcessTrimLocal;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.scan.BindScanner;
import org.tolven.trim.scan.ExtensionScanner;
import org.tolven.trim.scan.InboundScanner;
import org.tolven.trim.scan.PopulateScanner;

@Stateless
@Local( ProcessTrimLocal.class )
public class ProcessTrim extends AppEvalAdaptor implements ProcessTrimLocal {

	@EJB TrimLocal trimBean;
	@EJB CreatorLocal creatorBean;
	
    private static final String TRIMns = "urn:tolven-org:trim:4.0";
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	// Callback variables used during rule processing
	private TrimExpressionEvaluator trimee;
	private boolean saveTrim = false;
	protected Trim trim;
	protected TolvenMessage tm;

	// End Callback variables

	public void process( Object message, Date now ) {
		try {
			if (message instanceof TolvenMessage || 
					message instanceof TolvenMessageWithAttachments) { 
			tm = (TolvenMessage) message;
			trim = null;
			saveTrim = false;
			trimee = null;
			// We're only interested in Trim messages
			if (!TRIMns.equals(tm.getXmlNS())) return;
				// This will call back to load up working memory
				associateDocument( tm, now );
				// Run the rules now
				runRules( );
				// If requested, marshal and put the trim back - note: inbound trims are not immutable, however, functionally, an
				// inbound trim is immutable - because, by definition, it was authored by someone else.
				// Performance Note: We could save a bit of backend time if we coordinated this method with associateDocument which
				// also stores and encrypts the payload.
				if (isSaveTrim()) {
					ByteArrayOutputStream trimXML = new ByteArrayOutputStream() ;
					xmlBean.marshalTRIM(trim, trimXML);
				    String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
				    int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
					getDocument().setAsEncryptedContent(trimXML.toByteArray(), kbeKeyAlgorithm, kbeKeyLength);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Exception in TRIM processor", e);
		}
	}

	@Override
	protected DocBase scanInboundDocument(DocBase doc) throws Exception {
		// Now transform the original inbound trim (which had an outbound flavor) to be an inbound document
		// At the same time, we should resolve placeholders 
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.put("account", getAccount());
		variables.put("now", getNow());
		variables.put("fromAccountId", tm.getFromAccountId());
		TrimEx originalTrim = (TrimEx) xmlBean.unmarshal(tm.getXmlNS(), new ByteArrayInputStream(tm.getPayload()));
	    logger.info( "Trim Name in scanInboundDocument is  " + originalTrim.getMessage().getSender().getTrim());		
	    trim = getTrimBean().evaluateAndParseTrim(originalTrim.getMessage().getSender().getTrim(), variables);
		ExtensionScanner scanner = new ExtensionScanner();
		scanner.setTrim( originalTrim );
		scanner.setTargetTrim( trim );
		scanner.scan();
		InboundScanner inboundScanner = new InboundScanner();
		inboundScanner.setAccount(getAccount());
		inboundScanner.setDocumentId(doc.getId());
		inboundScanner.setKnownType(getAccount().getAccountType().getKnownType());
		inboundScanner.setMenuBean(menuBean);
		inboundScanner.setCreatorBean(creatorBean);
		inboundScanner.setNow(getNow());
		inboundScanner.setPhase(BindPhase.RECEIVE);
		inboundScanner.setTrim( trim );
		inboundScanner.scan();
		// The Trim is mutable in this case
		setSaveTrim(true);
		return doc;
	}
	
	@Override
	protected void loadWorkingMemory( StatefulSession workingMemory ) throws Exception {		
		if (trim == null) {
			trim = (TrimEx) xmlBean.unmarshal(tm.getXmlNS(), new ByteArrayInputStream(tm.getPayload()));
		}
	    // Assert a trim object and see what the rules do with it
	    workingMemory.insert(trim);
	    workingMemory.insert(this);
	    workingMemory.insert(tm);
	    // Add all the placeholders bound within the trim
		ExpressionEvaluator ee = getExpressionEvaluator();
		BindPhase bindPhase = BindPhase.CREATE;
	    // For local processing, we need all placeholders in the trim to be
	    // asserted into working memory.
	    BindScanner scanner = null;
    	scanner = new PopulateScanner();
    	scanner.setPhase(bindPhase);
    	scanner.setAccount(getAccount());
    	scanner.setKnownType(getAccount().getAccountType().getKnownType());
    	scanner.setMenuBean(menuBean);
    	scanner.setNow(getNow());
    	scanner.setTrim(trim);
    	scanner.setDocumentId(getDocument().getId());
    	scanner.scan();
    	// Remove references to changed placeholders and then insert them into working memory
    	for (MenuData mdPlaceholder : scanner.getPlaceholders()) {
    		// remember the placeholder in the variables list, in case we need it later for populating
    		ee.put(mdPlaceholder.getMenuStructure().getNode(), mdPlaceholder);
    		// If it's a changed placeholder
    		if (mdPlaceholder.getDocumentId()==getDocument().getId()) {
    			int count = menuBean.removeReferencingMenuData( mdPlaceholder );
    			logger.info( "Removed " + count + " menuData references to mdPlaceholder " + mdPlaceholder.getPath());
        		mdPlaceholder.setStatus(Status.ACTIVE);
        		workingMemory.insert(mdPlaceholder);
        		// See if there's a factTemplate for this placeholder, add it to working memory
        		for( org.drools.rule.Package pkg : ruleBase.getPackages()) {
        			FactTemplate ft = pkg.getFactTemplate(mdPlaceholder.getMenuStructure().getNode());
        			if (ft!=null) {
        				PlaceholderFact fact = (PlaceholderFact) ft.createFact(mdPlaceholder.getId());
        				fact.setPlaceholder(mdPlaceholder);
        				workingMemory.insert(fact);
        				break;	// Only insert a md fact once
        			}
        		}
        		if ("act/evn/treatmentPlan".equals(trim.getName()) && 
        				mdPlaceholder.getMenuStructure().getNode().equals("plan")) {
    				Plan plan = findEmptyPlan(mdPlaceholder);
    				if (plan!=null) {
    					workingMemory.insert(plan);
    				}
    			}
    		}
    	}
    	// Need to remove references to unused placeholders as well.
    	if (trim.getUnused()!=null) {
        	for (II ii : trim.getUnused().getIIS()) {
        		MenuData mdRemoved = menuBean.findMenuDataItem(getAccount().getId(), ii.getExtension());
    			int count = menuBean.removeReferencingMenuData( mdRemoved );
        	}
    	}
	}

	@Override
	protected ExpressionEvaluator getExpressionEvaluator() {
		if (trimee==null) {
			trimee = new TrimExpressionEvaluator();
			trimee.addVariable( "trim", trim);
			trimee.addVariable( "now", getNow());
			trimee.addVariable( "doc", getDocument());
			trimee.addVariable(TrimExpressionEvaluator.ACCOUNT, getAccount());
			trimee.addVariable(DocumentType.DOCUMENT, getDocument());
		}
		return trimee;
	}

	/**
	 * TODO: This plan behavior should be a plugin, not hard-coded.
	 * It may work in a compute. Done here as a prototype only.
	 */
	final static int LAST_PLAN = 4; 
	
	public Plan findEmptyPlan(MenuData mdPlaceholder ) {
		// Look for an empty summary slot
		MenuQueryControl ctrl = new MenuQueryControl();
		
		for (int n = 1; n <= LAST_PLAN; n++) {
			String planPath = "echr:patient:summary:plan" + n + "sum";
			MenuPath mdPath = new MenuPath( mdPlaceholder.getPath());
			MenuPath path = new MenuPath(planPath, mdPath);
			MenuStructure msPlan = menuBean.findMenuStructure( getAccount().getId(), planPath );
			ctrl.setMenuStructure(msPlan);
			ctrl.setRequestedPath(path);
			// If this plan number is available or it's the last one we have 
			if (0==menuBean.countMenuData(ctrl) || n==LAST_PLAN ) {
				Plan plan = new Plan( planPath );
				return plan;
			}
		}
		return null;
	}
	
	/**
	 * Create any placeholders that have not already been created.
	 * 
	 * @param trim the trim document to scan for binding instructions
	 * @param bindPhase Which phase to match
	 */
	public void createPlaceholders(TrimEx trim, BindPhase bindPhase) {
		ExpressionEvaluator ee = getExpressionEvaluator();
		creatorBean.placeholderBindScan(getAccount(), trim, null, null, getNow(), bindPhase, this.getDocument());
	}
	
	/**
	 * Insert placeholders from trim into working memory. 
	 * Only placeholders that have binding instructions are bound and then only those
	 * with a matching account type and phase matching the requested phase.
	 * The normal case it to is to bind "create" phase  
	 * As a side-effect, we also hold onto all placeholders we find in case they are needed
	 * later while processing other trims.
	 * @param trim the trim document to scan for binding instructions
	 * @param bindPhase Which phase to match
	 */
	public void insertPlaceholders( Trim trim, BindPhase bindPhase ) {
		 StatefulSession workingMemory = getWorkingMemory();
		 ExpressionEvaluator ee = getExpressionEvaluator();
	    // For local processing, we need all placeholders in the trim to be
	    // asserted into working memory.
	    BindScanner scanner = null;
    	scanner = new PopulateScanner();
    	scanner.setPhase(bindPhase);
    	scanner.setAccount(getAccount());
    	scanner.setKnownType(getAccount().getAccountType().getKnownType());
    	scanner.setMenuBean(menuBean);
    	scanner.setNow(getNow());
    	scanner.setTrim(trim);
    	scanner.setDocumentId(getDocument().getId());
    	scanner.scan();
    	// Remove references to changed placeholders and then insert them into working memory
    	assertPlaceholders(scanner.getChangedPlaceholders());
    	// Need to remove references to unused placeholders as well.
    	if (trim.getUnused()!=null) {
        	for (II ii : trim.getUnused().getIIS()) {
        		MenuData mdRemoved = menuBean.findMenuDataItem(getAccount().getId(), ii.getExtension());
    			int count = menuBean.removeReferencingMenuData( mdRemoved );
        	}
    	}
	}
	
	/**
	 * An outbound scan prepare a trim for the possibility that a reply will be received as
	 * a result of sending the message outbound. 
	 * @see ShareBean
	 */
	public void outboundScan( Trim trim ) {
		shareBean.outboundScan(trim, getAccount());
	}

	/**
	 * Send this document to another account.
	 * @param copyTo
	 * @throws Exception
	 */
	public void send( Message message ) throws Exception {
		// If we're missing accountId we're stuck
		if (message.getReceiver().getAccountId()==null) {
			throw new RuntimeException( "Message to be sent is missing destination account id");
		}
		if (message.getReceiver().getAccountId().equals(message.getSender().getAccountId())) {
			throw new RuntimeException( "Message sending account id same as destination account id");
		}
		// Send the document to the other account for persistence and rule processing
		TolvenMessage tmNew = new TolvenMessage();
		tmNew.setFromAccountId(getSourceAccount().getId());
		tmNew.setAccountId(Long.parseLong(message.getReceiver().getAccountId()));
		tmNew.setAuthorId(tm.getAuthorId());
        tmNew.setPayload(tm.getPayload());
        tmNew.setXmlName( tm.getXmlName() );
        tmNew.setXmlNS( tm.getXmlNS() );
        documentBean.queueTolvenMessage(tmNew);
		logger.info( "Send share from " + getSourceAccount().getId() + " to: " + tmNew.getAccountId());
	}

	
	public boolean isSaveTrim() {
		return saveTrim;
	}

	public void setSaveTrim(boolean saveTrim) {
		this.saveTrim = saveTrim;
	}
	public TrimLocal getTrimBean() {
		return trimBean;
	}
	public CreatorLocal getCreatorBean() {
		return creatorBean;
	}
	public TolvenMessage getTm() {
		return tm;
	}
}

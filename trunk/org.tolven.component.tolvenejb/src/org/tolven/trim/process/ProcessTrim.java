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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.Status;
import org.tolven.doc.bean.ProcessBean;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.rules.PlaceholderFact;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Application;
import org.tolven.trim.BindPhase;
import org.tolven.trim.II;
import org.tolven.trim.Message;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.TolvenId;
import org.tolven.trim.Trim;
import org.tolven.trim.api.ProcessTrimLocal;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.SETIISlotEx;
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
	private static ThreadLocal<TolvenMessage> tolvenMessageTL = new ThreadLocal<TolvenMessage>();
	// End Callback variables
	
	public static TolvenMessage get() {
		return tolvenMessageTL.get();
	}

	public static void set(TolvenMessage tolvenMessageTL) {
		ProcessTrim.tolvenMessageTL.set(tolvenMessageTL);
	}

	public void process( Object message, Date now ) {
		   
		try {
			if (message instanceof TolvenMessage || 
					message instanceof TolvenMessageWithAttachments) { 
			tm = (TolvenMessage) message;
			trim = null;
			saveTrim = false;
			trimee = null;
			
			tolvenMessageTL.set(tm);

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
		finally  {
			tolvenMessageTL.set(null);
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
		logger.info("Set target trim: " + trim.getName());
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
                //CCHIT merge
		ActRelationship sharingStatusRel = ((ActEx) trim.getAct())
				.getRelationship().get("sharingStatus");

		if (sharingStatusRel != null
				&& sharingStatusRel.getLabel().getValue().equals("1")) {
			if ("docclin/evn/shareInfoIn".equalsIgnoreCase(trim.getName())) {
      			 getWorkingMemory().insert(inboundScanner.getInstanceEvent());                               
      		}
			
			String originalTrimName = originalTrim.getName();
			
			if ("docclin/evn/problem".equals(originalTrimName)
					|| "pxDoc".equals(originalTrimName)
					|| "docclin/evn/drugAllergies".equals(originalTrimName)
					|| "obs/evn/medicationHistory".equals(originalTrimName)
					|| "labOrderDoc".equals(originalTrimName)
					|| "obs/evn/patientPrescription".equals(originalTrimName)) {
				MenuData mdPat = createReferenceMdForInShare(inboundScanner
						.getInstanceEvent());
				II trimII = ((SETIISlotEx) ((ActEx) trim.getAct())
						.getParticipation().get("subject").getRole().getId())
						.getFor().get(getAccountUser().getAccount());
				trimII.setExtension(mdPat.getPath());
			}
		}
		
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
	
	/** CCHIT merge
	 * Creates a patient in emergency access account.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param mdInShare - inshare menudata
	 * @return mdPatient - patient menudata
	 */
	public MenuData createReferenceMdForInShare(MenuData mdInShare){
		ActEx trimAct = (ActEx) trim.getAct();
		String uniqueId = trimAct.getRelationship().get("patientInfo").getAct()
				.getObservation().getValues().get(0).getST().toString();
		String root = ELFunctions.computeIDRoot(getAccount());
		String extension = uniqueId;
		String assigningAuthority = "aBPR";
		MenuData mdPatient = null;
		ExpressionEvaluator ee = getExpressionEvaluator();
		logger.info("Extension = " + extension + " Account: "
				+ getAccount().getId());
		MenuStructure msPatient = menuBean.findMenuStructure(tm.getAccountId(),
				"echr:patient");
		List<MenuData> mdPatients = menuBean.findMenuDataById(getAccount(),
				root, extension);

		if (mdPatients.size() > 0) {
			mdPatient = mdPatients.get(0);
			menuBean.removeReferencingMenuData(mdPatient);
		}

		if (mdPatient == null) {
			mdPatient = new MenuData();
		}
		
		mdPatient.setMenuStructure(msPatient.getAccountMenuStructure());
		mdPatient.setDocumentId(getDocument().getId());
		mdPatient.setAccount(getAccount());
		mdPatient.addPlaceholderID(root, extension, assigningAuthority);

		ee.pushContext();
		ee.addVariable("trim", trim);
		menuBean.populateMenuData(ee, mdPatient);
		setPatientDetails(mdPatient, trimAct);
		persistMenuData(mdPatient);
		ee.popContext();
		getWorkingMemory().insert(mdPatient);

		return mdPatient;
	}
	
	/**
	 * Sets patient details in menudata.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param mdPatient - patient menudata
	 * @param trimAct - parent act
	 */
	public void setPatientDetails(MenuData mdPatient, ActEx trimAct) {
		try {
			List<ObservationValueSlot> patientInfo = trimAct.getRelationship().get("patientInfo").getAct().getObservation().getValues();
			String[] patnameParts = patientInfo.get(1).getST().getValue().split(", ");
			mdPatient.setString01(patnameParts[0]);
			
			if (patnameParts.length > 1) {
				patnameParts = patnameParts[1].split(" ");
				mdPatient.setString02(patnameParts[0]);
				String lastName = "";
				
				if (patnameParts.length > 1) {
					lastName = patnameParts[1];
				}
				
				mdPatient.setString03(lastName + "(" + patientInfo.get(0).getST().getValue() + ")");
			}
			
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			mdPatient.setDate01(df.parse(patientInfo.get(2).getTS().getValue()));
			mdPatient.setString04(patientInfo.get(3).getST().getValue());
			mdPatient.setExtendedField("homeAddr1", patientInfo.get(4).getST().getValue());
			mdPatient.setExtendedField("homeAddr2", patientInfo.get(5).getST().getValue());
			mdPatient.setExtendedField("homeCity", patientInfo.get(6).getST().getValue());
			mdPatient.setExtendedField("homeState", patientInfo.get(7).getST().getValue());
			mdPatient.setExtendedField("homeZip", patientInfo.get(8).getST().getValue());
			mdPatient.setExtendedField("homeCountry", patientInfo.get(9).getST().getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates menudatas of shared information.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param mdInShare - inshare menudata
	 * @return mds - menudata list
	 */
	public List<MenuData> createMdForInShare(MenuData mdInShare){
		for (TolvenId eventId : trim.getTolvenEventIds()) {
			if ((getAccount().getId() + "").equals(eventId.getAccountId())) {
				MenuData activityMD = menuBean.findDefaultedMenuDataItem(
						getAccount(), eventId.getPath());
				activityMD.setDeleted(true);
				menuBean.persistMenuData(activityMD);
				List<MenuData> activityMdList = menuBean.findReferencingMDs(
						activityMD, menuBean.findMenuStructure(getAccount()
								.getId(), "echr:activity:all"));

				for (MenuData md : activityMdList) {
					md.setDeleted(true);
					menuBean.persistMenuData(md);
				}

				break;
			}
		}

		String patPath = ((ActEx) trim.getAct()).getRelationship().get(
				"patientInfo").getAct().getObservation().getValues().get(0)
				.getST().toString();
		String root = ELFunctions.computeIDRoot(getAccount());
		String assigningAuthority = "aBPR";
		String placeholderPath = "";

		for (Application application : trim.getApplications()) {
			if ("echr".equals(application.getName())
					&& application.getInstance().contains("echr:patient:")) {
				placeholderPath = application.getInstance();
			}
		}

		placeholderPath = placeholderPath.replace(":medication", ":currentMedication");
		placeholderPath = placeholderPath.replace(":wip", ":currentMedication");
		logger.info("placeholderPath: " + placeholderPath);
		MenuStructure msPatient = menuBean.findMenuStructure(tm.getAccountId(),
				placeholderPath);
		List<MenuData> mds = null;
		
		if (placeholderPath.equals("echr:patient:problem")) {
			mds = processProblems(trim, msPatient, patPath, assigningAuthority, root);
		} else if (placeholderPath.equals("echr:patient:px")) {
			mds = processProcedures(trim, msPatient, patPath, assigningAuthority, root);
		} else if (placeholderPath.equals("echr:patient:allergy")) {
			mds = processAllergies(trim, msPatient, patPath, assigningAuthority, root);
		} else if (placeholderPath.equals("echr:patient:currentMedication")) {
			if ("Prescription".equals(trim.getAct().getTitle().getST().getValue())) {
				mds = processPatientPrescriptions(trim, msPatient, patPath, assigningAuthority, root);
			} else {
				mds = processMedications(trim, msPatient, patPath, assigningAuthority, root);
			}
		} else if (placeholderPath.equals("echr:patient:laboratoryOrder")) {
			mds = processLabResults(trim, msPatient, patPath, assigningAuthority, root);
		}
		
		return mds;
	}
	
	/**
	 * Processes the menudata of problems.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param trim - shared trim
	 * @param msPatient - patient menu structure
	 * @param patPath - patient path
	 * @param assigningAuthority - assigning authority
	 * @param root - root
	 * @return mds - menudata list
	 */
	public List<MenuData> processProblems(Trim trim, MenuStructure msPatient,
			String patPath, String assigningAuthority, String root) {
		ActEx trimAct = (ActEx) trim.getAct();
		List<MenuData> mds = new ArrayList<MenuData>();
		
		if (trimAct.getRelationship().get("submitStatus").getAct()
				.getObservation().getValues().get(0).getINT().getValue() == 1L) {
			List<ActRelationship> entries = trimAct.getRelationshipsList().get(
					"entry");
			String extension = null;
			List<MenuData> mdList = new ArrayList<MenuData>();
			MenuData md = null;
			
			for (ActRelationship actRelationship : entries) {
				try {
					extension = patPath + "-"
							+ actRelationship.getAct().getId().getIIS().get(0).getExtension() + "-" + root;
					mdList = menuBean.findMenuDataById(getAccount(), root, extension);

					if (mdList.size() > 0) {
						md = mdList.get(0);
						menuBean.removeReferencingMenuData(md);
					}

					if (md == null) {
						md = new MenuData();
					}
					
					md.setMenuStructure(msPatient.getAccountMenuStructure());
					md.setDocumentId(getDocument().getId());
					md.setAccount(getAccount());
					md.addPlaceholderID(root, extension, assigningAuthority);
					ExpressionEvaluator ee = getExpressionEvaluator();
					ee.pushContext();
					ee.addVariable("trim", trim);
					menuBean.populateMenuData(ee, md);
					DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
					try {
						md.setDate01(df.parse(actRelationship.getAct()
								.getEffectiveTime().getTS().getValue().substring(0, 8)));
	
					} catch (Exception e) {
						logger.info("Failed to set date01 in menudata");
					}
	
					md.setString01(actRelationship.getAct().getObservation()
							.getValues().get(0).getCE().getDisplayName());
					md.setString05(getAuthor(trimAct));
					md.setString03("ACTIVE");
					md.setLong02(1L);
					persistMenuData(md);
					ee.popContext();
					getWorkingMemory().insert(md);
					mds.add(md);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return mds;
	}
	
	/**
	 * Processes the menudata of allergies.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param trim - shared trim
	 * @param msPatient - patient menu structure
	 * @param patPath - patient path
	 * @param assigningAuthority - assigning authority
	 * @param root - root
	 * @return mds - menudata list
	 */
	public List<MenuData> processAllergies(Trim trim, MenuStructure msPatient,
			String patPath, String assigningAuthority, String root) {

		ActEx trimAct = (ActEx) trim.getAct();
		ActRelationship allergyRel = trimAct.getRelationship().get(
				"allergyDetails");
		List<MenuData> mds = new ArrayList<MenuData>();
		
		try {
			String extension = patPath + "-" + trimAct.getId().getIIS().get(0).getExtension() + "-" + root;
			List<MenuData> mdList = menuBean.findMenuDataById(getAccount(), root, extension);
			MenuData md = null;
			
			if (mdList.size() > 0) {
				md = mdList.get(0);
				menuBean.removeReferencingMenuData(md);
			}
			
			if (md == null) {
				md = new MenuData();
			}
			
			md.setMenuStructure(msPatient.getAccountMenuStructure());
			md.setDocumentId(getDocument().getId());
			md.setAccount(getAccount());
			md.addPlaceholderID(root, extension, assigningAuthority);
			ExpressionEvaluator ee = getExpressionEvaluator();
			ee.pushContext();
			ee.addVariable("trim", trim);
			menuBean.populateMenuData(ee, md);
			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			try {
				md.setDate01(df.parse(trimAct.getEffectiveTime().getTS()
						.getValue().substring(0, 8)));
			} catch (Exception e) {
				logger.info("Failed to set date01 in menudata");
			}

			md.setString01(allergyRel.getAct().getObservation().getValues()
					.get(0).getST().getValue());
			md.setString05(getAuthor(trimAct));
			persistMenuData(md);
			ee.popContext();
			getWorkingMemory().insert(md);
			mds.add(md);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mds;
	}
	
	/**
	 * Processes the menudata of procedures.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param trim - shared trim
	 * @param msPatient - patient menu structure
	 * @param patPath - patient path
	 * @param assigningAuthority - assigning authority
	 * @param root - root
	 * @return mds - menudata list
	 */
	public List<MenuData> processProcedures(Trim trim, MenuStructure msPatient,
			String patPath, String assigningAuthority, String root) {
		ActEx trimAct = (ActEx) trim.getAct();
		List<ActRelationship> entries = trimAct.getRelationshipsList().get("entry");
		List<MenuData> mds = new ArrayList<MenuData>();
		String extension = "";
		List<MenuData> mdList = new ArrayList<MenuData>();
		
		for (ActRelationship actRelationship : entries) {
			try {
				extension = patPath + "-"
						+ actRelationship.getAct().getId().getIIS().get(0).getExtension() + "-" + root;
				mdList = menuBean.findMenuDataById(getAccount(), root, extension);
				MenuData md = null;
				
				if (mdList.size() > 0) {
					md = mdList.get(0);
					menuBean.removeReferencingMenuData(md);
				}

				if (md == null) {
					md = new MenuData();
				}
				
				md.setMenuStructure(msPatient.getAccountMenuStructure());
				md.setDocumentId(getDocument().getId());
				md.setAccount(getAccount());
				md.addPlaceholderID(root, extension, assigningAuthority);
				ExpressionEvaluator ee = getExpressionEvaluator();
				ee.pushContext();
				ee.addVariable("trim", trim);
				menuBean.populateMenuData(ee, md);
				DateFormat df = new SimpleDateFormat("yyyyMMdd");

				try {
					md.setDate01(df.parse(actRelationship.getAct()
							.getEffectiveTime().getTS().getValue().substring(0, 8)));

				} catch (Exception e) {
					logger.info("Failed to set date01 in menudata");
				}

				md.setString01(actRelationship.getAct().getCode().getCE().getDisplayName());
				md.setString05(getAuthor(trimAct));
				persistMenuData(md);
				ee.popContext();
				getWorkingMemory().insert(md);
				mds.add(md);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mds;
	}
	
	/**
	 * Processes the menudata of medications.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param trim - shared trim
	 * @param msPatient - patient menu structure
	 * @param patPath - patient path
	 * @param assigningAuthority - assigning authority
	 * @param root - root
	 * @return mds - menudata list
	 */
	public List<MenuData> processMedications(Trim trim, MenuStructure msPatient,
			String patPath, String assigningAuthority,
			String root) {
		ActEx trimAct = (ActEx) trim.getAct();
		List<ActRelationship> entries = ((ActEx) trimAct.getRelationship().get(
				"medications").getAct()).getRelationshipsList().get(
				"medication");
		List<MenuData> mds = new ArrayList<MenuData>();
		String extension = "";
		List<MenuData> mdList = new ArrayList<MenuData>();
		
		for (ActRelationship actRelationship : entries) {
			try {
				extension = patPath + "-"
						+ actRelationship.getAct().getId().getIIS().get(0).getExtension() + "-" + root;
				mdList = menuBean.findMenuDataById(getAccount(), root, extension);
				MenuData md = null;
				
				if (mdList.size() > 0) {
					md = mdList.get(0);
					menuBean.removeReferencingMenuData(md);
				}

				if (md == null) {
					md = new MenuData();
				}
				
				md.setMenuStructure(msPatient.getAccountMenuStructure());
				md.setDocumentId(getDocument().getId());
				md.setAccount(getAccount());
				md.addPlaceholderID(root, extension, assigningAuthority);
				ExpressionEvaluator ee = getExpressionEvaluator();
				ee.pushContext();
				ee.addVariable("trim", trim);
				menuBean.populateMenuData(ee, md);
				DateFormat df = new SimpleDateFormat("yyyyMMdd");

				try {
					md.setDate01(df.parse(trim.getAct().getEffectiveTime()
							.getTS().getValue().substring(0, 8)));

				} catch (Exception e) {
					logger.info("Failed to set date01 in menudata");
				}

				md.setString01(actRelationship.getAct().getObservation()
						.getValues().get(0).getST().getValue());
				persistMenuData(md);
				ee.popContext();
				getWorkingMemory().insert(md);
				mds.add(md);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mds;
	}
	
	/**
	 * Processes the menudata of lab results.
	 * 
	 * added on 02/03/2011
	 * @author valsaraj
	 * @param trim - shared trim
	 * @param msPatient - patient menu structure
	 * @param patPath - patient path
	 * @param assigningAuthority - assigning authority
	 * @param root - root
	 * @return mds - menudata list
	 */
	public List<MenuData> processLabResults(Trim trim, MenuStructure msPatient,
			String patPath, String assigningAuthority,
			String root) {
		ActEx trimAct = (ActEx) trim.getAct();
		List<ActRelationship> entries = trimAct.getRelationshipsList().get("entry");
		List<MenuData> mds = new ArrayList<MenuData>();
		String extension = "";
		List<MenuData> mdList = new ArrayList<MenuData>();
		
		for (ActRelationship actRelationship : entries) {
			try {
				extension = patPath + "-"
						+ actRelationship.getAct().getId().getIIS().get(0).getExtension() + "-" + root;
				mdList = menuBean.findMenuDataById(getAccount(), root, extension);
				MenuData md = null;
				
				if (mdList.size() > 0) {
					md = mdList.get(0);
					menuBean.removeReferencingMenuData(md);
				}

				if (md == null) {
					md = new MenuData();
				}
				
				md.setMenuStructure(msPatient.getAccountMenuStructure());
				md.setDocumentId(getDocument().getId());
				md.setAccount(getAccount());
				md.addPlaceholderID(root, extension, assigningAuthority);
				ExpressionEvaluator ee = getExpressionEvaluator();
				ee.pushContext();
				ee.addVariable("trim", trim);
				menuBean.populateMenuData(ee, md);
				DateFormat df = new SimpleDateFormat("yyyyMMdd");

				try {
					md.setDate01(df.parse(actRelationship.getAct().getEffectiveTime()
							.getTS().getValue().substring(0, 8)));

				} catch (Exception e) {
					logger.info("Failed to set date01 in menudata");
				}

				md.setString01(actRelationship.getAct().getCode().getCE().getDisplayName());
				md.setString03(getAuthor(trimAct));
				md.setString04(getOrderStatus(actRelationship));
				persistMenuData(md);
				ee.popContext();
				getWorkingMemory().insert(md);
				mds.add(md);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mds;
	}
	
	/**
	 * Processes the menudata of patient prescriptions.
	 * 
	 * added on 02/04/2011
	 * @author valsaraj
	 * @param trim - shared trim
	 * @param msPatient - patient menu structure
	 * @param patPath - patient path
	 * @param assigningAuthority - assigning authority
	 * @param root - root
	 * @return mds - menudata list
	 */
	public List<MenuData> processPatientPrescriptions(Trim trim, MenuStructure msPatient,
			String patPath, String assigningAuthority, String root) {
		ActEx trimAct = (ActEx) trim.getAct();
		ActRelationship prescriptionRel = trimAct.getRelationship().get(
				"toSureScripts");
		List<MenuData> mds = new ArrayList<MenuData>();
		
		try {
			String extension = patPath + "-"
					+ trimAct.getId().getIIS().get(0).getExtension() + "-" + root;
			List<MenuData> mdList = menuBean.findMenuDataById(getAccount(), root, extension);
			MenuData md = null;
			
			if (mdList.size() > 0) {
				md = mdList.get(0);
				menuBean.removeReferencingMenuData(md);
			}
		
			if (md == null) {
				md = new MenuData();
			}
	
			md.setMenuStructure(msPatient.getAccountMenuStructure());
			md.setDocumentId(getDocument().getId());
			md.setAccount(getAccount());
			md.addPlaceholderID(root, extension, assigningAuthority);
			ExpressionEvaluator ee = getExpressionEvaluator();
			ee.pushContext();
			ee.addVariable("trim", trim);
			menuBean.populateMenuData(ee, md);
			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			try {
				md.setDate01(df.parse(trimAct.getEffectiveTime().getTS()
						.getValue().substring(0, 8)));
			} catch (Exception e) {
				logger.info("Failed to set date01 in menudata");
			}

			md.setString01(prescriptionRel.getAct().getObservation().getValues()
					.get(21).getST().getValue());
			persistMenuData(md);
			ee.popContext();
			getWorkingMemory().insert(md);
			mds.add(md);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mds;
	}

	/**
	 * Returns the author's name.
	 * 
	 * added on 02/10/2011
	 * @author valsaraj
	 * @param trimAct - act
	 * @return author - author's name
	 */
	public String getAuthor(ActEx trimAct) {
		String author = "";

		try {
			author = trimAct.getRelationship().get("patientAccount").getAct()
					.getObservation().getValues().get(1).getST().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return author;
	}
	
	/**
	 * Returns the order status.
	 * 
	 * added on 02/10/2011
	 * @author valsaraj
	 * @param actRelationship - ActRelationship of an entry
	 * @return author - order status
	 */
	public String getOrderStatus(ActRelationship actRelationship) {
		String orderStatus = "";

		try {
			orderStatus = ((ActEx) actRelationship.getAct()).getRelationship()
					.get("orderStatus").getLabel().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return orderStatus;
	}
}

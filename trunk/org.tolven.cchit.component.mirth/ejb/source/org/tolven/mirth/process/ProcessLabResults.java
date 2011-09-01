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
 * @version $Id: ProcessLabResults.java 2466 2011-08-15 20:29:17Z kanag.kuttiannan $
 */  

package org.tolven.mirth.process;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.bean.CreatorBean;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.TrimHeader;
import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.entity.DocXML;
import org.tolven.mirth.api.ProcessLabResultsLocal;
import org.tolven.mirth.api.ProcessLabResultsRemote;
import org.tolven.trim.BindPhase;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.PQ;
import org.tolven.trim.ST;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipEx;
import org.tolven.trim.ex.ObservationValueSlotEx;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimEx;

import ca.uhn.hl7v2.model.v25.datatype.CWE;
import ca.uhn.hl7v2.model.v25.datatype.IS;
import ca.uhn.hl7v2.model.v25.datatype.SN;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_SPECIMEN;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.SPM;

@Stateless
@Local( ProcessLabResultsLocal.class )
@Remote( ProcessLabResultsRemote.class )
public class ProcessLabResults extends CreatorBean{
	
    private static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	ContinuityOfCareRecordEx ccr;
	@EJB TrimLocal trimBean;
	@EJB CreatorLocal creatorBean;
	@EJB MenuLocal menuBean;
	@EJB DocumentLocal documentBean;
	
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private ORU_R01 labResult;
	
	
	public void saveLabResults(ORU_R01 obj, AccountUser accountUser, Date now,PrivateKey userPrivateKey){
		labResult = obj;
		String root = ELFunctions.computeIDRoot(accountUser.getAccount());
		MenuData mdPatient = null;
		boolean patientExists = false;
		try {
			String patientId = labResult.getPATIENT_RESULT().getPATIENT().getPID().getPatientID().getIDNumber().getValue();
			// check for patient
			List<MenuData> mdPatients = menuBean.findMenuDataById(accountUser.getAccount(), root, "echr:patient-"+patientId);
			if (mdPatients.size() > 0) {
				 mdPatient = mdPatients.get(0);
				 patientExists = true;
			}
			if(patientExists){
				MenuData md = createTRIMPlaceholder(accountUser, "labResultDoc", "echr:patient-"+patientId+":results:lab", now,userPrivateKey);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TolvenMessage tm = new TolvenMessage();
	}
	
	/**
	 * Instantiate a new document and new event pointing to it. The event also points to the eventual placeholder.
	 * @param accountUser
	 * @param trimPath The trimName The key to the MenuStructure item containing the template XML. 
	 * @param context
	 * @param now
	 * @param alias - when binding trim, use this alias for the context object.
	 * @return MenuData - containing the event, not the placeholder
	 * @throws JAXBException
	 * @throws TRIMException
	 */
	public MenuData createTRIMPlaceholder( AccountUser accountUser, String trimPath, String context, Date now, PrivateKey userPrivateKey ) throws Exception {
		MenuData mdTrim = null;
		TrimHeader trimHeader = trimBean.findOptionalTrimHeader(trimPath);
		if (trimHeader==null) {
			// Get the TRIM template as XML
			// If the account doesn't know about this, then we'll allow access to the accountTemplate for this account type.
			mdTrim = menuBean.findDefaultedMenuDataItem(accountUser.getAccount(), trimPath);
			if (mdTrim==null) throw new IllegalArgumentException( "No TRIM item found for " + trimPath);
			trimHeader = mdTrim.getTrimHeader();
		}
		if (trimHeader==null) throw new IllegalArgumentException( "No TRIM found for " + trimPath);
		TrimEx trim = null;
		try {
			trim = trimBean.parseTrim(trimHeader.getTrim(), accountUser, context, now, null );
		} catch (RuntimeException e) {
			throw new RuntimeException( "Error parsing TRIM '" + trimHeader.getName() + "'", e );
		}
		// Setup variables we'll need for populate evaluations
		
		MenuPath contextPath = new MenuPath(context);
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.putAll(contextPath.getNodeValues());
		{
			String assignedPath = accountUser.getProperty().get("assignedAccountUser");
			if (assignedPath!=null) {
				MenuData assigned = menuBean.findMenuDataItem(accountUser.getAccount().getId(), assignedPath);
				variables.put("assignedAccountUser", assigned);
			}
		}
		variables.put("trim", trim);

		// Create an event to hold this trim document
		DocXML docXML = documentBean.createXMLDocument( TRIM_NS, accountUser.getUser().getId(), accountUser.getAccount().getId() );
		docXML.setSignatureRequired( isSignatureRequired(trim, accountUser.getAccount().getAccountType().getKnownType() ) );
		logger.info( "Document (placeholder) created, id: " + docXML.getId());
		// Call computes for the first time now
		computeScan( trim, accountUser, contextPath, now, docXML.getDocumentType());
		// Bind to placeholders
		placeholderBindScan( accountUser.getAccount(), trim, mdTrim, contextPath, now, BindPhase.CREATE, docXML);
		// Create an event
		MenuData mdEvent = establishEvent( accountUser.getAccount(), trim, now, variables);
		if (mdEvent==null) {
			throw new RuntimeException( "Unable to create instance of event for " + trim.getName());
		}
		OBX obsResult = labResult.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION().getOBX();
		OBR obsRequest = labResult.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
		
		mdEvent.setDocumentId(docXML.getId());
		ActEx trimAct = (ActEx)trim.getAct();
		//Date
		trimAct.getEffectiveTime().getTS().setValue(obsResult.getDateTimeOfTheAnalysis().getTime().getValue());
		//Test
		//trimAct.getTitle().getST().setValue(obsResult.getObservationIdentifier().getText().getValue());
		//mdEvent.setString01(obsResult.getObservationIdentifier().getText().getValue());
		ActRelationshipEx testRel = (ActRelationshipEx)trimAct.getRelationship().get("test");
		// test name
		testRel.getAct().getTitle().getST().setValue(obsRequest.getObr4_UniversalServiceIdentifier().getCe2_Text().getValue());
		//ordering MD
		if(obsRequest.getOrderingProvider().length > 0)
			testRel.getAct().getObservation().getValues().get(0).getST().setValue(obsRequest.getOrderingProvider()[0].getGivenName().getValue());
		// lab
		testRel.getAct().getObservation().getValues().get(1).getST().setValue(obsRequest.getFillerOrderNumber().getEi2_NamespaceID().toString());
		ActRelationshipEx resultRel = (ActRelationshipEx)trimAct.getRelationship().get("result");
		//result name
		resultRel.getAct().getTitle().getST().setValue(obsResult.getObservationIdentifier().getText().getValue());
		
		String loincCode=obsResult.getObservationIdentifier().getCe1_Identifier().getValue();
		
		if(!(loincCode==null) && !(loincCode.indexOf("-")==-1)  )
			loincCode=loincCode.split("-")[0];
		else
			loincCode="";
		
		System.out.println("*************************************************");
		System.out.println("LOINC Code: "+loincCode);
		System.out.println("*************************************************");
		
		resultRel.getAct().getObservation().getValues().get(3).getST().setValue(loincCode);
		
		PQ resultVal = resultRel.getAct().getObservation().getValues().get(0).getPQ();
		//value
		resultVal.setValue(Double.parseDouble(((SN)obsResult.getObservationValue(0).getData()).getNum1().getValue()));
		//unit
		resultVal.setUnit(obsResult.getUnits().getIdentifier().getValue());
		//result
		resultVal.setOriginalText(obsResult.getObservationResultStatus().getValue());
		PQ normalRange = resultRel.getAct().getObservation().getValues().get(1).getPQ();
		//normal range
		normalRange.setOriginalText(obsResult.getReferencesRange().getValue());
		//unit
		normalRange.setUnit(obsResult.getUnits().getIdentifier().getValue());
		String abnormalFlag = "";
		// abnormal flag
		if(obsResult.getAbnormalFlags().length > 0){
			for (IS is : obsResult.getAbnormalFlags()) {
				if(is!=null && is.getValue()!=null && !"".equals(is.getValue()))
				abnormalFlag += is.getValue()+", ";
			}
			if(abnormalFlag.length()>2)
				abnormalFlag = abnormalFlag.substring(0, (abnormalFlag.length()-2));
			resultRel.getAct().getObservation().getValues().get(2).getST().setValue(abnormalFlag);
			System.out.println(abnormalFlag);
		}
		//Interp
		resultRel.getAct().getText().getST().setValue(labResult.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR().getPrincipalResultInterpreter().getNameOfPerson().getGivenName().getValue());
		ActRelationshipEx specRel = (ActRelationshipEx)trimAct.getRelationship().get("specimen");
		List<ObservationValueSlot> specValues = specRel.getAct().getObservation().getValues();
		// specimen source
		specValues.get(0).getST().setValue(obsRequest.getObr15_SpecimenSource().getSps1_SpecimenSourceNameOrCode().getCwe1_Identifier().getValue());
		ORU_R01_SPECIMEN specimen = labResult.getPATIENT_RESULT().getORDER_OBSERVATION().getSPECIMEN();
		if(specimen != null){
			//specimen type
			specRel.getAct().getTitle().getST().setValue(specimen.getSPM().getSpecimenType().getCwe1_Identifier().getValue());
			// specimen quality
			specValues.get(1).getST().setValue(specimen.getSPM().getSpecimenQuality().getCwe1_Identifier().getValue());
			// specimen condition
			String specimenCondition = "";
			if(specimen.getSPM().getSpecimenCondition() != null){
				for (CWE cwe : specimen.getSPM().getSpecimenCondition()) {
					if(cwe!=null && cwe.getCwe1_Identifier()!=null && !"".equals(cwe.getCwe1_Identifier()))
						specimenCondition += cwe.getCwe1_Identifier()+", ";
				}
				if(specimenCondition.length()>2)
					specimenCondition = specimenCondition.substring(0, specimenCondition.length()-2);
			}
			specValues.get(2).getST().setValue(specimenCondition);
			// specimen reject reason
			String specimenRejectReason = "";
			if(specimen.getSPM().getSpecimenRejectReason() != null){
				for (CWE cwe : specimen.getSPM().getSpecimenRejectReason()) {
					if(cwe!=null && cwe.getCwe1_Identifier()!=null && !"".equals(cwe.getCwe1_Identifier()))
						specimenRejectReason += cwe.getCwe1_Identifier()+", ";
				}
				if(specimenRejectReason.length()>2)
					specimenRejectReason = specimenRejectReason.substring(0, specimenRejectReason.length()-2);
			}
			specValues.get(3).getST().setValue(specimenRejectReason);
		}
			
		//source
		trim.getMessage().getSender().setProviderName(labResult.getMSH().getSendingFacility().getNamespaceID().getValue());
		// Marshal the finished TRIM into XML and store in the document.
		marshalToDocument( trim, docXML );
		
		System.out.println(docXML.toString());
		
		// Make sure this item shows up on the activity list
		addToWIP(mdEvent, trim, now, variables );
		creatorBean.submit(mdEvent, accountUser,userPrivateKey);
		return mdEvent;
	}
	
}

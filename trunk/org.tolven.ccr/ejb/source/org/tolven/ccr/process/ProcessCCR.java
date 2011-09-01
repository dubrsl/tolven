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
 * @version $Id: ProcessCCR.java,v 1.1 2009/06/29 06:35:44 jchurin Exp $
 */  

package org.tolven.ccr.process;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.drools.FactException;
import org.drools.StatefulSession;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.ccr.ActorReferenceType;
import org.tolven.ccr.ActorType;
import org.tolven.ccr.CommunicationType;
import org.tolven.ccr.Direction;
import org.tolven.ccr.IDType;
import org.tolven.ccr.InstructionType;
import org.tolven.ccr.Location;
import org.tolven.ccr.QuantityType;
import org.tolven.ccr.Reaction;
import org.tolven.ccr.StructuredProductType;
import org.tolven.ccr.api.ProcessCCRLocal;
import org.tolven.ccr.ex.AlertTypeEx;
import org.tolven.ccr.ex.CCRSourceHelper;
import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.ccr.ex.DateTimeTypeEx;
import org.tolven.ccr.ex.EncounterTypeEx;
import org.tolven.ccr.ex.PersonNameTypeEx;
import org.tolven.ccr.ex.PlanTypeEx;
import org.tolven.ccr.ex.ProblemTypeEx;
import org.tolven.ccr.ex.ProcedureTypeEx;
import org.tolven.ccr.ex.ResultTypeEx;
import org.tolven.ccr.ex.StructuredProductTypeEx;
import org.tolven.ccr.ex.TestTypeEx;
import org.tolven.core.entity.Account;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.CCRException;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.el.ExpressionEvaluator;

@Stateless
@Local( ProcessCCRLocal.class )
public class ProcessCCR extends AppEvalAdaptor implements ProcessCCRLocal {
	
    private static final String CCRns = "urn:astm-org:CCR";	
    private ExpressionEvaluator ccree;
	private Logger logger = Logger.getLogger(this.getClass());
	ContinuityOfCareRecordEx ccr;
	
	public void process(Object message, Date now ) {
		try {
			if (message instanceof TolvenMessage || 
					message instanceof TolvenMessageWithAttachments) { 
				TolvenMessage tm = (TolvenMessage) message;
				// We're only interested in CCR messages
				if (CCRns.equals(tm.getXmlNS())) {
					associateDocument( tm, now );		
					// Run the rules now
					runRules( );
					logger.info( "Processing CCR document " + getDocument().getId() + " for account: " + tm.getAccountId());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Exception in CCR processor", e);
		}
	}
	@Override
	protected void loadWorkingMemory( StatefulSession workingMemory ) throws Exception {
		ccr = (ContinuityOfCareRecordEx) xmlBean.unmarshal(tm.getXmlNS(), new ByteArrayInputStream(tm.getPayload()));
		workingMemory.insert(ccr);
	    workingMemory.insert(this);
		processPatient( workingMemory );
	}

	@Override
	protected ExpressionEvaluator getExpressionEvaluator() {
		if (ccree==null) {
			ccree = new TrimExpressionEvaluator();
			ccree.addVariable( "now", getNow());
			ccree.addVariable( "doc", getDocument());
			ccree.addVariable(TrimExpressionEvaluator.ACCOUNT, getAccount());
			ccree.addVariable(DocumentType.DOCUMENT, getDocument());
		}
		return ccree;
	}

	/**
	 * If we get a CCR from another account, there's nothing we can do special - except to process the ccr as normal.
	 */
	@Override
	protected DocBase scanInboundDocument(DocBase doc) throws Exception {
		return doc;
	}

	/**
	 * Process a single CCR message. Verify the account and user
	 * @param tm A TolvenMessage
	 * @throws JAXBException 
	 * @throws CCRException 
	 * @throws ParseException 
	 * @throws CCRException 
	 * @throws IOException 
	 * @throws IOException 
	 */
	public void processPatient(StatefulSession workingMemory) throws JAXBException, ParseException, CCRException,
			IOException {
		Account account = getAccount();
		String knownType = account.getAccountType().getKnownType();
		MenuStructure msPatient = menuBean.findMenuStructure(account.getId(), knownType + ":patient");
		ActorType pat = ccr.getPatientActor();
		MenuData mdPatient = null;
		List<MenuData> mdPatients = null;
		String mrn = null;
		String phone = null;
		String lastIdVal = null;

		// See if this patient is a current patient		
		for (IDType id : pat.getIDs()) {
			String idText = id.getType().getText();
			String idVal = id.getID();
			if ("MRN".equals(id.getType().getText()) || "SSN".equals(id.getType().getText())) {
				mdPatients = menuBean.findMenuDataById(account, idText, idVal);
				if (mdPatients.size() > 0) {
					mdPatient = mdPatients.get(0);
				}
				lastIdVal = idVal;
				break;
			}
		}
		// For grins, try the actor ID - it's wrong but it might work
		if (mdPatient == null) {
			mdPatients = menuBean.findMenuDataById(account, "MRN", pat.getActorObjectID());
			if (mdPatients.size() > 0) {
				mdPatient = mdPatients.get(0);
			}
			lastIdVal = pat.getActorObjectID();
		}
		if (mdPatient == null) {
			mdPatient = new MenuData();
			mdPatient.setMenuStructure(msPatient.getAccountMenuStructure());
			mdPatient.setDocumentId(getDocument().getId());
			mdPatient.setAccount(account);
			// OK, get started...
			pat = ccr.getPatientActor();
			DateTimeTypeEx dob = (DateTimeTypeEx) pat.getPerson().getDateOfBirth();
			if (dob != null && dob.getExactDateTime() != null) {
				mdPatient.setDate01(dob.getDateValue());
				mdPatient.setDate01String(dob.getDateValue().toString());
			}
			PersonNameTypeEx name = (PersonNameTypeEx) ccr.getpersonNameType(); //pat.getPerson().getName().getCurrentName();
			if (name != null) {
				mdPatient.setString01(name.getFamilyString());
				mdPatient.setString02(name.getGivenString());
				mdPatient.setString03(name.getMiddleString());
			} else {
				mdPatient.setString01("Name Missing");
				mdPatient.setString02(lastIdVal);
			}
			mdPatient.setField("sex", pat.getPerson().getGender().getText());

			mdPatient.setField("gender", sexToGender(pat.getPerson().getGender().getText()));
			// Process external ids
			for (IDType id : pat.getIDs()) {
				ActorReferenceType actorRef = id.getIssuedBy();
				String issuedBy = null;
				if (actorRef != null && actorRef.getActorID() != null) {
					for (ActorType actor : ccr.getActors().getActor()) {
						if (actorRef.getActorID().equals(actor.getActorObjectID()) && actor.getOrganization() != null) {
							issuedBy = actor.getOrganization().getName();
							break;
						}
					}
				}
				mdPatient.addPlaceholderID(id.getType().getText(), id.getID(), issuedBy);
				if ("MRN".equals(id.getType().getText()))
					//mrn = id.getID();
					mdPatient.setString05(id.getID());
			}
			// get the phone numbers
			try {
				// set the phone numbers
				for (CommunicationType ct : pat.getTelephone()) {
					if (ct.getType() != null
							&& ct.getType().getText() != null
							&& (ct.getType().getText().startsWith("Home") || ct.getType().getText().startsWith(
									"Residen"))) {
						phone = ct.getValue();
						mdPatient.setField("homeTelecom", phone);
					} else if ("Work".equals(ct.getType().getText()))
						mdPatient.setField("workTelecom", ct.getValue());
				}
			} catch (Exception e) {
				phone = null;
			}
			// Address
			try {
				for (ActorType.Address add : pat.getAddress()) {
					if (add.getType() == null || "Home".equals(add.getType().getText())) {
						mdPatient.setField("homeAddr1", add.getLine1());
						mdPatient.setField("homeAddr2", add.getLine2());
						mdPatient.setField("homeCity", add.getCity());
						mdPatient.setField("homeState", add.getState());
						mdPatient.setField("homeZip", add.getPostalCode());
						mdPatient.setField("homeCountry", add.getCountry());
					}
				}
			} catch (Exception e) {

			}
			menuBean.persistMenuData(mdPatient);
			workingMemory.insert(mdPatient);
		}

		processAllergies(mdPatient, workingMemory);
		processProblems(mdPatient, workingMemory);
		processMedications(mdPatient, workingMemory);
		processResults(mdPatient, workingMemory);
		processVitals(mdPatient, workingMemory);
		processAppointments(mdPatient, workingMemory);
		processPlanOfCare(mdPatient, workingMemory);
		processImmunizations(mdPatient, workingMemory);
		processProcedures(mdPatient, workingMemory);
	}

	/**
	 * In CCR, allergies are in the alerts lists.
	 * 
	 * @param mdPatient
	 * @param getDocument().getId()
	 * @param ccr
	 * @throws JAXBException
	 * @throws ParseException
	 * @throws CCRException
	 * @throws IOException
	 */
	public void processAllergies(MenuData mdPatient, StatefulSession workingMemory) throws JAXBException,
			ParseException, CCRException, IOException {
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getAlerts() == null)
			return;
		MenuStructure msAllergy = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "allergy");
		for (AlertTypeEx alert : ccr.getAlerts()) {
			// NOTE: Bump the number of allergies in the patient for each active
			// allergy we find.
			mdPatient.setLong01((mdPatient.getLong01() == null ? 1 : mdPatient.getLong01() + 1));
			// Create the allergy
			MenuData mdAllergy = new MenuData();
			mdAllergy.setMenuStructure(msAllergy.getAccountMenuStructure());
			mdAllergy.setDocumentId(getDocument().getId());
			mdAllergy.setAccount(mdPatient.getAccount());
			mdAllergy.setParent01(mdPatient);
			mdAllergy.setString01(alert.getDescriptionText());
			mdAllergy.setDateType(alert.getDateTypeText());
			DateTimeTypeEx dt = alert.getDateTimeType(alert.getDateTypeText());
			setDateTime01(alert, mdAllergy, dt);
			String allergySource = CCRSourceHelper.getActorFromSource(alert, ccr.getActors());
			mdAllergy.setString02(allergySource);
			mdAllergy.setString03(alert.getStatusText());

			// Get Reaction, if given any in the CCR
			try {
				Reaction reaction = alert.getReaction().get(0);
				mdAllergy.setString04(reaction.getDescription().getText());
				mdAllergy.setString05(reaction.getSeverity().getText());
			} catch (Exception e) {

			}
			menuBean.persistMenuData(mdAllergy);
			workingMemory.insert(mdAllergy);
		}
	}

	public void processMedications(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getMedications() == null)
			return;
		MenuStructure msMedication = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "medication");
		StringBuffer productDetails = null;

		int count = 0;
		for (StructuredProductTypeEx med : ccr.getMedications()) {
			try {
				count++;
				// Create the medication
				MenuData mdMedication = new MenuData();
				mdMedication.setMenuStructure(msMedication.getAccountMenuStructure());
				mdMedication.setDocumentId(getDocument().getId());
				mdMedication.setAccount(mdPatient.getAccount());
				mdMedication.setParent01(mdPatient);
				mdMedication.setField("title", med.getProductNameText()); // generic
				productDetails = new StringBuffer(med.getProductNameText());
				productDetails.append(" ");
				mdMedication.setField("brand", med.getProductBrandNameText());// brand
				mdMedication.setDateType(med.getDateTypeText());
				DateTimeTypeEx dt = med.getDateTimeType(med.getDateTypeText());
				setDateTime01(med, mdMedication, dt);
				String medSource = CCRSourceHelper.getActorFromSource(med, ccr.getActors());
				mdMedication.setField("source", medSource);
				mdMedication.setField("status", med.getStatusText());
				// Add other data for the placeholder

				StructuredProductType.Product.Strength strength = null;
				try {
					strength = med.getProduct().get(0).getStrength().get(0);
				} catch (Exception e) {
					strength = null;
				}
				StructuredProductType.Product.Form form = null;
				try {
					form = med.getProduct().get(0).getForm().get(0);
				} catch (Exception e) {
					form = null;
				}

				QuantityType qt = null;
				try {
					qt = med.getQuantity().get(0);
				} catch (Exception e) {
					qt = null;
				}

				Direction direction = null;
				try {
					direction = med.getDirections().get(0).getDirection().get(0);
				} catch (Exception e) {
					direction = null;
				}
				InstructionType itype = null;
				try {
					itype = med.getPatientInstructions().getInstruction().get(0);
				} catch (Exception e) {
					itype = null;
				}

				// Strength 400 MG
				if (strength != null) {
					try {
						mdMedication.setPqValue01(Double.parseDouble(strength.getValue()));
					} catch (Exception nfe) {
						mdMedication.setPqValue01(0.0);
					}
					mdMedication.setPqStringVal01(strength.getValue());
					productDetails.append(strength.getValue());
					productDetails.append(" ");
					if (strength.getUnits() != null) {
						mdMedication.setPqUnits01(strength.getUnits().getUnit());
					} else {
						mdMedication.setPqUnits01("");
					}
					productDetails.append(mdMedication.getPqUnits01());
				}
				if (form != null) {
					productDetails.append(" ");
					productDetails.append(form.getText());
				}
				if (productDetails != null && productDetails.toString().trim().length() > 0)
					mdMedication.setString01(productDetails.toString());

				// directions
				if (direction != null) {
					StringBuffer dosageBuffer = new StringBuffer();
					if (direction.getDescription() != null) {
						dosageBuffer.append(direction.getDescription().getText());
						dosageBuffer.append(" ");
					}
					if (direction.getDoseIndicator() != null) {
						dosageBuffer.append(direction.getDoseIndicator().getText());
						dosageBuffer.append(" ");
					}
					if (direction.getDose() != null) {
						try {
							org.tolven.ccr.Direction.Dose dose = direction.getDose().get(0);
							dosageBuffer.append(dose.getValue());
							dosageBuffer.append(" ");
							if (dose.getUnits() != null && dose.getUnits().getUnit() != null)
								dosageBuffer.append(dose.getUnits().getUnit());
							dosageBuffer.append(" ");
						} catch (Exception e) {

						}
					}
					if (direction.getFrequency() != null) {
						try {
							org.tolven.ccr.FrequencyType fq = direction.getFrequency().get(0);
							dosageBuffer.append(fq.getValue());
						} catch (Exception e) {

						}
					}
					mdMedication.setString05(dosageBuffer.toString());
				}
				// patient instructions
				if (itype != null)
					mdMedication.setString07(itype.getText());

				if (qt != null) {
					try {
						mdMedication.setPqValue02(Double.parseDouble(qt.getValue()));
					} catch (Exception nfe) {
						mdMedication.setPqValue02(0.0);
					}
					mdMedication.setPqStringVal02(qt.getValue());
					if (strength != null && strength.getUnits() != null) {
						mdMedication.setPqUnits02(qt.getUnits().getUnit());
					} else {
						mdMedication.setPqUnits02("");
					}
				}
				menuBean.persistMenuData(mdMedication);
				workingMemory.insert(mdMedication);
			} catch (Exception e) {
				String name; 
				if (med !=null && med.getProductNameText()!=null) {
					name = med.getProductNameText();
				} else {
					name = "--no product name--";
				}
				throw new RuntimeException( "Error processing medication #" + count + " ("+ name + ")", e );
			}
		}
	}

	public void processResults(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		// Extract Results (in CCR, a ResultType can also be an observation)
		// We do results
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getResults() == null)
			return;

		MenuStructure msRequest = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "request");
		MenuStructure msResult = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "result");
		for (ResultTypeEx result : ccr.getResults()) {
			try {
				// Create the top-level result
				MenuData mdRequest = new MenuData();
				mdRequest.setMenuStructure(msRequest.getAccountMenuStructure());
				mdRequest.setDocumentId(getDocument().getId());
				mdRequest.setAccount(mdPatient.getAccount());
				mdRequest.setParent01(mdPatient);
				mdRequest.setField("title", result.getDescriptionText());
	
				mdRequest.setDateType(result.getDateTypeText());
				DateTimeTypeEx dt = result.getDateTimeType(result.getDateTypeText());
				setDateTime01(result, mdRequest, dt);
	
				String resultSource = CCRSourceHelper.getActorFromSource(result, ccr.getActors());
				if (resultSource == null) {
					resultSource = CCRSourceHelper.getFromName(ccr);
				}
				mdRequest.setField("source", resultSource);
				mdRequest.setField("status", result.getStatusText());
				if (result.getType() != null) {
					mdRequest.setField("type", result.getType().getText());
				}
				menuBean.persistMenuData(mdRequest);
				workingMemory.insert(mdRequest);
				// ResultTypeEx.IMAGING_X_RAY_TYPE.equals(result.getType().getText())) {
				// We also need to represent individual test results
				// (independent of result)
				// for (TestType test : result.getTest()) {
				for (TestTypeEx test : ccr.getTests(result)) {
					MenuData mdTest = new MenuData();
					mdTest.setMenuStructure(msResult.getAccountMenuStructure());
					mdTest.setDocumentId(getDocument().getId());
					mdTest.setAccount(mdPatient.getAccount());
					mdTest.setParent01(mdPatient);
					mdTest.setParent02(mdRequest);
					mdTest.setField("title", test.getDescriptionText());
					mdTest.setField("source", resultSource);
					mdTest.setField("status", result.getStatusText());
					// Note date is copied from the result (parent), not the
					// test (child)
					mdTest.setDateType(result.getDateTypeText());
					setDateTime01(result, mdTest, dt);

					// Value+units
					if (test.getTestResult() != null) {
						String resultValue = test.getTestResult().getValue();
						if (resultValue != null) {
							try {
								mdTest.setPqValue01(Double.parseDouble(resultValue));
							} catch (NumberFormatException nfe) {
								mdTest.setPqValue01(0.0);
							}
							if (resultValue.length()>30) {
								mdTest.setPqStringVal01("see drilldown");
								mdTest.setField("comment", resultValue);
							} else {
								mdTest.setPqStringVal01(resultValue);
							}
							if (test.getTestResult().getUnits() != null) {
								mdTest.setPqUnits01(test.getTestResult().getUnits().getUnit());
							} else {
								mdTest.setPqUnits01("");
							}
						}
						if (test.getTestResult() != null && test.getTestResult().getDescription() != null
								&& test.getTestResult().getDescription().size() > 0) {
							String resultText = test.getTestResult().getDescription().get(0).getText();
							mdTest.setField("title", resultText);
						}
					}
					menuBean.persistMenuData(mdTest);
					workingMemory.insert(mdTest);
				}
			} catch (Exception e) {
				throw new RuntimeException( "Except processing CCR Result " + result.getDescriptionText(), e);
			}
		}
	}

	public void processVitals(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		// Extract Results (in CCR, a ResultType can also be an observation)
		// We do results
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getVitalSigns() == null)
			return;

		MenuStructure msObservation = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "observation");

		for (ResultTypeEx result : ccr.getVitals()) {
			// Create the result
			MenuData mdObservation = new MenuData();
			mdObservation.setMenuStructure(msObservation.getAccountMenuStructure());
			mdObservation.setDocumentId(getDocument().getId());
			mdObservation.setAccount(mdPatient.getAccount());
			mdObservation.setParent01(mdPatient);
			mdObservation.setString01(result.getDescriptionText());
			// DateTimeTypeEx dt = result.getDateTimeType("OBSERVED");
			DateTimeTypeEx dt = result.getDateTimeType(result.getDateTypeText());

			setDateTime01(result, mdObservation, dt);
			String medSource = CCRSourceHelper.getActorFromSource(result, ccr.getActors());
			mdObservation.setString02(medSource);
			mdObservation.setString03(result.getStatusText());
			menuBean.persistMenuData(mdObservation);
			workingMemory.insert(mdObservation);
		}
	}

	public void processPlanOfCare(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		// Extract Results (in CCR, a ResultType can also be an observation)
		// We do results
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getPlanOfCare() == null)
			return;

		MenuStructure msPlan = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "plan");

		for (PlanTypeEx plan : ccr.getPlans()) {
			// Create the result
			MenuData mdPlan = new MenuData();
			mdPlan.setMenuStructure(msPlan.getAccountMenuStructure());
			mdPlan.setDocumentId(getDocument().getId());
			mdPlan.setAccount(mdPatient.getAccount());
			mdPlan.setParent01(mdPatient);
			DateTimeTypeEx dt = plan.getDateTimeType(plan.getDateTypeText());
			setDateTime01(plan, mdPlan, dt);
			String medSource = CCRSourceHelper.getActorFromSource(plan, ccr.getActors());
			mdPlan.setString01(plan.getTypeText());
			mdPlan.setString02(plan.getDescriptionText());
			mdPlan.setString03(medSource);
			mdPlan.setString04(plan.getStatusText());
			mdPlan.setDateType(plan.getDateTypeText());
			menuBean.persistMenuData(mdPlan);
			workingMemory.insert(mdPlan);

		}
	}

	public void processImmunizations(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getImmunizations() == null)
			return;
		MenuStructure msImmunization = null;
		MenuStructure msImmunizationList = null;

		try {
			msImmunization = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
					.getMenuStructure(), "immunization");
			msImmunizationList = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
					.getMenuStructure(), "immu:current");
		} catch (Exception e) {
			return;
		}
		for (StructuredProductTypeEx immu : ccr.getImmunizations()) {
			// Create the Immunization
			MenuData mdImmunization = new MenuData();
			mdImmunization.setMenuStructure(msImmunization.getAccountMenuStructure());
			mdImmunization.setDocumentId(getDocument().getId());
			mdImmunization.setAccount(mdPatient.getAccount());
			mdImmunization.setParent01(mdPatient);
			mdImmunization.setString01(immu.getProductNameText()); // generic
			mdImmunization.setDateType(immu.getDateTypeText());
			DateTimeTypeEx dt = immu.getDateTimeType(immu.getDateTypeText());
			setDateTime01(immu, mdImmunization, dt);
			String immuSource = CCRSourceHelper.getActorFromSource(immu, ccr.getActors());
			mdImmunization.setString02(immuSource);
			mdImmunization.setString03(immu.getStatusText());

			menuBean.persistMenuData(mdImmunization);
			workingMemory.insert(mdImmunization);
		}
	}

	public void processProcedures(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getProcedures() == null)
			return;
		MenuStructure msProcedureGroup = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "procedureGroup");
		MenuStructure msProcedure = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(),
				msProcedureGroup.getAccountMenuStructure(), "procedure");

		for (ProcedureTypeEx proc : ccr.getProcedures()) {
			// Create the Procedure
			MenuData mdProcedure = new MenuData();
			MenuData mdProcedureGroup = new MenuData();

			// Create PlaceHolder for ProcedureGroup First

			mdProcedureGroup.setMenuStructure(msProcedureGroup.getAccountMenuStructure());
			mdProcedureGroup.setDocumentId(getDocument().getId());
			mdProcedureGroup.setAccount(mdPatient.getAccount());
			mdProcedureGroup.setParent01(mdPatient);
			mdProcedureGroup.setString01(proc.getDescriptionText()); // generic
			mdProcedureGroup.setString04(proc.getTypeText()); // generic
			mdProcedureGroup.setDateType(proc.getDateTypeText());
			DateTimeTypeEx dt = proc.getDateTimeType(proc.getDateTypeText());
			setDateTime01(proc, mdProcedureGroup, dt);
			String procSource = CCRSourceHelper.getActorFromSourceList(proc.getSource(), ccr.getActors());
			mdProcedureGroup.setString02(procSource);
			mdProcedureGroup.setString03(proc.getStatusText());

			menuBean.persistMenuData(mdProcedureGroup);
			workingMemory.insert(mdProcedureGroup);

			// Now Create Placeholder for Procedure
			mdProcedure.setMenuStructure(msProcedure.getAccountMenuStructure());
			mdProcedure.setDocumentId(getDocument().getId());
			mdProcedure.setAccount(mdPatient.getAccount());
			mdProcedure.setParent01(mdProcedureGroup);
			mdProcedure.setString01(proc.getDescriptionText()); // generic
			mdProcedure.setString04(proc.getTypeText()); // generic
			mdProcedure.setDateType(proc.getDateTypeText());
			// DateTimeTypeEx dt = proc.getDateTimeType(proc.getDateTypeText());
			setDateTime01(proc, mdProcedure, dt);
			// String procSource =
			// CCRSourceHelper.getActorFromSourceList(proc.getSource(), ccr
			// .getActors());
			mdProcedure.setString02(procSource);
			mdProcedure.setString03(proc.getStatusText());

			menuBean.persistMenuData(mdProcedure);
			workingMemory.insert(mdProcedure);
		}
	}

	/**
	 * @param result
	 * @param md
	 * @param dt
	 * @throws ParseException
	 */
	private void setDateTime01(ResultTypeEx result, MenuData md, DateTimeTypeEx dt) throws ParseException {
		Date dateTime = null;
		String dateTimeStr = "";
		if (dt == null) {
			dateTime = result.getExactDateTime();
			if (dateTime == null) {
				dateTimeStr = result.getApproximateDateTime();
			} else {
				dateTimeStr = dateTime.toString();
			}
		} else {
			dateTime = dt.getDateValue();
			if (dateTime != null)
				dateTimeStr = dateTime.toString();
			else
				dateTimeStr = dt.getApproximateDate();
		}
		md.setDate01(dateTime);
		md.setDate01String(dateTimeStr);
	}

	/**
	 * @param alert
	 * @param md
	 * @param dt
	 * @throws ParseException
	 */
	private void setDateTime01(AlertTypeEx alert, MenuData md, DateTimeTypeEx dt) throws ParseException {
		Date dateTime = null;
		String dateTimeStr = "";
		if (dt == null) {
			dateTime = alert.getExactDateTime();
			if (dateTime == null) {
				dateTimeStr = alert.getApproximateDateTime();
			} else {
				dateTimeStr = dateTime.toString();
			}
		} else {
			dateTime = dt.getDateTimeValue();
			if (dateTime != null)
				dateTimeStr = dateTime.toString();
			else
				dateTimeStr = dt.getApproximateDate();
		}
		md.setDate01(dateTime);
		md.setDate01String(dateTimeStr);
	}

	/**
	 * @param med
	 * @param md
	 * @param dt
	 * @throws ParseException
	 */
	private void setDateTime01(StructuredProductTypeEx med, MenuData md, DateTimeTypeEx dt) throws ParseException {
		Date dateTime = null;
		String dateTimeStr = "";
		if (dt == null) {
			dateTime = med.getExactDateTime();
			if (dateTime == null) {
				dateTimeStr = med.getApproximateDateTime();
			} else {
				dateTimeStr = dateTime.toString();
			}
		} else {
			dateTime = dt.getDateTimeValue();
			if (dateTime != null)
				dateTimeStr = dateTime.toString();
			else
				dateTimeStr = dt.getApproximateDate();
		}
		md.setDate01(dateTime);
		md.setDate01String(dateTimeStr);
	}

	/**
	 * @param plan
	 * @param md
	 * @param dt
	 * @throws ParseException
	 */
	private void setDateTime01(PlanTypeEx plan, MenuData md, DateTimeTypeEx dt) throws ParseException {
		Date dateTime = null;
		String dateTimeStr = "";
		if (dt == null) {
			dateTime = plan.getExactDateTime();
			if (dateTime == null) {
				dateTimeStr = plan.getApproximateDateTime();
			} else {
				dateTimeStr = dateTime.toString();
			}
		} else {
			dateTime = dt.getDateValue();
			if (dateTime != null)
				dateTimeStr = dateTime.toString();
			else
				dateTimeStr = dt.getApproximateDate();
		}
		md.setDate01(dateTime);
		md.setDate01String(dateTimeStr);
	}

	/**
	 * @param proc
	 * @param md
	 * @param dt
	 * @throws ParseException
	 */
	private void setDateTime01(ProcedureTypeEx proc, MenuData md, DateTimeTypeEx dt) throws ParseException {
		Date dateTime = null;
		String dateTimeStr = "";
		if (dt == null) {
			dateTime = proc.getExactDateTime();
			if (dateTime == null) {
				dateTimeStr = proc.getApproximateDateTime();
			} else {
				dateTimeStr = dateTime.toString();
			}
		} else {
			dateTime = dt.getDateValue();
			if (dateTime != null)
				dateTimeStr = dateTime.toString();
			else
				dateTimeStr = dt.getApproximateDate();
		}
		md.setDate01(dateTime);
		md.setDate01String(dateTimeStr);
	}

	/**
	 * @param problem
	 * @param md
	 * @param dt
	 * @throws ParseException
	 */
	private void setDateTime01(ProblemTypeEx problem, MenuData md, DateTimeTypeEx dt) throws ParseException {
		Date dateTime = null;
		String dateTimeStr = "";
		if (dt == null) {
			dateTime = problem.getExactDateTime();
			if (dateTime == null) {
				dateTimeStr = problem.getApproximateDateTime();
			} else {
				dateTimeStr = dateTime.toString();
			}
		} else {
			dateTime = dt.getDateValue();
			if (dateTime != null)
				dateTimeStr = dateTime.toString();
			else
				dateTimeStr = dt.getApproximateDate();
		}
		md.setDate01(dateTime);
		md.setDate01String(dateTimeStr);
	}

	public void processProblems(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getProblems() == null)
			return;
		MenuStructure msProblem = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "problem");
		for (ProblemTypeEx problem : ccr.getProblems()) {
			// Create the problem
			MenuData mdProblem = new MenuData();
			mdProblem.setMenuStructure(msProblem.getAccountMenuStructure());
			mdProblem.setDocumentId(getDocument().getId());
			mdProblem.setAccount(mdPatient.getAccount());
			mdProblem.setParent01(mdPatient);
			mdProblem.setString01(problem.getDescriptionText());
			mdProblem.setDateType(problem.getDateTypeText());

			DateTimeTypeEx dt = problem.getDateTimeType(problem.getDateTypeText());
			setDateTime01(problem, mdProblem, dt);
			String problemSource = CCRSourceHelper.getActorFromSource(problem, ccr.getActors());
			mdProblem.setString02(problemSource);
			mdProblem.setString03(problem.getStatusText());
			menuBean.persistMenuData(mdProblem);
			workingMemory.insert(mdProblem);
		}
	}

	public void processAppointments(MenuData mdPatient, StatefulSession workingMemory) throws ParseException {
		if (ccr.getBody() == null)
			return;
		if (ccr.getBody().getEncounters() == null)
			return;
		MenuStructure msAppointment = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "appointment");
		MenuStructure msEncounter = menuBean.findDescendentMenuStructure(mdPatient.getAccount().getId(), mdPatient
				.getMenuStructure(), "encounter");
		for (EncounterTypeEx encounter : ccr.getEncounters()) {
			//Date apptDate = encounter.getDateTimeType("Encounter DateTime").getDateValue();
			if ("EM".equals(encounter.getType().getText())) {
				// Create the appointment
				MenuData mdEncounter = new MenuData();
				mdEncounter.setMenuStructure(msEncounter.getAccountMenuStructure());
				mdEncounter.setDocumentId(getDocument().getId());
				mdEncounter.setAccount(mdPatient.getAccount());
				mdEncounter.setParent01(mdPatient);
				mdEncounter.setString01(encounter.getDescriptionText());
				DateTimeTypeEx dt = encounter.getDateTimeType("Encounter DateTime");
				Date apptDate = null;
				String apptDateStr = "";
				if (dt == null) {
					apptDate = encounter.getExactDateTime();
					if (apptDate == null) {
						apptDateStr = encounter.getApproximateDateTime();
					} else {
						apptDateStr = apptDate.toString();
					}
				} else {
					apptDate = dt.getDateValue();
					apptDateStr = apptDate.toString();
				}
				mdEncounter.setDate01(apptDate);
				mdEncounter.setDate01String(apptDateStr);
				// Get the attending and admitting
				if (encounter.getPractitioners()!=null) {
					for (ActorReferenceType actorRef : encounter.getPractitioners().getPractitioner()) {
						String actorId = actorRef.getActorID();
						String role = actorRef.getActorRole().get(0).getText();
						if (role.startsWith("Attend")) {
							mdEncounter.setField("otherAttender", actorId);
						}
						if (role.startsWith("Admit")) {
							mdEncounter.setField("otherAdmitter", actorId);
						}
					}
				}
				if (encounter.getLocations()!=null && encounter.getLocations().getLocation().size() > 0) {
					Location location = encounter.getLocations().getLocation().get(0);
					mdEncounter.setField("otherLocation", location.getDescription().getText());
				}
				
//				mdAppointment.setString02(encounter.getStatusText());
				menuBean.persistMenuData(mdEncounter);
				workingMemory.insert(mdEncounter);
				
			} else {
				// Create the appointment
				MenuData mdAppointment = new MenuData();
				mdAppointment.setMenuStructure(msAppointment.getAccountMenuStructure());
				mdAppointment.setDocumentId(getDocument().getId());
				mdAppointment.setAccount(mdPatient.getAccount());
				mdAppointment.setParent01(mdPatient);
				mdAppointment.setString01(encounter.getDescriptionText());
				DateTimeTypeEx dt = encounter.getDateTimeType("Encounter DateTime");
				Date apptDate = null;
				String apptDateStr = "";
				if (dt == null) {
					apptDate = encounter.getExactDateTime();
					if (apptDate == null) {
						apptDateStr = encounter.getApproximateDateTime();
					} else {
						apptDateStr = apptDate.toString();
					}
				} else {
					apptDate = dt.getDateValue();
					apptDateStr = apptDate.toString();
				}
				mdAppointment.setDate01(apptDate);
				mdAppointment.setDate01String(apptDateStr);
				//				mdAppointment.setString02(encounter.getStatusText());
				menuBean.persistMenuData(mdAppointment);
				workingMemory.insert(mdAppointment);
			}
		}
	}
}

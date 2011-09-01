/**
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
 * @author Kul Bhushan
 * @version $Id: ContinuityOfCareRecordEx.java,v 1.3 2009/05/25 04:56:16 jchurin Exp $
 */

package org.tolven.ccr.ex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;

import org.tolven.ccr.ActorType;
import org.tolven.ccr.AlertType;
import org.tolven.ccr.DateTimeType;
import org.tolven.ccr.EncounterType;
import org.tolven.ccr.PersonNameType;
import org.tolven.ccr.ProblemType;
import org.tolven.ccr.ResultType;
import org.tolven.ccr.PlanType;
import org.tolven.ccr.ProcedureType;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.StructuredProductType;
import org.tolven.ccr.TestType;
import org.tolven.ccr.ActorType.Person;
import org.tolven.ccr.ActorType.Person.Name;
import org.tolven.doc.entity.CCRException;

public class ContinuityOfCareRecordEx extends ContinuityOfCareRecord{
	
	@XmlTransient
    private  Map<String, ActorType> actorMap;

	
	public List<AlertTypeEx> getAlerts() {
		List<AlertTypeEx> alertTypeEx = new ArrayList<AlertTypeEx>();
		for (AlertType at : getBody().getAlerts().getAlert()) {
			alertTypeEx.add( (AlertTypeEx) at);
		}
		return alertTypeEx;
	}
	
	public List<StructuredProductTypeEx> getMedications() {
		List<StructuredProductTypeEx> structuredProductTypeEx = new ArrayList<StructuredProductTypeEx>();
		for (StructuredProductType spt : getBody().getMedications().getMedication()) {
			structuredProductTypeEx.add( (StructuredProductTypeEx) spt);
		}
		return structuredProductTypeEx;
	}
	
	public List<StructuredProductTypeEx> getImmunizations() {
		List<StructuredProductTypeEx> structuredProductTypeEx = new ArrayList<StructuredProductTypeEx>();
		for (StructuredProductType spt : getBody().getImmunizations().getImmunization()) {
			structuredProductTypeEx.add( (StructuredProductTypeEx) spt);
		}
		return structuredProductTypeEx;
	}

	public List<ProcedureTypeEx> getProcedures() {
		List<ProcedureTypeEx> ProcedureTypeEx = new ArrayList<ProcedureTypeEx>();
		for (ProcedureType spt : getBody().getProcedures().getProcedure()) {
			ProcedureTypeEx.add( (ProcedureTypeEx) spt);
		}
		return ProcedureTypeEx;
	}
	
	public List<ResultTypeEx> getResults() {	
		List<ResultTypeEx> resultTypeEx = new ArrayList<ResultTypeEx>();
		for (ResultType rt : getBody().getResults().getResult()) {		
			resultTypeEx.add( (ResultTypeEx) rt);
		}		
		return resultTypeEx;
	}
	
	public List<ResultTypeEx> getVitals() {	
		List<ResultTypeEx> resultTypeEx = new ArrayList<ResultTypeEx>();
		for (ResultType rt : getBody().getVitalSigns().getResult()) {		
			resultTypeEx.add( (ResultTypeEx) rt);
		}		
		return resultTypeEx;
	}

	public List<TestTypeEx> getTests(ResultTypeEx rte) {	
		List<TestTypeEx> testTypeEx = new ArrayList<TestTypeEx>();
		for (TestType tt : rte.getTest()) {		
			testTypeEx.add( (TestTypeEx) tt);
		}		
		return testTypeEx;
	}
	
	public List<PlanTypeEx> getPlans() {
		List<PlanTypeEx> planTypeEx = new ArrayList<PlanTypeEx>();
		for (PlanType at : getBody().getPlanOfCare().getPlan()) {
			planTypeEx.add( (PlanTypeEx) at);
		}
		return planTypeEx;
	}	

	public List<ProblemTypeEx> getProblems() {	
		List<ProblemTypeEx> problemTypeEx = new ArrayList<ProblemTypeEx>();
		for (ProblemType pte : getBody().getProblems().getProblem()) {		
			problemTypeEx.add( (ProblemTypeEx) pte);
		}		
		return problemTypeEx;
	}
	
	public List<EncounterTypeEx> getEncounters() {
		
		List<EncounterTypeEx> encounterTypeEx = new ArrayList<EncounterTypeEx>();
		for (EncounterType ete : getBody().getEncounters().getEncounter()) {
			encounterTypeEx.add( (EncounterTypeEx) ete);
		}
		return encounterTypeEx;
	}	

	public PersonNameType getpersonNameType() throws JAXBException, CCRException {		
		ActorType patientActor = getPatientActor();
		if (patientActor==null) return null;
		Person person = patientActor.getPerson();
		if (person==null) return null;
		Name name = person.getName();
		if (name==null) return null;
		return name.getCurrentName() ;		
	}
	
	public List<DateTimeTypeEx> getDateTimeType() {	
		List<DateTimeTypeEx> dateTimeTypeEx = new ArrayList<DateTimeTypeEx>();
		for (DateTimeType pte : getDateTimeType()) {		
			dateTimeTypeEx.add( (DateTimeTypeEx) pte);
		}		
		return dateTimeTypeEx;
	}	
	
    /**
	 * We build and maintain a transient map of ActorType instances in the document. 
	 * Thus, when an object has an ActorLink, we can easily find the actor.
	 * We keep this map up if we create another actor.
	 * If the object graph already exists and this is the first time we've been asked for the actorMap,
	 * then walk through the object graph to get the actors.
	 * @return
	 * @throws JAXBException 
	 */
	public Map<String, ActorType> getActorMap() {
		if (actorMap==null) { 
			actorMap = new HashMap<String, ActorType>( 10 );
			ContinuityOfCareRecord.Actors ccrActors = getActors();
			if (ccrActors!=null)
				for (ActorType actor : ccrActors.getActor()) {
					actorMap.put(actor.getActorObjectID(), actor);
			}
		}
		return actorMap;
	}

	public void setActorMap(Map<String, ActorType> actorMap) {
		this.actorMap = actorMap;
	}

	/**
	 * A convenience method that finds the patient object which just contains an actorId and
	 * then returns the actor.
	 * @return the actor or null if no patient yet.
	 * @throws JAXBException 
	 * @throws CCRException 
	 */
	public ActorType getPatientActor() throws JAXBException, CCRException {
		List<ContinuityOfCareRecord.Patient> pats = getPatient();
		if (pats.size()>1) throw new CCRException( "[tolven Compliance Issue]Siamese twins not supported" );
		if (pats.size()==0) return null;
		ContinuityOfCareRecord.Patient pat = pats.get(0);
		ActorType actor = getActorMap().get(pat.getActorID());
		if (actor==null) throw new CCRException( "Missing Actor for Patient A2.5.2.6(1)" );
//		TolvenLogger.info( "PatientActor: " + actor.getActorObjectID(), ContinuityOfCareRecord.class);
		return actor;
	}
	/**
	 * Create a new actor. It should be referenced from at least one place in the
	 * document. Use convenience methods such as addNewPatient instead of this method in most cases.
	 * @return The new Actor
	 * @throws CCRException
	 * @throws JAXBException
	 */
	public ActorType addNewActor( ) throws CCRException, JAXBException {
		ActorType actor = new ActorType();
		// Creating a unique id is a little tricky since we may not have created the original
		// scheme so we'll just guess at numbers til we find something unique.
		for (int x = 1; x < 1000000; x++) {
			String id = String.format("A%06d", x);
			if (null==getActorMap().get(id)) {
				actor.setActorObjectID(id);
				break;
			}
		}
		// Remember that we're storing actors at the Tolven document level until marshall time
		// when we put it in the CCR document.
		getActorMap().put(actor.getActorObjectID(), actor);
//		TolvenLogger.info( "Adding Actor: " + actor.getActorObjectID(), ContinuityOfCareRecord.class);
		return actor;
	}

	/**
	 * Add a new patient actor to the document and return the actor. the caller is expected to fill out the
	 * actor at this point. The Id has already been populated.
	 * @return the Actor
	 * @throws CCRException 
	 * @throws JAXBException 
	 */
	public ActorType addNewPatient( ) throws CCRException, JAXBException {
		// Create the actor
		ActorType actor = addNewActor();
		// Make this actor the patient
		addPatient( actor );
		return actor;
	}

	/**
	 * The actor already exists, we're just adding this actor as the patient.
	 * Only do this once or you're violating CCR semantics (except for Siamese twins).
	 * We won't complain now, but it could fail during validation.
	 * @param actor
	 * @return
	 * @throws CCRException 
	 * @throws JAXBException 
	 */
	public void addPatient( ActorType actor ) throws CCRException, JAXBException {
		ContinuityOfCareRecord.Patient pat = new ContinuityOfCareRecord.Patient();
		pat.setActorID(actor.getActorObjectID());
		getPatient().add(pat);
	}

}

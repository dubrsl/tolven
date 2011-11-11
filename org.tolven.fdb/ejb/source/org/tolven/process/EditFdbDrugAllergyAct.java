package org.tolven.process;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.tolven.app.AllergyVO;
import org.tolven.app.FDBInterface;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.fdb.entity.FdbAllergenpicklist;
import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActStatus;
import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.Entity;
import org.tolven.trim.ex.ActRelationshipEx;

public class EditFdbDrugAllergyAct extends InsertAct {

	@EJB FDBInterface fdbInterface;
	
	private String fdbDrugAllergyName;
	private String fdbDrugAllergyType;
	private String fdbDrugAllergyCode;
	
	public EditFdbDrugAllergyAct(){
		try {
			if (fdbInterface==null) {
				InitialContext ctx = new InitialContext();
			    fdbInterface = (FDBInterface) ctx.lookup("java:global/tolven/tolvenEJB/FDBBean!org.tolven.app.FDBInterface");
			}
		} catch (NamingException e) {
			throw new RuntimeException( "Unable to access FDBBean in InsertFdbMedicationAct", e);
		}
	}
	
	@Override
	public void compute() throws Exception {
		TolvenLogger.info( "Compute enabled=" + isEnabled(), EditFdbDrugAllergyAct.class);
		super.checkProperties();		
		if (isEnabled() && StringUtils.isNotBlank(getTemplate())) {		
			Integer position = Integer.valueOf(getPosition());
			if (getAction().equals("addDrugAllergyDetails")){
				
	            ActRelationship newRelationship = parseTemplate();
	            newRelationship.setEnabled(isEnableAct());
	            this.getAct().getRelationships().add(newRelationship);		
	            TolvenLogger.info( "added ...", EditFdbDrugAllergyAct.class);
				//this should insert the trim for the severity/reaction
				//this.getTrim().getAct().getObservation().getValues().get(0).setCE(ce);
				
				
			}else if (getAction().equals("remove")){// Remove	
				List<ActRelationship> lActToRemove = new ArrayList<ActRelationship>();
		        for (ActRelationship lRel : this.getAct().getRelationships()) {
		        	if(!lRel.getName().equals(getArName())) 
		        		continue;
		        	if (lRel.getSequenceNumber() != null && lRel.getSequenceNumber().intValue() == position){
		        		lActToRemove.add(lRel);
		        	}
		        }
		        this.getAct().getRelationships().removeAll(lActToRemove);
			}else if (getAction().equals("removeUnsaved")){ //remove unsaved drugs
				List<ActRelationship> lActToRemove = new ArrayList<ActRelationship>();
		        for (ActRelationship lRel : this.getAct().getRelationships()) {
		        	if(!lRel.getName().equals(getArName())) 
		        		continue;
		        	if (!lRel.isEnabled()){
		        		lActToRemove.add(lRel);
		        	}
		        }
		        this.getAct().getRelationships().removeAll(lActToRemove);
			}else if (getAction().equals("selectDrugAllergy")){
				//set CE in observation
				AllergyVO allergy = fdbInterface.findDrugAllergy(getFdbDrugAllergyName());
				CE ce = new CE();
				ce.setCodeSystem("FDB");
				ce.setCodeSystemVersion(fdbInterface.getFdbVersion().getDbversion());
				ce.setDisplayName(allergy.getAllergen());
				ce.setCode(constructAllergyCode(allergy));
				this.getTrim().getAct().getObservation().getValues().get(0).setCE(ce);
			}
			
		    // Reset the Sequence
	        int index = 1;
	        for (ActRelationship lRel : this.getAct().getRelationships()){
	        	if(!lRel.getName().equals(getArName())) // set the sequence number only for the Inserted acts
	        		continue;
	        	lRel.setSequenceNumber(new Integer(index));
	        	index++;
	        }
	        
	        // Disable the Compute since its job is done.
	    	for (Property property : getComputeElement().getProperties()) {
				if (property.getName().equals("enabled")) {
					property.setValue(Boolean.FALSE);
					break;
				}
			}
		}
		TolvenLogger.info( "Compute Execute over", EditFdbMedicationAct.class);
	}
	
	@Override
	public ActRelationship parseTemplate() throws JAXBException {
		//load the trim to be inserted
		ActRelationshipEx  drugallergies = (ActRelationshipEx)super.parseTemplate();
		//load the drug details form the FDB database and fill in the trim being inserted
		try {
			TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
			ee.addVariable("act", drugallergies);
			//drugallergies.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().setValue(allergy.getAllergen());
			AllergyVO allergy = fdbInterface.findDrugAllergy(getFdbDrugAllergyName());
			CE ce = new CE();
			ce.setCodeSystem("FDB");
			ce.setCodeSystemVersion(fdbInterface.getFdbVersion().getDbversion());
			ce.setDisplayName(allergy.getAllergen());
			ce.setCode(constructAllergyCode(allergy));
			this.getTrim().getAct().getObservation().getValues().get(0).setCE(ce);
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return drugallergies;
	}	
	
	private String constructAllergyCode(AllergyVO allergy){
		String allergyType = allergy.getType();
		String allergyCode = allergy.getInternalId().toString();
		if(allergyType.equals("Allergen Group")) {
			allergyCode = allergyCode.concat("_1");
		} else if (allergyType.equals("Drug Name")){
			allergyCode = allergyCode.concat("_2");
		} else if (allergyType.equals("Ingredient")){
			allergyCode = allergyCode.concat("_6");
		} 
		return allergyCode;
	}
	
	public String getFdbDrugAllergyName() {
		return fdbDrugAllergyName;
	}
	public void setFdbDrugAllergyName(String fdbDrugAllergyName) {
		this.fdbDrugAllergyName = fdbDrugAllergyName;
	}
	public String getFdbDrugAllergyType() {
		return fdbDrugAllergyType;
	}
	public void setFdbDrugAllergyType(String fdbDrugAllergyType) {
		this.fdbDrugAllergyType = fdbDrugAllergyType;
	}
	
	public String getFdbDrugAllergyCode() {
		return fdbDrugAllergyCode;
	}

	public void setFdbDrugAllergyCode(String fdbDrugAllergyCode) {
		this.fdbDrugAllergyCode = fdbDrugAllergyCode;
	}
}

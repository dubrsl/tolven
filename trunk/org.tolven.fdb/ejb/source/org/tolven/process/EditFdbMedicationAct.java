package org.tolven.process;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.tolven.app.FDBInterface;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActStatus;
import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.Entity;
import org.tolven.trim.ex.ActRelationshipEx;

public class EditFdbMedicationAct extends InsertAct {

	@EJB FDBInterface fdbInterface;
	
	private String fdbDrugCode;
	private String fdbDrugName;
	private String drugStatus;
	public String getDrugStatus() {
		return drugStatus;
	}
	public void setDrugStatus(String drugStatus) {
		this.drugStatus = drugStatus;
	}
	public EditFdbMedicationAct(){
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
		TolvenLogger.info( "Compute enabled=" + isEnabled(), EditFdbMedicationAct.class);
		super.checkProperties();		
		if (isEnabled() && StringUtils.isNotBlank(getTemplate())) {		
			Integer position = Integer.valueOf(getPosition());
			if (getAction().equals("add")){
	            ActRelationship newRelationship = parseTemplate();
	            newRelationship.setEnabled(isEnableAct());
	            this.getAct().getRelationships().add(newRelationship);		
	            TolvenLogger.info( "added ...", EditFdbMedicationAct.class);
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
	public String getFdbDrugCode() {
		return fdbDrugCode;
	}
	public void setFdbDrugCode(String fdbDrugCode) {
		this.fdbDrugCode = fdbDrugCode;
	}
	public String getFdbDrugName() {
		return fdbDrugName;
	}
	public void setFdbDrugName(String fdbDrugName) {
		this.fdbDrugName = fdbDrugName;
	}
	@Override
	public ActRelationship parseTemplate() throws JAXBException {
		//load the trim to be inserted
		ActRelationshipEx  medication = (ActRelationshipEx)super.parseTemplate();
		//load the drug details form the FDB database and fill in the trim being inserted
		try {
			FdbDispensable drug = fdbInterface.findFdbDispensable(new Integer(getFdbDrugCode()));
			TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
			ee.addVariable("act", medication);
			Entity player= medication.getAct().getParticipations().get(0).getRole().getPlayer();
			CE ce = new CE();
			ce.setCodeSystem("FDB");
			ce.setDisplayName(drug.getDescdisplay());
			ce.setCodeSystemVersion(fdbInterface.getFdbVersion().getDbversion());
			ce.setCode(String.valueOf(drug.getMedid()));
			player.getCode().setCE(ce);
			if(this.getDrugStatus().equalsIgnoreCase("medicationsTaken")){
				medication.getAct().setStatusCode(ActStatus.COMPLETED);				
			}/*else{
				medication.getAct().setStatusCode(ActStatus.ACTIVE);
			}*/				
			//act.getAct().getObservation().getValues().get(0).getST().setValue(drug.getDrugName());			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return medication;
	}	
}

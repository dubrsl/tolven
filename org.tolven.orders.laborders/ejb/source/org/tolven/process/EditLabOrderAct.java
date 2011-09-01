package org.tolven.process;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActStatus;
import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.Entity;
import org.tolven.trim.ex.ActRelationshipEx;

public class EditLabOrderAct extends InsertAct {

	private String listPath;
	private String arType;

	@Override
	public void compute() throws Exception {
		TolvenLogger.info( "Compute enabled=" + isEnabled(), EditLabOrderAct.class);
		super.checkProperties();		
		if (isEnabled() && StringUtils.isNotBlank(getTemplate())) {		
			Integer position = Integer.valueOf(getPosition());
			if (getAction().equals("add")){
	            ActRelationship newRelationship = parseTemplate();
	            newRelationship.setEnabled(true);
	            //newRelationship.setEnabled(isEnableAct());
	            this.getAct().getRelationships().add(newRelationship);		
	            TolvenLogger.info( "added ...", EditLabOrderAct.class);
			}
			if (getAction().equals("remove")){
				List<ActRelationship> lActToRemove = new ArrayList<ActRelationship>();
		        for (ActRelationship lRel : this.getAct().getRelationships()) {
		        	if(!lRel.getName().equals(getArName())) 
		        		continue;
		        	if (lRel.getSequenceNumber() != null && lRel.getSequenceNumber().intValue() == position){
		        		lActToRemove.add(lRel);
		        	}
		        }
		        this.getAct().getRelationships().removeAll(lActToRemove);
			}
			if (getAction().equals("removeUnsaved")){ //remove unsaved drugs
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
		// TolvenLogger.info( "Compute Execute over", EditFdbMedicationAct.class);
	}
			
	public String getListPath() {
		return listPath;
	}
	public void setListPath(String listPath) {
		this.listPath = listPath;
	}
	public String getArType() {
		return arType;
	}
	public void setArType(String arType) {
		this.arType = arType;
	}
	
}

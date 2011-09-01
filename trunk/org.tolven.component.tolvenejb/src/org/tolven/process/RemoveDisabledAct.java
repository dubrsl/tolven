package org.tolven.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Compute.Property;

/**
 * Compute to remove the unsaved acts in the trim. Unsaved Acts in the trim will be identified
 * when isEnabled=false.
 * This is mostly used to remove unsaved problems, medications etc in the respective wizards

 * @author skandula
 *
 */
public class RemoveDisabledAct extends ComputeBase{
	private boolean enabled;
	private String arName;
	
	public void compute( ) throws Exception {
		TolvenLogger.info( "Compute enabled=" + isEnabled(), RemoveDisabledAct.class);
		super.checkProperties();
		if (isEnabled() && StringUtils.isNotBlank(getArName())) {
			Act act = this.getAct();
			List<ActRelationship> lActToRemove = new ArrayList<ActRelationship>();
	        for (ActRelationship lRel : act.getRelationships()) {
	        	if(!lRel.getName().equals(arName)) 
	        		continue;
	        	if (!lRel.isEnabled()){
	        		lActToRemove.add(lRel);
	        	}
	        }
	        act.getRelationships().removeAll(lActToRemove);
			
		    // Reset the Sequence
	        int index = 1;
	        for (ActRelationship lRel : act.getRelationships()){
	        	if(!lRel.getName().equals(arName)) // set the sequence number only for the Inserted acts
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
	}
	
	public String getArName() {
		return arName;
	}

	public void setArName(String arName) {
		this.arName = arName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
}

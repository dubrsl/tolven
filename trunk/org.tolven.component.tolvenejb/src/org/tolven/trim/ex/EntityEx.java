package org.tolven.trim.ex;

import java.util.ArrayList;
import java.util.List;

import org.tolven.trim.ENSlot;
import org.tolven.trim.Entity;
import org.tolven.trim.EntityStatus;
import org.tolven.trim.RoleStatus;
import org.tolven.trim.Transition;
import org.tolven.trim.Transitions;

@SuppressWarnings("serial")
public class EntityEx extends Entity {

	private static final long serialVersionUID = 1L;

	public Boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * Return a list of transitions appropriate to the current event
	 * @return A list of transitions.
	 */
	public List<Transition> getEventTransitions( ) {
		List<Transition> results = new ArrayList<Transition>(10);
		String statusCodeValue = getStatusCodeValue();
		Transitions transitions = this.getTransitions();
		if (transitions!=null ) {
			for (Transition t : transitions.getTransitions()) {
				if (statusCodeValue!=null && statusCodeValue.equals(t.getFrom())) {
					results.add(t);
				} else if (statusCodeValue==null && t.getFrom()==null) {
					results.add(t);
				}
			}
		}
		return results;
	}
	/**
	 * Make an attempt to return the name of the entity
	 * @return A concatenated formatted name or null if none.
	 */
	public String getEntityName() {
		ENSlot slot = getName();
		// If no name slot, no name
		if (slot==null) return null;
		// If no ENs in the slot, then no name.
		if (slot.getENS().size()==0) return null;
		// The first EN will be fine
		ENEx en = (ENEx) slot.getENS().get(0);
		return en.getFormatted();
	}
	
	/**
	 * Set the statusCode using a string to create the enum 
	 * @param code
	 */
	public void setStatusCodeValue(String code) {
		if (code==null) {
			this.setStatusCode(null);
		} else {
			this.setStatusCode(EntityStatus.fromValue(code));
		}
	}
	
	/**
	 * Get the string value of the statusCode 
	 * @return A string containing the statusCode value
	 */
	public String getStatusCodeValue( ) {
		if (this.getStatusCode()==null) return null;
		return this.getStatusCode().value();
	}
	
    public void setAccountShares(List<String> accountShares) {
		this.accountShares = accountShares;
	}

}

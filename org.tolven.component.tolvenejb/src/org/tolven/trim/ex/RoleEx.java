package org.tolven.trim.ex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tolven.trim.Role;
import org.tolven.trim.RoleParticipation;
import org.tolven.trim.RoleStatus;
import org.tolven.trim.Transition;
import org.tolven.trim.Transitions;

@SuppressWarnings("serial")
public class RoleEx extends Role {
	private transient RoleParticipationMap rpMap = null;

	public Boolean getEnabled() {
		return enabled;
	}

	public Map<String, RoleParticipation> getParticipation() {
        if (rpMap == null) {
        	rpMap = new RoleParticipationMap( getParticipations());
        }
        return rpMap;
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
	 * Set the statusCode using a string to create the enum 
	 * @param code
	 */
	public void setStatusCodeValue(String code) {
		if (code==null) {
			this.setStatusCode(null);
		} else {
			this.setStatusCode(RoleStatus.fromValue(code));
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

package org.tolven.trim.ex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActStatus;
import org.tolven.trim.BindTo;
import org.tolven.trim.CD;
import org.tolven.trim.CE;
import org.tolven.trim.Compute;
import org.tolven.trim.Party;
import org.tolven.trim.Transition;
import org.tolven.trim.Transitions;

@SuppressWarnings("serial")
public class ActEx extends Act implements Serializable {
	private transient ActRelationshipMap arMap = null;
	private transient ActRelationshipsMap arsMap = null;
	private transient ActParticipationMap apMap = null;
	
	/**
	 * Return a list of transitions appropriate to the current event
	 * @return A list of transitions.
	 */
	public List<Transition> getEventTransitions( ) {
		List<Transition> results = new ArrayList<Transition>(10);
//		String statusCodeValue = getStatusCodeValue();
		Transitions transitions = this.getTransitions();
		String initalState = transitions.getInitialState();
		if (transitions!=null ) {
			for (Transition t : transitions.getTransitions()) {
				if (initalState!=null && initalState.equals(t.getFrom())) {
					results.add(t);
				} else if (initalState==null && t.getFrom()==null) {
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
			this.setStatusCode(ActStatus.fromValue(code));
		}
	}
	/**
	 * Get the plain enabled flag - which will be null if the flag is not set
	 * @return
	 */
	public Boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * Get the string value of the statusCode 
	 * @return A string containing the statusCode value
	 */
	public String getStatusCodeValue( ) {
		if (this.getStatusCode()==null) return null;
		return this.getStatusCode().value();
	}
	
	public String getCodeValue() {
		if (code!=null) {
			if (code.getCD()!=null) {
				CD cd = code.getCD();
				return cd.getCode();
			}
			if (code.getCE()!=null) {
				CE ce = code.getCE();
				return ce.getCode();
			}
		}
		return "";	// easier for rules
	}

	public Map<String, ActRelationship> getRelationship() {
        if (arMap == null) {
        	arMap = new ActRelationshipMap(getRelationships());
        }
        return arMap;
	}
	
	/** Method to return the relationships with a given name as a list.
	 * @param key
	 * @return
	 */
	public Map<String, List<ActRelationship>> getRelationshipsList(){		
		 if (arsMap == null) {
	        	arsMap = new ActRelationshipsMap(getRelationships());
	        }
	        return arsMap;
	}
	
	public Map<String, ActParticipation> getParticipation() {
        if (apMap == null) {
        	apMap = new ActParticipationMap( getParticipations());
        }
        return apMap;
	}

	public ObservationEx getObservationEx() {
		return (ObservationEx) super.getObservation();
	}

    public void setAccountShares(List<String> accountShares) {
		this.accountShares = accountShares;
	}
    
    public void setEnableAct(Boolean value) {
		this.enabled = value;
	}

	public Boolean getEnableAct() {
		return this.enabled;
	}
	
    @Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}
	/**
	 * Add the included act into this act
	 * @param actInclude
	 */
	public void blend( Act actInclude ) {
		ActEx actIncludeEx = (ActEx) actInclude;
		// Add the included act into this act.
		//this.setActivityTime(actInclude.getActivityTime());
		//this.setAvailabilityTime(actInclude.getAvailabilityTime());
		if (this.getBinds().size()==0) {
			for (BindTo bindTo : actInclude.getBinds()) {
				if (bindTo.getPlaceholder()!=null) {
					this.getBinds().add(bindTo);
				}
			}
		}
		// Blend all computes
		for (Compute compute : actInclude.getComputes()) {
			this.getComputes().add(compute);
		}
		if (this.getClassCode()==null) this.setClassCode(actInclude.getClassCode());
		if (this.getCode()==null) this.setCode(actInclude.getCode());
		if (this.getConfidentialityCode()==null) this.setConfidentialityCode(actInclude.getConfidentialityCode());
		if (this.getDerivationExpr()==null) this.setDerivationExpr(actInclude.getDerivationExpr());
		if (this.getDrilldown()==null) this.setDrilldown(actInclude.getDrilldown());
		if (this.getEffectiveTime()==null) this.setEffectiveTime(actInclude.getEffectiveTime());
		if (this.getActivityTime()==null) this.setActivityTime(actInclude.getActivityTime());
		if (this.getAvailabilityTime()==null) this.setAvailabilityTime(actInclude.getAvailabilityTime());
		if (this.getId()==null) this.setId(actInclude.getId());
		if (this.getIndependentInd()==null) this.setIndependentInd(actInclude.getIndependentInd());
		if (this.getInternalId()==null) this.setInternalId(actInclude.getInternalId());
		if (this.getInterruptibleInd()==null) this.setInterruptibleInd(actInclude.getInterruptibleInd());
		if (this.getLabel()==null) this.setLabel(actInclude.getLabel());
		if (this.getLanguageCode()==null) this.setLanguageCode(actInclude.getLanguageCode());
		if (this.getLevelCode()==null) this.setLevelCode(actInclude.getLevelCode());
		if (this.getMoodCode()==null) this.setMoodCode(actInclude.getMoodCode());
		if (this.getNegationInd()==null) this.setNegationInd(actInclude.getNegationInd());
		if (this.getObservation()==null) this.setObservation(actInclude.getObservation());
		if (this.getPage()==null) this.setPage(actInclude.getPage());
		if (this.getPatientEncounter()==null) this.setPatientEncounter(actInclude.getPatientEncounter());
		if (this.getPriorityCode()==null) this.setPriorityCode(actInclude.getPriorityCode());
		if (this.getProcedure()==null) this.setProcedure(actInclude.getProcedure());
		if (this.getReasonCode()==null) this.setReasonCode(actInclude.getReasonCode());
		if (this.getRepeatNumber()==null) this.setRepeatNumber(actInclude.getRepeatNumber());
		if (this.getStatusCode()==null) this.setStatusCode(actInclude.getStatusCode());
		if (this.getSubstanceAdministration()==null) this.setSubstanceAdministration(actInclude.getSubstanceAdministration());
		if (this.getSupply()==null) this.setSupply(actInclude.getSupply());
		if (this.getText()==null) this.setText(actInclude.getText());
		if (this.getTitle()==null) this.setTitle(actInclude.getTitle());
		if (this.getTransitions()==null) this.setTransitions(actInclude.getTransitions());
		if (this.getTransition()==null) this.setTransition(actInclude.getTransition());
		if (this.getTypeId()==null) this.setTypeId(actInclude.getTypeId());
		if (this.getUncertaintyCode()==null) this.setUncertaintyCode(actInclude.getUncertaintyCode());
		if (this.getSendTos()==null) 
			this.sendTos = actInclude.getSendTos();
		// Need to check raw property, not default getter in this case due to default processing in the default method
		if (this.getEnabled()==null && actIncludeEx.getEnabled()!=null) this.setEnabled(actInclude.isEnabled());
	}
	
	public List<Party> getSendTos(){
		List<Party> list = new ArrayList<Party>();
		for(Party p:super.getSendTos()){
			String args[] = {p.getAccountPath(),p.getAccountId(),String.valueOf(p.getProviderId())};
			list.add(TrimFactory.createParty(args));
		}
		return list;
	}
	
	/** This has to check if the accountShare is already there on the Act since there is no
	 * setter method for sendTos on Act.java
	 * @param sendTos
	 */
	public void setSendTos(List<String> sendTos){
		List<PartyEx> selectedParties = new ArrayList<PartyEx>();
		for(String sendTo:sendTos){
			String args[] = TrimFactory.decode(sendTo);
			PartyEx party = TrimFactory.createParty(args);
			selectedParties.add(party);						
		}
		super.getSendTos().clear();		
		super.getSendTos().addAll(selectedParties);
	}
}

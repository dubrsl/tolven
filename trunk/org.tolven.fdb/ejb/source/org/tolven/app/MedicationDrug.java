package org.tolven.app;

import org.apache.commons.lang.builder.ToStringBuilder;

public class MedicationDrug {
	private long drugCode;
	private String drugName;
	private String strength;
	private String route;
	private String drugFoodInteraction;
	private String duplicateTherapyInteraction;
	private String fdbResponse;
	private String fdbVersion;
	
	
	public String getFdbVersion() {
		return fdbVersion;
	}
	public void setFdbVersion(String fdbVersion) {
		this.fdbVersion = fdbVersion;
	}
	public long getDrugCode() {
		return drugCode;
	}
	public void setDrugCode(long drugCode) {
		this.drugCode = drugCode;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getDrugFoodInteraction() {
		return drugFoodInteraction;
	}
	public void setDrugFoodInteraction(String drugFoodInteraction) {
		this.drugFoodInteraction = drugFoodInteraction;
	}
	public String getDuplicateTherapyInteraction() {
		return duplicateTherapyInteraction;
	}
	public void setDuplicateTherapyInteraction(String duplicateTherapyInteraction) {
		this.duplicateTherapyInteraction = duplicateTherapyInteraction;
	}
	public String getFdbResponse() {
		return fdbResponse;
	}
	public void setFdbResponse(String fdbResponse) {
		this.fdbResponse = fdbResponse;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}

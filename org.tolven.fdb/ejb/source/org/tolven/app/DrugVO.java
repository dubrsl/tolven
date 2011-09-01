/**
 * 
 */
package org.tolven.app;

import java.io.Serializable;

/**
 * @author mohammed
 *
 */
public class DrugVO implements Serializable {
	/**
	 * Auto Generated Serial Version Id
	 */
		private static final long serialVersionUID = 0L;
	/**
	 * Variable to hold the Drug Code
	 */
		private String drugCode;
	/**
	 * Variable to hold the value of the Drug Description
	 */	
		private String drugName;
	/**
	 * Variable to hold the name type of the drug	
	 */
	    private String nameType;	
	/**
	 * @return the drugCode
	 */
	public String getDrugCode() {
		return drugCode;
	}
	/**
	 * @param drugCode the drugCode to set
	 */
	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}
	/**
	 * @return the drugName
	 */
	public String getDrugName() {
		return drugName;
	}
	/**
	 * @param drugName the drugName to set
	 */
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getNameType() {
		return nameType;
	}
	public void setNameType(String nameType) {
		this.nameType = nameType;
	}	
}

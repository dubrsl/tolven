package org.tolven.app;

import java.io.Serializable;

/**
 * @author mohammed
 *
 */
public class AllergyVO implements Serializable {

	/** Auto generated Serial Version ID */
	private static final long serialVersionUID = 5727534441061959530L;
    /**  Variable to hold the name of the allergen */
	private String allergen;
	/** Variable to hold the type of the allergen */
	private String type;
	/** Variable to hold the internalId of the allergen */
	private Long internalId;
	
	
	public Long getInternalId() {
		return internalId;
	}

	public void setInternalId(Long internalId) {
		this.internalId = internalId;
	}

	/**
	 * @return the allergen
	 */
	public String getAllergen() {
		return allergen;
	}
	/**
	 * @param allergen the allergen to set
	 */
	public void setAllergen(String allergen) {
		this.allergen = allergen;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
    
}

package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * Any method of tumor detection that involves obtaining a picture of the tumor,
 * as opposed to a biochemical test or direct observation (biopsy, endoscopy, etc.
 * ).
 * @version 1.0
 * @created 27-Sep-2006 9:53:59 AM
 */
@Entity
@DiscriminatorValue("IMG")
public class Imaging extends Procedure implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Indication if the image was enhanced (Y) or not (N) by the use of a contrast
	 * agent. Values include: Not Applicable; No; Yes
	 */
	@Column private String contrastAgentEnhancement;
	/**
	 * Text description of an imaging enhancement procedure. 
	 */
	@Column private String descriptiveText;
	/**
	 * Unique identifier of an image.
	 */
	@Column private String identifier;
	/**
	 * Numeric value to indicate an increase in voxel signal over time for an MRI,
	 * expressed as signal intensity units per second. 
	 */
	@Column private int rateOfEnhancementValue;

	public Imaging(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getContrastAgentEnhancement() {
		return contrastAgentEnhancement;
	}

	public void setContrastAgentEnhancement(String contrastAgentEnhancement) {
		this.contrastAgentEnhancement = contrastAgentEnhancement;
	}

	public String getDescriptiveText() {
		return descriptiveText;
	}

	public void setDescriptiveText(String descriptiveText) {
		this.descriptiveText = descriptiveText;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getRateOfEnhancementValue() {
		return rateOfEnhancementValue;
	}

	public void setRateOfEnhancementValue(int rateOfEnhancementValue) {
		this.rateOfEnhancementValue = rateOfEnhancementValue;
	}

}
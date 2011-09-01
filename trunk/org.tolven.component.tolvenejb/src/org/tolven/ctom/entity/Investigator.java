package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * A researcher in a clinical trial or clinical study who oversees all aspects of
 * the trial, such as concept development, protocol writing, protocol submission
 * for IRB approval, participant recruitment, informed consent, data collection,
 * analysis, interpretation and presentation.
 * @version 1.0
 * @created 27-Sep-2006 9:51:58 AM
 */
@Entity
@DiscriminatorValue("INV")
public class Investigator extends Person implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The identifier assigned in the NCI investigator registry to a physician
	 * approved for conducting a clinical trial (CTEP)
	 */
	@Column private String nciCode;

	public Investigator(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getNciCode() {
		return nciCode;
	}

	public void setNciCode(String nciCode) {
		this.nciCode = nciCode;
	}

}
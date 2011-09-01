package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * A form of diagnostic, treatment, or intervention healthcare activity
 * experienced by a subject on a study. Surgeries, Biopsies and Radiation Therapy
 * are example procedures.
 * @version 1.0
 * @created 27-Sep-2006 9:54:09 AM
 */
@Entity
@DiscriminatorValue("PROC")
public class Procedure extends Activity implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Name of site(s) within the body targeted for procedures or interventions;
	 * multiple contiguous sites within the same organ system may be referenced. 
	 */
	@Column private String targetSiteCode;

	public Procedure(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getTargetSiteCode() {
		return targetSiteCode;
	}

	public void setTargetSiteCode(String targetSiteCode) {
		this.targetSiteCode = targetSiteCode;
	}

}
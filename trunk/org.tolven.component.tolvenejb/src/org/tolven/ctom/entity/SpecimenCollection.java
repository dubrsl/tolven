package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * Specifies the routine of gathering and/or locating the sites for body samples,
 * such as, urine, blood, biopsies, etc. to be used in the conduct of a clinical
 * trial.
 * 
 * @version 1.0
 * @created 26-Sep-2006 12:45:27 PM
 */
@Entity
@DiscriminatorValue("SPEC")
public class SpecimenCollection extends Procedure implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The type of procedure or method used to collect the specimen. Values include:
	 * Abdominal/ ascites effusion; Biopsy; Bronchial alveolar lavage (BAL); Bronchial
	 * brushing; Bronchial secretions; Bronchial washing; Bucal brushing; Fine needle
	 * aspiration, specify site; Frozen Biopsy; Mediastinoscopy; Other, specify;
	 * Pericardial effusion; Pleural effusion; Pleural lavage; Sputum, induced; Sputum,
	 * spontaneous.
	 */
	@Column private String method;
	/**
	 * The type of the site where specimen/sample has been collected. Values include
	 * Normal or Abnormal.
	 */
	@Column private String siteCondition;

	public SpecimenCollection(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSiteCondition() {
		return siteCondition;
	}

	public void setSiteCondition(String siteCondition) {
		this.siteCondition = siteCondition;
	}

}
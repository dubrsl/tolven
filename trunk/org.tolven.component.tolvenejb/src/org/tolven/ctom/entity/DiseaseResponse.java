package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * A term used in the treatment of cancer to describe a positive result of
 * treatment or therapy. A "complete response" (CR) indicates the disappearance of
 * all measurable evidence of the cancer. A CR is not intended to imply a cure, as
 * recurrence may develop over time. A response is the purpose of treatment.
 * @version 1.0
 * @created 27-Sep-2006 9:58:09 AM
 */
@Entity
@DiscriminatorValue("RESP")
public class DiseaseResponse extends Assessment implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Free text field to add details and reason for a response call.
	 */
	@Column private String comments;
	/**
	 * The patient's disease state as of the last day of the observation period for
	 * the course (cycle). Values include: DU Disease Unchanged, CR Complete Response,
	 * MR Less Than Partial Response, NA Not Assessed, NE Not Evaluable, NP Not
	 * Applicable Per Protocol, NR No Response, PD Progressive Disease, PR Partial
	 * Response, SD Stable Disease, TE Too early, CRU Complete Response Unconfirmed,
	 * UK Unknown.
	 */
	@Column private String diseaseState;
	/**
	 * The best improvement achieved throughout the entire course of protocol
	 * treatment. Values include: CR Complete Response, PD Progressive Disease, PR
	 * Partial Reponse, SD Stable Disease, NA Not Assessed, UK Unknown, MR Less Than
	 * Partial Response, NP Not Applicable Per Protocol, NE Not Evaluable, TE Too
	 * Early, DU Disease Unchanged, NR No Response, CRU Complete Response Unconfirmed.
	 */
	@Column private String diseaseStateBest;
	/**
	 * Date of the evaluation used to determine the patient's maximum improvement in
	 * disease status within a period of protocol time.  
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date diseaseStateBestDate;
	/**
	 * The java.util.Date of the evaluation or procedure used to determine the patient's disease
	 * status of progressive disease.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date progressionDate;
	/**
	 * Time from the point of intervention to the point of disease progression,
	 * expressed in months.
	 */
	@Column private int progressionPeriod;

	public DiseaseResponse(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDiseaseState() {
		return diseaseState;
	}

	public void setDiseaseState(String diseaseState) {
		this.diseaseState = diseaseState;
	}

	public String getDiseaseStateBest() {
		return diseaseStateBest;
	}

	public void setDiseaseStateBest(String diseaseStateBest) {
		this.diseaseStateBest = diseaseStateBest;
	}

	public java.util.Date getDiseaseStateBestDate() {
		return diseaseStateBestDate;
	}

	public void setDiseaseStateBestDate(java.util.Date diseaseStateBestDate) {
		this.diseaseStateBestDate = diseaseStateBestDate;
	}

	public java.util.Date getProgressionDate() {
		return progressionDate;
	}

	public void setProgressionDate(java.util.Date progressionDate) {
		this.progressionDate = progressionDate;
	}

	public int getProgressionPeriod() {
		return progressionPeriod;
	}

	public void setProgressionPeriod(int progressionPeriod) {
		this.progressionPeriod = progressionPeriod;
	}

}
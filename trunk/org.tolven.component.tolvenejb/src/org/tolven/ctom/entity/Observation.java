package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * An act of monitoring, recognizing and noting measurement of some magnitude with
 * suitable instruments or established scientific processes.
 * @version 1.0
 * @created 27-Sep-2006 9:55:57 AM
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("OBS")
public class Observation implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * A code for the degree of confidentiality applicable to an observation.
	 * 
	 */
	@Column private String confidentialityCode;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The java.util.Date the observation was reported.
	 * The java.util.Date the report was created. (DCP)
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date reportingDate;
	/**
	 * A code indicating whether the observation, with its subordinate components has
	 * been asserted to be uncertain in any way. For example, a patient might have had
	 * a cholecystectomy procedure in the past (but isn't sure). 
	 */
	@Column private String uncertaintyCode;
	@ManyToOne private Assessment assessment;
	@ManyToOne private Activity activity;

	public Observation(){

	}

	public void finalize() throws Throwable {

	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Assessment getAssessment() {
		return assessment;
	}

	public void setAssessment(Assessment assessment) {
		this.assessment = assessment;
	}

	public String getConfidentialityCode() {
		return confidentialityCode;
	}

	public void setConfidentialityCode(String confidentialityCode) {
		this.confidentialityCode = confidentialityCode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.util.Date getReportingDate() {
		return reportingDate;
	}

	public void setReportingDate(java.util.Date reportingDate) {
		this.reportingDate = reportingDate;
	}

	public String getUncertaintyCode() {
		return uncertaintyCode;
	}

	public void setUncertaintyCode(String uncertaintyCode) {
		this.uncertaintyCode = uncertaintyCode;
	}

}
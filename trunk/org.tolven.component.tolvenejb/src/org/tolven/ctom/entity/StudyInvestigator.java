package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Specifies the information describing an investigator associated with a study, i.
 * e. role, association start and end java.util.Date, etc.
 * @version 1.0
 * @created 27-Sep-2006 9:50:37 AM
 */
@Entity
@Table
public class StudyInvestigator implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The java.util.Date from which an investigator's participation in this Study ends.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date endDate;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The responsibility of the Investigator on a particular Study.  e.g., Primary
	 * Investigator, Co-Investigator, etc.
	 */
	@Column private String responsibilityRole;
	/**
	 * The indicator representing that the investigator has signed the document.
	 * (DCP)
	 * 
	 * BRIDG: A code specifying whether and how the participant has attested his
	 * participation through a signature and or whether such a signature is needed. 
	 */
	@Column private int signatureCode;
	/**
	 * The signed name of the investigator who is responsible for completing a form or
	 * report for a clinical trial. (CTEP)
	 * 
	 * BRIDG: A textual or multimedia depiction of the signature by which the
	 * participant endorses his or her participation in the Act as specified in the
	 * Participation.typeCode and that he or she agrees to assume the associated
	 * accountability. 
	 */
	@Column private String signatureText;
	/**
	 * The java.util.Date from which an investigator's participation in this Study begins.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date startDate;
	/**
	 * A code specifying whether the state of participation of an investigator in the
	 * given Study is pending, active, complete, or cancelled.
	 */
	@Column private String status;
	@ManyToOne private Study study;
	@ManyToOne private Investigator investigator;

	public StudyInvestigator(){

	}

	public void finalize() throws Throwable {

	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Investigator getInvestigator() {
		return investigator;
	}

	public void setInvestigator(Investigator investigator) {
		this.investigator = investigator;
	}

	public String getResponsibilityRole() {
		return responsibilityRole;
	}

	public void setResponsibilityRole(String responsibilityRole) {
		this.responsibilityRole = responsibilityRole;
	}

	public int getSignatureCode() {
		return signatureCode;
	}

	public void setSignatureCode(int signatureCode) {
		this.signatureCode = signatureCode;
	}

	public String getSignatureText() {
		return signatureText;
	}

	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}

	public java.util.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

}
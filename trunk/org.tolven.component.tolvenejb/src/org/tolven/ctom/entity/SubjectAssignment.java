package org.tolven.ctom.entity;
import java.io.Serializable;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * Specifies the information associated with assigning a Subject to a Study.
 * Examples include: the java.util.Date a subject was on, and off, the study; study arm
 * assignment; etc.
 * @version 1.0
 * @created 27-Sep-2006 9:52:23 AM
 */
@Entity
@Table
public class SubjectAssignment implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Age at the time of study enrollment, expressed in number of years completed at
	 * the last birthday. Value is collected to two decimal points of precision to
	 * meet Clinical Trials Monitoring Service (CTMS) reporting requirements.
	 */
	@Column private int ageAtEnrollment;
	/**
	 * Protocol-specific arm assignment identified in a formal communication.
	 * NOTE:  When the epochName is "Prior" or "Baseline" -- the arm value will be
	 * defaulted to NULL.
	 */
	@Column private String arm;
	/**
	 * The reason that a subject is given a sponsor-approved waiver for meeting
	 * protocol-defined eligibility requirements.  
	 */
	@Column private String eligibilityWaiverReason;
	/**
	 * Work in Progress: BRIDG- a time interval in the planned conduct of a study.
	 * Values include: Baseline, Screening, Run-in, Treatment, Follow-Up, etc.
	 * 
	 * Note:  When pre-study or medical history information is collected -- the epoch
	 * would be "Pre-Study";  relevant attributes in Activity, Observation and
	 * Assessment will be defaulted accordingly. 
	 */
	@Column private String epochName;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The java.util.Date the patient acknowledged participation on the protocol by signing the
	 * informed consent document.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date informedConsentFormSignedDate;
	/**
	 * The java.util.Date when the patient is removed from the protocol, i.e., is not being
	 * followed and will not be retreated.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date offStudyDate;
	/**
	 * Dose Level of an agent or medication, assigned to a protocol
	 * patient/participant.
	 */
	@Column private String studyAgentDoseLevel;
	/**
	 * The unique number assigned to identify a patient on a protocol.
	 */
	@Column private int studySubjectIdentifier;
	/**
	 * A unique code for identification of uniform groups of patients for separate
	 * analysis or treatment is defined in the Clinical Data Upjava.util.Date System (CDUS)
	 * Version 2.0. Use the codes submitted to CTEP on the Protocol Submission
	 * Checklist.
	 */
	@Column private String subgroupCode;

	@OneToOne( cascade=CascadeType.ALL)  
	private Subject subject;

	@ManyToOne 
	private StudySite studySite;

	public SubjectAssignment(){

	}

	public void finalize() throws Throwable {

	}

	public int getAgeAtEnrollment() {
		return ageAtEnrollment;
	}

	public void setAgeAtEnrollment(int ageAtEnrollment) {
		this.ageAtEnrollment = ageAtEnrollment;
	}

	public String getArm() {
		return arm;
	}

	public void setArm(String arm) {
		this.arm = arm;
	}

	public String getEligibilityWaiverReason() {
		return eligibilityWaiverReason;
	}

	public void setEligibilityWaiverReason(String eligibilityWaiverReason) {
		this.eligibilityWaiverReason = eligibilityWaiverReason;
	}

	public String getEpochName() {
		return epochName;
	}

	public void setEpochName(String epochName) {
		this.epochName = epochName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.util.Date getInformedConsentFormSignedDate() {
		return informedConsentFormSignedDate;
	}

	public void setInformedConsentFormSignedDate(
			java.util.Date informedConsentFormSignedDate) {
		this.informedConsentFormSignedDate = informedConsentFormSignedDate;
	}

	public java.util.Date getOffStudyDate() {
		return offStudyDate;
	}

	public void setOffStudyDate(java.util.Date offStudyDate) {
		this.offStudyDate = offStudyDate;
	}

	public String getStudyAgentDoseLevel() {
		return studyAgentDoseLevel;
	}

	public void setStudyAgentDoseLevel(String studyAgentDoseLevel) {
		this.studyAgentDoseLevel = studyAgentDoseLevel;
	}

	public StudySite getStudySite() {
		return studySite;
	}

	public void setStudySite(StudySite studySite) {
		this.studySite = studySite;
	}

	public int getStudySubjectIdentifier() {
		return studySubjectIdentifier;
	}

	public void setStudySubjectIdentifier(int studySubjectIdentifier) {
		this.studySubjectIdentifier = studySubjectIdentifier;
	}

	public String getSubgroupCode() {
		return subgroupCode;
	}

	public void setSubgroupCode(String subgroupCode) {
		this.subgroupCode = subgroupCode;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

}
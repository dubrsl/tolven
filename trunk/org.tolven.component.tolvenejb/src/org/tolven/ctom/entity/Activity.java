package org.tolven.ctom.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * An activity/event consists of performing a healthcare procedure on, or
 * administering treatment to, a subject on a study.
 * 
 * @version 1.0
 * @created 27-Sep-2006 9:55:02 AM
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("ACT")
public class Activity implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Free format description of the protocol tracking activity worthy of
	 * documentation.
	 */
	@Column private String description;
	/**
	 * Unit of Measure to express the length of time of an event or activity. Values
	 * include: MN - Minutes; HR - Hours; DY - Days; WK - Weeks;  CO - Course; MO -
	 * Month.
	 */
	@Column private String durationUnitOfMeasure;
	/**
	 * Actual value that is paired with a unit of measure (hours, weeks, days) to
	 * express the length of time that an activity or event lasted.  
	 */
	@Column private int durationValue;
	/**
	 * The system generated unique identifier.
	 */
    @Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * Names assigned to health care procedures done for diagnostic, surveillance,
	 * treatment, palliation, or study-directed purposes. Values include: Bone Scan;
	 * Colonoscopy; CAT Scan; Biopsy; Flow Cytometry, Blood; Flow Cytometry, Bone
	 * Marrow; MRI; X-ray, Chest; Physical Examination, Positron Emission Tomography;
	 * MUGA Scan; Transrectal Ultrasound; Ultrasound; Flow cytometry.
	 */
	@Column private String name;
	/**
	 * An indicator to specify whether the activity/event was planned, according to
	 * the study, or unplanned.
	 */
	@Column private boolean plannnedUnplannedInd;
	/**
	 * Text reason that a procedure is performed on an individual. Values include:
	 * Diagnostic, Research, Treatment.
	 * 
	 * Captures the specific reason for the event.
	 * 
	 */
	@Column private String reason;
	/**
	 * The begin java.util.Date of a measure or agent.
	 */
    @Temporal(TemporalType.TIMESTAMP) @Column private Date startDate;
	/**
	 * The end java.util.Date of a measure or agent.
	 */
    @Temporal(TemporalType.TIMESTAMP) private Date stopDate;
	@ManyToOne private SubjectAssignment subjectAssignment;

	public Activity(){

	}

	public void finalize() throws Throwable {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDurationUnitOfMeasure() {
		return durationUnitOfMeasure;
	}

	public void setDurationUnitOfMeasure(String durationUnitOfMeasure) {
		this.durationUnitOfMeasure = durationUnitOfMeasure;
	}

	public int getDurationValue() {
		return durationValue;
	}

	public void setDurationValue(int durationValue) {
		this.durationValue = durationValue;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPlannnedUnplannedInd() {
		return plannnedUnplannedInd;
	}

	public void setPlannnedUnplannedInd(boolean plannnedUnplannedInd) {
		this.plannnedUnplannedInd = plannnedUnplannedInd;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public SubjectAssignment getSubjectAssignment() {
		return subjectAssignment;
	}

	public void setSubjectAssignment(SubjectAssignment subjectAssignment) {
		this.subjectAssignment = subjectAssignment;
	}

}
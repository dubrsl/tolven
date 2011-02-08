package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Female specific characteristics that may be significant as a female subject
 * participates in a clinical trial. Examples include pregnancy and menopause.
 * @version 1.0
 * @created 27-Sep-2006 9:52:44 AM
 */
@Entity
@Table
public class FemaleReproductiveCharacteristic implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Indication (Yes or No) of a woman's incidence of induced abortion in her
	 * pregnancy history.
	 */
	@Column private boolean abortionInd;
	/**
	 * Age at the time of first live birth expressed in number of years.
	 */
	@Column private int firstLiveBirthAge;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * Count of the number of successful pregnancies that resulted in the birth of at
	 * least one child.
	 */
	@Column private int liveBirthCount;
	/**
	 * Age at the time of menopause expressed in number of years since birth.
	 */
	@Column private int menopauseAge;
	/**
	 * Explanation of why menstrual periods ceased. Values include:  Other (specify),
	 * Natural Menopause, Surgery Stopped Period, Medication Stopped Period, Radiation
	 * Stopped Period.
	 */
	@Column private String menopauseReason;
	/**
	 * Date when menopausal status is confirmed to begin.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date menopauseStartDate;
	/**
	 * Incidence of stillbirth as part of a woman's pregnancy history.
	 */
	@Column private int stillBirthCount;
	@ManyToOne private Subject subject;

	public FemaleReproductiveCharacteristic(){

	}

	public void finalize() throws Throwable {

	}

	public boolean isAbortionInd() {
		return abortionInd;
	}

	public void setAbortionInd(boolean abortionInd) {
		this.abortionInd = abortionInd;
	}

	public int getFirstLiveBirthAge() {
		return firstLiveBirthAge;
	}

	public void setFirstLiveBirthAge(int firstLiveBirthAge) {
		this.firstLiveBirthAge = firstLiveBirthAge;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLiveBirthCount() {
		return liveBirthCount;
	}

	public void setLiveBirthCount(int liveBirthCount) {
		this.liveBirthCount = liveBirthCount;
	}

	public int getMenopauseAge() {
		return menopauseAge;
	}

	public void setMenopauseAge(int menopauseAge) {
		this.menopauseAge = menopauseAge;
	}

	public String getMenopauseReason() {
		return menopauseReason;
	}

	public void setMenopauseReason(String menopauseReason) {
		this.menopauseReason = menopauseReason;
	}

	public java.util.Date getMenopauseStartDate() {
		return menopauseStartDate;
	}

	public void setMenopauseStartDate(java.util.Date menopauseStartDate) {
		this.menopauseStartDate = menopauseStartDate;
	}

	public int getStillBirthCount() {
		return stillBirthCount;
	}

	public void setStillBirthCount(int stillBirthCount) {
		this.stillBirthCount = stillBirthCount;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

}
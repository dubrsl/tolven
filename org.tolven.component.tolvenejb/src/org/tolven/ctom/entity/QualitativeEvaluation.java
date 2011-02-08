package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * A systemic and thorough assessment of the body and its functions using visual
 * inspection, palpation, percussion, and auscultation, aimed to determine the
 * presence or absence of signs of disease or abnormality.
 * @version 1.0
 * @created 27-Sep-2006 9:58:33 AM
 */
@Entity
@DiscriminatorValue("QUAL")
public class QualitativeEvaluation extends Assessment implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Percentage of accuracy of a result using the ANAM, a psychometric measurement
	 * of memory and reaction time.
	 */
	@Column private int anamResultAccuracyInPercentage;
	/**
	 * Code for the reason patient died relative specifically to the disease/condition
	 * under investigation. Values include: I - Infection; M - Malignant Disease; O-
	 * Other; T - Toxicity From Protocol Treatment.
	 */
	@Column private String deathCause;
	/**
	 * Descriptive text for clarification with the cause of death is 'Other'.
	 * 
	 * 
	 */
	@Column private String deathCauseText;
	/**
	 * The actual java.util.Date of a patient's death.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date deathDate;
	/**
	 * Indicator of Yes or No to indicate if a woman has had a menstrual period in the
	 * past 12 months. Values include: Yes, No.
	 */
	@Column private boolean menstrualInd;
	/**
	 * Evaluation of the regularity of an individual's menstrual periods. Values
	 * include: Always Regular, Never Regular, Usually Regular.
	 */
	@Column private String menstrualPatternType;
	/**
	 * Numeric score (0 to 10) denoting level of pain.
	 */
	@Column private int painIndexScore;
	/**
	 * Score from the Karnofsky Performance status scale, representing the functional
	 * capabilities of a person. Values include: 100 Normal; 90 Able to carry on
	 * normal activity; 80 Normal activity with effort; 70 Cares for self;
	 * 60 Requires occasional assistance; 50 Requires considerable assistance; 40
	 * Disabled; 30 Severely disabled; 20 Very sick - hospital admission necessary, 10
	 * Moribund; 0 DEAD.
	 */
	@Column private int performanceStatusScore;
	/**
	 * Status of a person's survival and disease at a particular point in time. Values
	 * include: 3	 Alive Disease Status Unknown; 1 Alive With Disease; 2 Alive With No
	 * Evidence Of Disease; 5 Died; 4 Unknown.
	 */
	@Column private int survivalStatus;
	/**
	 * Explanation of a survival status of 'Other'.  
	 */
	@Column private String survivalStatusDescription;

	public QualitativeEvaluation(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public int getAnamResultAccuracyInPercentage() {
		return anamResultAccuracyInPercentage;
	}

	public void setAnamResultAccuracyInPercentage(int anamResultAccuracyInPercentage) {
		this.anamResultAccuracyInPercentage = anamResultAccuracyInPercentage;
	}

	public String getDeathCause() {
		return deathCause;
	}

	public void setDeathCause(String deathCause) {
		this.deathCause = deathCause;
	}

	public String getDeathCauseText() {
		return deathCauseText;
	}

	public void setDeathCauseText(String deathCauseText) {
		this.deathCauseText = deathCauseText;
	}

	public java.util.Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(java.util.Date deathDate) {
		this.deathDate = deathDate;
	}

	public boolean isMenstrualInd() {
		return menstrualInd;
	}

	public void setMenstrualInd(boolean menstrualInd) {
		this.menstrualInd = menstrualInd;
	}

	public String getMenstrualPatternType() {
		return menstrualPatternType;
	}

	public void setMenstrualPatternType(String menstrualPatternType) {
		this.menstrualPatternType = menstrualPatternType;
	}

	public int getPainIndexScore() {
		return painIndexScore;
	}

	public void setPainIndexScore(int painIndexScore) {
		this.painIndexScore = painIndexScore;
	}

	public int getPerformanceStatusScore() {
		return performanceStatusScore;
	}

	public void setPerformanceStatusScore(int performanceStatusScore) {
		this.performanceStatusScore = performanceStatusScore;
	}

	public int getSurvivalStatus() {
		return survivalStatus;
	}

	public void setSurvivalStatus(int survivalStatus) {
		this.survivalStatus = survivalStatus;
	}

	public String getSurvivalStatusDescription() {
		return survivalStatusDescription;
	}

	public void setSurvivalStatusDescription(String survivalStatusDescription) {
		this.survivalStatusDescription = survivalStatusDescription;
	}

}
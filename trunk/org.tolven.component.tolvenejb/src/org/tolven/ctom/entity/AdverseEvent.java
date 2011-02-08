package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * An unfavorable and unintended reaction, symptom, syndrome, or disease
 * encountered by a subject while on a clinical trial regardless of whether or not
 * it is considered related to the product or procedure. The concept refers to
 * assessments that could be medically related, dose related, route related,
 * patient related, caused by an interaction with another therapy or procedure, or
 * dose escalation.
 * @version 1.0
 * @created 27-Sep-2006 9:58:23 AM
 */
@Entity
@DiscriminatorValue("ADV")
public class AdverseEvent extends Assessment implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Action taken in response to an adverse event. Values include: 2 - Dose Reduced;
	 * 1 - None; 3 - Regimen Interrupted; 4 - TherapyDiscontinued/Interrupted/ Reduced;
	 * 5 - Interrupted/Reduced.
	 */
	@Column private String actionTaken;
	/**
	 * Time pattern by which Adverse Event occurs. Values include: 1 single episode, 2
	 * intermittent, 3 continuous.
	 */
	@Column private String conditionPattern;
	/**
	 * A scale of values to indicate the causal relation between the treatment
	 * modality and the specific adverse event. Values include: Definite; Possible;
	 * Probable; Unlikely; Unrelated	.
	 */
	@Column private String ctcAttribution;
	/**
	 * The high level classification in CTC containing the adverse event term.  Values
	 * include: Constitutional Symptoms; Pulmonary/Upper Respiratory; Dermatology/Skin;
	 * Gastrointestinal; Pain; Infection; Allergy/Immunology; Auditory/Ear; Blood/Bone
	 * Marrow; Cardiac Arrhythmia; Cardiac General; Coagulation; Endocrine;
	 * Hemorrhage/Bleeding; Hepatobiliary/Pancreas; Lymphatics; Metabolic/ Laboratory;
	 * Musculoskeletal/Soft Tissue; Neurology; Ocular/Visual; Renal/Genitourinary;
	 * Secondary Malignancy; Sexual/Reproductive Function; Syndromes; Death; Growth
	 * and Development; Surgery/Intra-operative Injury; Vascular.
	 */
	@Column private String ctcCategory;
	/**
	 * Adverse Event numeric grade (0-5) based on the NCI Common Toxicity Criteria.
	 * Values include:
	 * 0	No Adverse Event Or Within Normal Limits
	 * 1	Mild Adverse Event
	 * 2	Moderate Adverse Event
	 * 3	Severe and Undesirable Adverse Event
	 * 4	Life-Threatening Or Disabling Adverse Event
	 * 5	Death Related To Adverse Event
	 */
	@Column private String ctcGrade;
	/**
	 * The medical description of the adverse event using NCI CTC terminology.
	 * Includes 1059 values.
	 */
	@Column private String ctcTermType;
	/**
	 * The clinical description of the adverse event.
	 */
	@Column private String description;
	/**
	 * Description of dose limiting toxicities for a drug or agent.
	 */
	@Column private String doseLimitingToxicityDescription;
	/**
	 * Indication (Y or N) of an Adverse event considered to be a dose-limiting
	 * toxicity.
	 */
	@Column private boolean doseLimitingToxicityInd;
	/**
	 * The java.util.Date when a symptom or Adverse Event (AE) initially occurred.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date onsetDate;
	/**
	 * Final status of the patient related to an adverse event.  Values include: 1 -
	 * Recovered; 2 Still Under Treatment/Observation; 3 Alive With Sequelae; 4 Died.
	 */
	@Column private String outcome;
	/**
	 * The java.util.Date when an Adverse Event (AE) is resolved or returns to baseline value,
	 * based on standardized criteria (CTCAE).
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date resolvedDate;
	/**
	 * Reason that an Adverse Event is evaluated to be serious. Values include: 1 - No;
	 * 2 - Life-Threatening; 3 - Death; 4 - Disability; 5 - Hospitalized; 6 -
	 * Congenital Anomaly; 7 - Jeopardizes Patient/Requires Intervention.
	 */
	@Column private String seriousReason;

	public AdverseEvent(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getConditionPattern() {
		return conditionPattern;
	}

	public void setConditionPattern(String conditionPattern) {
		this.conditionPattern = conditionPattern;
	}

	public String getCtcAttribution() {
		return ctcAttribution;
	}

	public void setCtcAttribution(String ctcAttribution) {
		this.ctcAttribution = ctcAttribution;
	}

	public String getCtcCategory() {
		return ctcCategory;
	}

	public void setCtcCategory(String ctcCategory) {
		this.ctcCategory = ctcCategory;
	}

	public String getCtcGrade() {
		return ctcGrade;
	}

	public void setCtcGrade(String ctcGrade) {
		this.ctcGrade = ctcGrade;
	}

	public String getCtcTermType() {
		return ctcTermType;
	}

	public void setCtcTermType(String ctcTermType) {
		this.ctcTermType = ctcTermType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDoseLimitingToxicityDescription() {
		return doseLimitingToxicityDescription;
	}

	public void setDoseLimitingToxicityDescription(
			String doseLimitingToxicityDescription) {
		this.doseLimitingToxicityDescription = doseLimitingToxicityDescription;
	}

	public boolean isDoseLimitingToxicityInd() {
		return doseLimitingToxicityInd;
	}

	public void setDoseLimitingToxicityInd(boolean doseLimitingToxicityInd) {
		this.doseLimitingToxicityInd = doseLimitingToxicityInd;
	}

	public java.util.Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(java.util.Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public java.util.Date getResolvedDate() {
		return resolvedDate;
	}

	public void setResolvedDate(java.util.Date resolvedDate) {
		this.resolvedDate = resolvedDate;
	}

	public String getSeriousReason() {
		return seriousReason;
	}

	public void setSeriousReason(String seriousReason) {
		this.seriousReason = seriousReason;
	}

}
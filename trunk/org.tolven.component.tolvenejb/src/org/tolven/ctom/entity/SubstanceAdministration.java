package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * The description of applying, dispensing or giving agents or medications to
 * subjects.
 * @version 1.0
 * @created 27-Sep-2006 9:52:32 AM
 */
@Entity
@DiscriminatorValue("SBADM")
public class SubstanceAdministration extends Activity implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Value to represent a change in the plan for treatment dosage.  The change may
	 * be known or unknown, as well as planned or unplanned. Values include: 9 Unknown,
	 * 3 No, 1 Yes Planned, 2 Yes Unplanned.
	 */
	@Column private int doseChangeType;
	/**
	 * The number of regular recurrences in a given time. Values include: Daily,
	 * Weekly, Monthly, Yearly, Never, Unknown, Some days (1-2 DAYS), Refused to
	 * answer the question.
	 */
	@Column private String doseFrequency;
	/**
	 * A description of the modification of the dose. Values include: Agent Added,
	 * Agent Dose Decreased, Agent Dose Increased, Agent Dropped, Course Added, Course
	 * Decreased, Course Dropped, Course Increased, Cycle/Rotation Added,
	 * Cycle/Rotation Decreased, Cycle/Rotation Dropped, Cycle/Rotation Increased,
	 * Regimen Interrupted, Therapy Discontinued.
	 */
	@Column private String doseModificationType;
	/**
	 * The total dose of study drug given to the patient in the time period
	 * encompassed by the duration.
	 */
	@Column private int doseQuantity;
	/**
	 * Unit of measurement (UOM) used to express the amount of agent used in dosing. 
	 */
	@Column private String doseUnitOfMeasure;
	/**
	 * Name of an access route for administration of agents, evaluation of vital signs,
	 * etc. Values include:  Gastrostomy Tube, CIV Continuous Intravenous Infusion, IA
	 * Intra-Arterial, ID Intradermal, IH  Intrahepatic, IHI  Intrahepatic Infusion,
	 * IM  Intramuscular, Inhalatn  Inhalation, IP  Intraperitoneal, IT  Intrathecal,
	 * IV  Intravenous Bolus, IVI  Intravenous Fusion, NASAL, NG  Nasogastric, Oph
	 * Each  Ophthalmic, Each Eye ;  Oph Left  Ophthalmic, Left Eye; Oph Rt
	 * Ophthalmic, Right Eye; PO  Oral, PR  Rectal, RT  Radiation, SC  Subcutaneous,
	 * SWSP  Swish & Spit, SWSW  Swish & Swallow, TOP  Topical, INTUM  Intratumoral. 
	 */
	@Column private String route;
	@ManyToOne private Agent agent;
	@ManyToOne private StudyAgent studyAgent;

	public SubstanceAdministration(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public int getDoseChangeType() {
		return doseChangeType;
	}

	public void setDoseChangeType(int doseChangeType) {
		this.doseChangeType = doseChangeType;
	}

	public String getDoseFrequency() {
		return doseFrequency;
	}

	public void setDoseFrequency(String doseFrequency) {
		this.doseFrequency = doseFrequency;
	}

	public String getDoseModificationType() {
		return doseModificationType;
	}

	public void setDoseModificationType(String doseModificationType) {
		this.doseModificationType = doseModificationType;
	}

	public int getDoseQuantity() {
		return doseQuantity;
	}

	public void setDoseQuantity(int doseQuantity) {
		this.doseQuantity = doseQuantity;
	}

	public String getDoseUnitOfMeasure() {
		return doseUnitOfMeasure;
	}

	public void setDoseUnitOfMeasure(String doseUnitOfMeasure) {
		this.doseUnitOfMeasure = doseUnitOfMeasure;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public StudyAgent getStudyAgent() {
		return studyAgent;
	}

	public void setStudyAgent(StudyAgent studyAgent) {
		this.studyAgent = studyAgent;
	}

}
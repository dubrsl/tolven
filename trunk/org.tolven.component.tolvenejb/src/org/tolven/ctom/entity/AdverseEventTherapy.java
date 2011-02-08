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
 * Therapy required to treat an adverse event.
 * @version 1.0
 * @created 27-Sep-2006 9:58:40 AM
 */
@Entity
@Table
public class AdverseEventTherapy implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The duration of delay of the adverse event treatment.
	 */
	@Column private String delayDuration;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The intensity an adverse event has been treated with. Values include: None,
	 * Symptomatic, Supportive, Vigorous , Supportive.
	 */
	@Column private String intensity;
	/**
	 * The java.util.Date when adverse event was treated.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date treatmentDate;
	@ManyToOne private AdverseEvent adverseEvent;

	public AdverseEventTherapy(){

	}

	public void finalize() throws Throwable {

	}

	public AdverseEvent getAdverseEvent() {
		return adverseEvent;
	}

	public void setAdverseEvent(AdverseEvent adverseEvent) {
		this.adverseEvent = adverseEvent;
	}

	public String getDelayDuration() {
		return delayDuration;
	}

	public void setDelayDuration(String delayDuration) {
		this.delayDuration = delayDuration;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIntensity() {
		return intensity;
	}

	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}

	public java.util.Date getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(java.util.Date treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

}
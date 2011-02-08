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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;


/**
 * The subjective and objective interpretation or evaluation of raw clinical data
 * captured via observations.
 * 
 * @version 1.0
 * @created 27-Sep-2006 9:58:16 AM
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("ASMT")
public class Assessment implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The java.util.Date on which the evaluation was recorded. 
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date evaluationDate;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	@ManyToOne private  Activity activity;

	public Assessment(){

	}

	public void finalize() throws Throwable {

	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public java.util.Date getEvaluationDate() {
		return evaluationDate;
	}

	public void setEvaluationDate(java.util.Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
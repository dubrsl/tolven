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
 * A written notification to a reporting authority, i.e. FDA, that a subject has
 * experienced an adverse event.
 * @version 1.0
 * @created 27-Sep-2006 9:58:48 AM
 */
@Entity
@Table
public class AdverseEventReport implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * Date the AER was submitted to FDA or other regulatory body.
	 * CTEP: a java.util.Date on which printed or electronic documents were sent to an
	 * authorized institution. 
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date submissionDate;
	@ManyToOne private AdverseEvent adverseEvent;

	public AdverseEventReport(){

	}

	public void finalize() throws Throwable {

	}

	public AdverseEvent getAdverseEvent() {
		return adverseEvent;
	}

	public void setAdverseEvent(AdverseEvent adverseEvent) {
		this.adverseEvent = adverseEvent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.util.Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(java.util.Date submissionDate) {
		this.submissionDate = submissionDate;
	}

}
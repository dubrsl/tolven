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
 * The principal activity that a person performs to earn a living.
 * @version 1.0
 * @created 27-Sep-2006 9:52:04 AM
 */
@Entity
@Table
public class PersonOccupation implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The primary occupation of the participant for most of their adult employment.
	 * www.osha.gov/cgi-bin/sic/sicser5  This is paired with Occupation Primary  -
	 * Industry.  4 Digit SIC Codes.
	 */
	@Column private String primaryType;
	/**
	 * Date this type of occupation started.  This is a formatted java.util.Date field.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date startDate;
	/**
	 * Date this type of occupation stopped.  This is a formatted java.util.Date field.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date stopDate;
	@ManyToOne private Person person;

	public PersonOccupation(){

	}

	public void finalize() throws Throwable {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getPrimaryType() {
		return primaryType;
	}

	public void setPrimaryType(String primaryType) {
		this.primaryType = primaryType;
	}

	public java.util.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	public java.util.Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(java.util.Date stopDate) {
		this.stopDate = stopDate;
	}

}
package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * The semantic link between observations.
 * @version 1.0
 * @created 27-Sep-2006 9:55:38 AM
 */
@Entity
@Table
public class ObservationRelationship implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * A description of the relationship between two observations.
	 */
	@Column private String comments;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	@ManyToOne private Observation observation;

	public ObservationRelationship(){

	}

	public void finalize() throws Throwable {

	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

}
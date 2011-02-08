package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * A chemical or biological substance with specific characteristics used in a
 * study for treatment or prevention of cancer or another disease as specified by
 * the study.
 * @version 1.0
 * @created 27-Sep-2006 9:51:06 AM
 */
@Entity
@Table
public class Agent implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The text that describes the formulation of the agent used in the study (DCP).
	 */
	@Column private String description;
	/**
	 * The code that represents the formulation of the agent used in the study (DCP)
	 */
	@Column private String formCode;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The name of the agent used in the treatment regimen following CTMS requirements
	 * for name formatting.  
	 */
	@Column private String name;
	/**
	 * A value representing whether the information associated with the Entity is
	 * currently active or inactive for the purpose of Participation in Acts. [RIM]
	 * (BRIDG)
	 */
	@Column private String status;

	public Agent(){

	}

	public void finalize() throws Throwable {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
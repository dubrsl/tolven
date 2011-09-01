package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * A pharmaceutical product, vitamin, mineral, food supplement or a combination
 * that are being used or tested as part of a clinical trial.
 * @version 1.0
 * @created 27-Sep-2006 9:51:29 AM
 */
@Entity
@Table
public class StudyAgent implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * A yes/no value indicating if the agent is an Investigational New Drug (IND)
	 */
	@Column private boolean investigationalInd;
	@ManyToOne private Agent agent;
	@ManyToOne private Study study;

	public StudyAgent(){

	}

	public void finalize() throws Throwable {

	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isInvestigationalInd() {
		return investigationalInd;
	}

	public void setInvestigationalInd(boolean investigationalInd) {
		this.investigationalInd = investigationalInd;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

}
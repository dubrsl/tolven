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
 * The various name(s) attributed to a chemical or biological substance used as
 * part of a study for the treatment or prevention of disease.
 * @version 1.0
 * @created 27-Sep-2006 9:50:56 AM
 */
@Entity
@Table
public class AgentSynonym implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The alternate name for the agent.
	 */
	@Column private String name;
	@ManyToOne private Agent agent;

	public AgentSynonym(){

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
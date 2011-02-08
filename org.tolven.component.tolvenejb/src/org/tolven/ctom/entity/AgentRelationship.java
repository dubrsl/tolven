package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Association between compound agent and component agents.
 * @version 1.0
 * @created 27-Sep-2006 9:51:18 AM
 */
@Entity
@Table
public class AgentRelationship implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	@ManyToOne private Agent agent;

	public AgentRelationship(){

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

}
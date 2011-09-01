package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * A formalized group of persons or other organizations collected together for a
 * common purpose (such as administrative, legal, political) and the
 * infrastructure to carry out that purpose.
 * @version 1.0
 * @created 27-Sep-2006 9:51:36 AM
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("ORG")
public class Organization implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Text describing the organization
	 */
	@Column private String description;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * Name of the organization or an institution conducting the trial. (DCP)
	 */
	@Column private String name;

	public Organization(){

	}

	public void finalize() throws Throwable {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
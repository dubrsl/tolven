package org.tolven.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dosage_form" , schema="app")
public class DosageForm implements Serializable{

	private static final long serialVersionUID = -9027924570428154963L;
	@Id
	private long id;
	@Column
	private String definition;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
}

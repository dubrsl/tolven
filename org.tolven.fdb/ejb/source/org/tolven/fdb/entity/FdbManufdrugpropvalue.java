package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_manufdrugpropvalue database table.
 * 
 */
@Entity
@Table(name="fdb_manufdrugpropvalue")
public class FdbManufdrugpropvalue implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbManufdrugpropvaluePK id;

	private String propertyvalue;

    public FdbManufdrugpropvalue() {
    }

	public FdbManufdrugpropvaluePK getId() {
		return this.id;
	}

	public void setId(FdbManufdrugpropvaluePK id) {
		this.id = id;
	}
	
	public String getPropertyvalue() {
		return this.propertyvalue;
	}

	public void setPropertyvalue(String propertyvalue) {
		this.propertyvalue = propertyvalue;
	}

}
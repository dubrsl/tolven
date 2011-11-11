package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_regpackagedpropvalue database table.
 * 
 */
@Entity
@Table(name="fdb_regpackagedpropvalue")
public class FdbRegpackagedpropvalue implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRegpackagedpropvaluePK id;

	private String propertyvalue;

    public FdbRegpackagedpropvalue() {
    }

	public FdbRegpackagedpropvaluePK getId() {
		return this.id;
	}

	public void setId(FdbRegpackagedpropvaluePK id) {
		this.id = id;
	}
	
	public String getPropertyvalue() {
		return this.propertyvalue;
	}

	public void setPropertyvalue(String propertyvalue) {
		this.propertyvalue = propertyvalue;
	}

}
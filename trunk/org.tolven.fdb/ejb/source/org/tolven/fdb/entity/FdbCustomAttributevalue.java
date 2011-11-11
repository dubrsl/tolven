package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_attributevalues database table.
 * 
 */
@Entity
@Table(name="fdb_custom_attributevalues")
public class FdbCustomAttributevalue implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomAttributevaluePK id;

	private String attributevalue;

	private Integer conceptidnumeric;

    public FdbCustomAttributevalue() {
    }

	public FdbCustomAttributevaluePK getId() {
		return this.id;
	}

	public void setId(FdbCustomAttributevaluePK id) {
		this.id = id;
	}
	
	public String getAttributevalue() {
		return this.attributevalue;
	}

	public void setAttributevalue(String attributevalue) {
		this.attributevalue = attributevalue;
	}

	public Integer getConceptidnumeric() {
		return this.conceptidnumeric;
	}

	public void setConceptidnumeric(Integer conceptidnumeric) {
		this.conceptidnumeric = conceptidnumeric;
	}

}
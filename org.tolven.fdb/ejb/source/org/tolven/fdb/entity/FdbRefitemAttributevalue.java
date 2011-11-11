package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_refitem_attributevalues database table.
 * 
 */
@Entity
@Table(name="fdb_refitem_attributevalues")
public class FdbRefitemAttributevalue implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRefitemAttributevaluePK id;

	private String attributevalue;

    public FdbRefitemAttributevalue() {
    }

	public FdbRefitemAttributevaluePK getId() {
		return this.id;
	}

	public void setId(FdbRefitemAttributevaluePK id) {
		this.id = id;
	}
	
	public String getAttributevalue() {
		return this.attributevalue;
	}

	public void setAttributevalue(String attributevalue) {
		this.attributevalue = attributevalue;
	}

}
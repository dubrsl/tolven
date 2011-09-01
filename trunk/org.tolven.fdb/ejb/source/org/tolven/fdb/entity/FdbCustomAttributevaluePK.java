package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_attributevalues database table.
 * 
 */
@Embeddable
public class FdbCustomAttributevaluePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer concepttype;

	private String conceptidstring;

	private String attributeid;

    public FdbCustomAttributevaluePK() {
    }
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public String getConceptidstring() {
		return this.conceptidstring;
	}
	public void setConceptidstring(String conceptidstring) {
		this.conceptidstring = conceptidstring;
	}
	public String getAttributeid() {
		return this.attributeid;
	}
	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCustomAttributevaluePK)) {
			return false;
		}
		FdbCustomAttributevaluePK castOther = (FdbCustomAttributevaluePK)other;
		return 
			this.concepttype.equals(castOther.concepttype)
			&& this.conceptidstring.equals(castOther.conceptidstring)
			&& this.attributeid.equals(castOther.attributeid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptidstring.hashCode();
		hash = hash * prime + this.attributeid.hashCode();
		
		return hash;
    }
}
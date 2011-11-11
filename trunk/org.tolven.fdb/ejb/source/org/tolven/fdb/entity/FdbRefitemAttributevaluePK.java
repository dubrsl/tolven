package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_refitem_attributevalues database table.
 * 
 */
@Embeddable
public class FdbRefitemAttributevaluePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer refitemid;

	private String attributeid;

    public FdbRefitemAttributevaluePK() {
    }
	public Integer getRefitemid() {
		return this.refitemid;
	}
	public void setRefitemid(Integer refitemid) {
		this.refitemid = refitemid;
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
		if (!(other instanceof FdbRefitemAttributevaluePK)) {
			return false;
		}
		FdbRefitemAttributevaluePK castOther = (FdbRefitemAttributevaluePK)other;
		return 
			this.refitemid.equals(castOther.refitemid)
			&& this.attributeid.equals(castOther.attributeid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.refitemid.hashCode();
		hash = hash * prime + this.attributeid.hashCode();
		
		return hash;
    }
}
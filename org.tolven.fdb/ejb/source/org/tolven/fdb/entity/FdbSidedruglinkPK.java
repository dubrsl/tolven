package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_sidedruglink database table.
 * 
 */
@Embeddable
public class FdbSidedruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer sideeffid;

    public FdbSidedruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
	}
	public Integer getSideeffid() {
		return this.sideeffid;
	}
	public void setSideeffid(Integer sideeffid) {
		this.sideeffid = sideeffid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbSidedruglinkPK)) {
			return false;
		}
		FdbSidedruglinkPK castOther = (FdbSidedruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.sideeffid.equals(castOther.sideeffid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.sideeffid.hashCode();
		
		return hash;
    }
}
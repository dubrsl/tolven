package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_inddruglink database table.
 * 
 */
@Embeddable
public class FdbInddruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer indid;

    public FdbInddruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
	}
	public Integer getIndid() {
		return this.indid;
	}
	public void setIndid(Integer indid) {
		this.indid = indid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbInddruglinkPK)) {
			return false;
		}
		FdbInddruglinkPK castOther = (FdbInddruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.indid.equals(castOther.indid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.indid.hashCode();
		
		return hash;
    }
}
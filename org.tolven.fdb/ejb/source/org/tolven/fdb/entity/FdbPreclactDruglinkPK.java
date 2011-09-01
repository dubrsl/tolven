package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_preclact_druglink database table.
 * 
 */
@Embeddable
public class FdbPreclactDruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer lactcode;

    public FdbPreclactDruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
	}
	public Integer getLactcode() {
		return this.lactcode;
	}
	public void setLactcode(Integer lactcode) {
		this.lactcode = lactcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPreclactDruglinkPK)) {
			return false;
		}
		FdbPreclactDruglinkPK castOther = (FdbPreclactDruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.lactcode.equals(castOther.lactcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.lactcode.hashCode();
		
		return hash;
    }
}
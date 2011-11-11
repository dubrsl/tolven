package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_drug_rtgenid database table.
 * 
 */
@Embeddable
public class FdbDrugRtgenidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer rtgenid;

    public FdbDrugRtgenidPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
	}
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDrugRtgenidPK)) {
			return false;
		}
		FdbDrugRtgenidPK castOther = (FdbDrugRtgenidPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.rtgenid.equals(castOther.rtgenid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.rtgenid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_drugvalidation database table.
 * 
 */
@Embeddable
public class FdbDrugvalidationPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer rtgenid;

	private Integer rtdfgenid;

    public FdbDrugvalidationPK() {
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
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDrugvalidationPK)) {
			return false;
		}
		FdbDrugvalidationPK castOther = (FdbDrugvalidationPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.rtgenid.equals(castOther.rtgenid)
			&& this.rtdfgenid.equals(castOther.rtdfgenid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.rtdfgenid.hashCode();
		
		return hash;
    }
}
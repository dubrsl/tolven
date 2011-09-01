package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_compound_rtgen database table.
 * 
 */
@Embeddable
public class FdbCompoundRtgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer compoundid;

	private Integer rtgenid;

    public FdbCompoundRtgenPK() {
    }
	public Integer getCompoundid() {
		return this.compoundid;
	}
	public void setCompoundid(Integer compoundid) {
		this.compoundid = compoundid;
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
		if (!(other instanceof FdbCompoundRtgenPK)) {
			return false;
		}
		FdbCompoundRtgenPK castOther = (FdbCompoundRtgenPK)other;
		return 
			this.compoundid.equals(castOther.compoundid)
			&& this.rtgenid.equals(castOther.rtgenid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.compoundid.hashCode();
		hash = hash * prime + this.rtgenid.hashCode();
		
		return hash;
    }
}
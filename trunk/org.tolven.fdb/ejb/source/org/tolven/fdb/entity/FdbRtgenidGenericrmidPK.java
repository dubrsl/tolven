package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_rtgenid_genericrmid database table.
 * 
 */
@Embeddable
public class FdbRtgenidGenericrmidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid;

	private Integer rmid;

    public FdbRtgenidGenericrmidPK() {
    }
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}
	public Integer getRmid() {
		return this.rmid;
	}
	public void setRmid(Integer rmid) {
		this.rmid = rmid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbRtgenidGenericrmidPK)) {
			return false;
		}
		FdbRtgenidGenericrmidPK castOther = (FdbRtgenidGenericrmidPK)other;
		return 
			this.rtgenid.equals(castOther.rtgenid)
			&& this.rmid.equals(castOther.rmid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.rmid.hashCode();
		
		return hash;
    }
}
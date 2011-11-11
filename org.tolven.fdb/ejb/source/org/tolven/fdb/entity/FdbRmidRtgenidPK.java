package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_rmid_rtgenid database table.
 * 
 */
@Embeddable
public class FdbRmidRtgenidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rmid;

	private Integer rtgenid;

    public FdbRmidRtgenidPK() {
    }
	public Integer getRmid() {
		return this.rmid;
	}
	public void setRmid(Integer rmid) {
		this.rmid = rmid;
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
		if (!(other instanceof FdbRmidRtgenidPK)) {
			return false;
		}
		FdbRmidRtgenidPK castOther = (FdbRmidRtgenidPK)other;
		return 
			this.rmid.equals(castOther.rmid)
			&& this.rtgenid.equals(castOther.rtgenid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rmid.hashCode();
		hash = hash * prime + this.rtgenid.hashCode();
		
		return hash;
    }
}
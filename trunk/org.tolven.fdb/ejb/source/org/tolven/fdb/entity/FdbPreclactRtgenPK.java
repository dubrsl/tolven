package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_preclact_rtgen database table.
 * 
 */
@Embeddable
public class FdbPreclactRtgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid;

	private Integer lactcode;

    public FdbPreclactRtgenPK() {
    }
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
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
		if (!(other instanceof FdbPreclactRtgenPK)) {
			return false;
		}
		FdbPreclactRtgenPK castOther = (FdbPreclactRtgenPK)other;
		return 
			this.rtgenid.equals(castOther.rtgenid)
			&& this.lactcode.equals(castOther.lactcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.lactcode.hashCode();
		
		return hash;
    }
}
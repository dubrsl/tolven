package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_preclact_rtdfgen database table.
 * 
 */
@Embeddable
public class FdbPreclactRtdfgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtdfgenid;

	private Integer lactcode;

    public FdbPreclactRtdfgenPK() {
    }
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
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
		if (!(other instanceof FdbPreclactRtdfgenPK)) {
			return false;
		}
		FdbPreclactRtdfgenPK castOther = (FdbPreclactRtdfgenPK)other;
		return 
			this.rtdfgenid.equals(castOther.rtdfgenid)
			&& this.lactcode.equals(castOther.lactcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtdfgenid.hashCode();
		hash = hash * prime + this.lactcode.hashCode();
		
		return hash;
    }
}
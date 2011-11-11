package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precgeri_rtgen database table.
 * 
 */
@Embeddable
public class FdbPrecgeriRtgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid;

	private Integer gericode;

    public FdbPrecgeriRtgenPK() {
    }
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}
	public Integer getGericode() {
		return this.gericode;
	}
	public void setGericode(Integer gericode) {
		this.gericode = gericode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPrecgeriRtgenPK)) {
			return false;
		}
		FdbPrecgeriRtgenPK castOther = (FdbPrecgeriRtgenPK)other;
		return 
			this.rtgenid.equals(castOther.rtgenid)
			&& this.gericode.equals(castOther.gericode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.gericode.hashCode();
		
		return hash;
    }
}
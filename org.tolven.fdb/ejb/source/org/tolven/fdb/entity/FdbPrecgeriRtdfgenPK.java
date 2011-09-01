package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precgeri_rtdfgen database table.
 * 
 */
@Embeddable
public class FdbPrecgeriRtdfgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtdfgenid;

	private Integer gericode;

    public FdbPrecgeriRtdfgenPK() {
    }
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
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
		if (!(other instanceof FdbPrecgeriRtdfgenPK)) {
			return false;
		}
		FdbPrecgeriRtdfgenPK castOther = (FdbPrecgeriRtdfgenPK)other;
		return 
			this.rtdfgenid.equals(castOther.rtdfgenid)
			&& this.gericode.equals(castOther.gericode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtdfgenid.hashCode();
		hash = hash * prime + this.gericode.hashCode();
		
		return hash;
    }
}
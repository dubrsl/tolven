package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpedi_rtdfgen database table.
 * 
 */
@Embeddable
public class FdbPrecpediRtdfgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtdfgenid;

	private Integer pedicode;

    public FdbPrecpediRtdfgenPK() {
    }
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
	}
	public Integer getPedicode() {
		return this.pedicode;
	}
	public void setPedicode(Integer pedicode) {
		this.pedicode = pedicode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPrecpediRtdfgenPK)) {
			return false;
		}
		FdbPrecpediRtdfgenPK castOther = (FdbPrecpediRtdfgenPK)other;
		return 
			this.rtdfgenid.equals(castOther.rtdfgenid)
			&& this.pedicode.equals(castOther.pedicode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtdfgenid.hashCode();
		hash = hash * prime + this.pedicode.hashCode();
		
		return hash;
    }
}
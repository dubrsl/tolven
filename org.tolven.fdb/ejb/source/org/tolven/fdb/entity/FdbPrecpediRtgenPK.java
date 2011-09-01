package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpedi_rtgen database table.
 * 
 */
@Embeddable
public class FdbPrecpediRtgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid;

	private Integer pedicode;

    public FdbPrecpediRtgenPK() {
    }
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
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
		if (!(other instanceof FdbPrecpediRtgenPK)) {
			return false;
		}
		FdbPrecpediRtgenPK castOther = (FdbPrecpediRtgenPK)other;
		return 
			this.rtgenid.equals(castOther.rtgenid)
			&& this.pedicode.equals(castOther.pedicode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.pedicode.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpreg_rtgen database table.
 * 
 */
@Embeddable
public class FdbPrecpregRtgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid;

	private Integer pregcode;

    public FdbPrecpregRtgenPK() {
    }
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}
	public Integer getPregcode() {
		return this.pregcode;
	}
	public void setPregcode(Integer pregcode) {
		this.pregcode = pregcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPrecpregRtgenPK)) {
			return false;
		}
		FdbPrecpregRtgenPK castOther = (FdbPrecpregRtgenPK)other;
		return 
			this.rtgenid.equals(castOther.rtgenid)
			&& this.pregcode.equals(castOther.pregcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.pregcode.hashCode();
		
		return hash;
    }
}
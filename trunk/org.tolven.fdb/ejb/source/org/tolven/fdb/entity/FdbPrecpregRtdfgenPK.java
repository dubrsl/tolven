package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpreg_rtdfgen database table.
 * 
 */
@Embeddable
public class FdbPrecpregRtdfgenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtdfgenid;

	private Integer pregcode;

    public FdbPrecpregRtdfgenPK() {
    }
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
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
		if (!(other instanceof FdbPrecpregRtdfgenPK)) {
			return false;
		}
		FdbPrecpregRtdfgenPK castOther = (FdbPrecpregRtdfgenPK)other;
		return 
			this.rtdfgenid.equals(castOther.rtdfgenid)
			&& this.pregcode.equals(castOther.pregcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtdfgenid.hashCode();
		hash = hash * prime + this.pregcode.hashCode();
		
		return hash;
    }
}
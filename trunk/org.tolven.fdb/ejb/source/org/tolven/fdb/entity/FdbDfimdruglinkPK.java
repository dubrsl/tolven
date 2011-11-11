package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_dfimdruglink database table.
 * 
 */
@Embeddable
public class FdbDfimdruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid;

	private Integer interactionid;

    public FdbDfimdruglinkPK() {
    }
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}
	public Integer getInteractionid() {
		return this.interactionid;
	}
	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDfimdruglinkPK)) {
			return false;
		}
		FdbDfimdruglinkPK castOther = (FdbDfimdruglinkPK)other;
		return 
			this.rtgenid.equals(castOther.rtgenid)
			&& this.interactionid.equals(castOther.interactionid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.interactionid.hashCode();
		
		return hash;
    }
}
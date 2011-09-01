package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_user_ddimdruglink_category database table.
 * 
 */
@Embeddable
public class FdbUserDdimdruglinkCategoryPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rtgenid1;

	private Integer rtgenid2;

	private Integer interactionid;

    public FdbUserDdimdruglinkCategoryPK() {
    }
	public Integer getRtgenid1() {
		return this.rtgenid1;
	}
	public void setRtgenid1(Integer rtgenid1) {
		this.rtgenid1 = rtgenid1;
	}
	public Integer getRtgenid2() {
		return this.rtgenid2;
	}
	public void setRtgenid2(Integer rtgenid2) {
		this.rtgenid2 = rtgenid2;
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
		if (!(other instanceof FdbUserDdimdruglinkCategoryPK)) {
			return false;
		}
		FdbUserDdimdruglinkCategoryPK castOther = (FdbUserDdimdruglinkCategoryPK)other;
		return 
			this.rtgenid1.equals(castOther.rtgenid1)
			&& this.rtgenid2.equals(castOther.rtgenid2)
			&& this.interactionid.equals(castOther.interactionid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rtgenid1.hashCode();
		hash = hash * prime + this.rtgenid2.hashCode();
		hash = hash * prime + this.interactionid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precgeri_druglink database table.
 * 
 */
@Embeddable
public class FdbPrecgeriDruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer gericode;

    public FdbPrecgeriDruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
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
		if (!(other instanceof FdbPrecgeriDruglinkPK)) {
			return false;
		}
		FdbPrecgeriDruglinkPK castOther = (FdbPrecgeriDruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.gericode.equals(castOther.gericode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.gericode.hashCode();
		
		return hash;
    }
}
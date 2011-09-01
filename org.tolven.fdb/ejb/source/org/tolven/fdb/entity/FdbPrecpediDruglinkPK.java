package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpedi_druglink database table.
 * 
 */
@Embeddable
public class FdbPrecpediDruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer pedicode;

    public FdbPrecpediDruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
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
		if (!(other instanceof FdbPrecpediDruglinkPK)) {
			return false;
		}
		FdbPrecpediDruglinkPK castOther = (FdbPrecpediDruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.pedicode.equals(castOther.pedicode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.pedicode.hashCode();
		
		return hash;
    }
}
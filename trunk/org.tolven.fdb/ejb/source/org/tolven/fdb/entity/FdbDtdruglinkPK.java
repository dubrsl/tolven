package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_dtdruglink database table.
 * 
 */
@Embeddable
public class FdbDtdruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer dtcid;

    public FdbDtdruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
	}
	public Integer getDtcid() {
		return this.dtcid;
	}
	public void setDtcid(Integer dtcid) {
		this.dtcid = dtcid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDtdruglinkPK)) {
			return false;
		}
		FdbDtdruglinkPK castOther = (FdbDtdruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.dtcid.equals(castOther.dtcid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.dtcid.hashCode();
		
		return hash;
    }
}
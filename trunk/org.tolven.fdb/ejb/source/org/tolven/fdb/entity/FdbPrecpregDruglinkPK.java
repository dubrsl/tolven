package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpreg_druglink database table.
 * 
 */
@Embeddable
public class FdbPrecpregDruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer pregcode;

    public FdbPrecpregDruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
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
		if (!(other instanceof FdbPrecpregDruglinkPK)) {
			return false;
		}
		FdbPrecpregDruglinkPK castOther = (FdbPrecpregDruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.pregcode.equals(castOther.pregcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.pregcode.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ddcmdruglink database table.
 * 
 */
@Embeddable
public class FdbDdcmdruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer contraindid;

    public FdbDdcmdruglinkPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
	}
	public Integer getContraindid() {
		return this.contraindid;
	}
	public void setContraindid(Integer contraindid) {
		this.contraindid = contraindid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDdcmdruglinkPK)) {
			return false;
		}
		FdbDdcmdruglinkPK castOther = (FdbDdcmdruglinkPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.contraindid.equals(castOther.contraindid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.contraindid.hashCode();
		
		return hash;
    }
}
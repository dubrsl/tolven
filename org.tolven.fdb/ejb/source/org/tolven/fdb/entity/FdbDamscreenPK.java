package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_damscreen database table.
 * 
 */
@Embeddable
public class FdbDamscreenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugallergyid;

	private String hitval;

    public FdbDamscreenPK() {
    }
	public String getDrugallergyid() {
		return this.drugallergyid;
	}
	public void setDrugallergyid(String drugallergyid) {
		this.drugallergyid = drugallergyid;
	}
	public String getHitval() {
		return this.hitval;
	}
	public void setHitval(String hitval) {
		this.hitval = hitval;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDamscreenPK)) {
			return false;
		}
		FdbDamscreenPK castOther = (FdbDamscreenPK)other;
		return 
			this.drugallergyid.equals(castOther.drugallergyid)
			&& this.hitval.equals(castOther.hitval);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugallergyid.hashCode();
		hash = hash * prime + this.hitval.hashCode();
		
		return hash;
    }
}
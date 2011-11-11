package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_dispensable_labeler database table.
 * 
 */
@Embeddable
public class FdbDispensableLabelerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer medid;

	private String labelerid;

    public FdbDispensableLabelerPK() {
    }
	public Integer getMedid() {
		return this.medid;
	}
	public void setMedid(Integer medid) {
		this.medid = medid;
	}
	public String getLabelerid() {
		return this.labelerid;
	}
	public void setLabelerid(String labelerid) {
		this.labelerid = labelerid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDispensableLabelerPK)) {
			return false;
		}
		FdbDispensableLabelerPK castOther = (FdbDispensableLabelerPK)other;
		return 
			this.medid.equals(castOther.medid)
			&& this.labelerid.equals(castOther.labelerid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.medid.hashCode();
		hash = hash * prime + this.labelerid.hashCode();
		
		return hash;
    }
}
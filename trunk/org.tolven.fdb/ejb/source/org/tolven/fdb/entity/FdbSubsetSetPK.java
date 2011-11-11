package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_subset_set database table.
 * 
 */
@Embeddable
public class FdbSubsetSetPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String siteid;

	private String subsetid;

    public FdbSubsetSetPK() {
    }
	public String getSiteid() {
		return this.siteid;
	}
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
	public String getSubsetid() {
		return this.subsetid;
	}
	public void setSubsetid(String subsetid) {
		this.subsetid = subsetid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbSubsetSetPK)) {
			return false;
		}
		FdbSubsetSetPK castOther = (FdbSubsetSetPK)other;
		return 
			this.siteid.equals(castOther.siteid)
			&& this.subsetid.equals(castOther.subsetid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.siteid.hashCode();
		hash = hash * prime + this.subsetid.hashCode();
		
		return hash;
    }
}
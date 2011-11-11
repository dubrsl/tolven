package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_allergengroupsearch_ii database table.
 * 
 */
@Embeddable
public class FdbAllergengroupsearchIiPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer agid;

    public FdbAllergengroupsearchIiPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getAgid() {
		return this.agid;
	}
	public void setAgid(Integer agid) {
		this.agid = agid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbAllergengroupsearchIiPK)) {
			return false;
		}
		FdbAllergengroupsearchIiPK castOther = (FdbAllergengroupsearchIiPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.agid.equals(castOther.agid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.agid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_class_fdbsearch database table.
 * 
 */
@Embeddable
public class FdbClassFdbsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer fdbclassid;

    public FdbClassFdbsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getFdbclassid() {
		return this.fdbclassid;
	}
	public void setFdbclassid(Integer fdbclassid) {
		this.fdbclassid = fdbclassid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbClassFdbsearchPK)) {
			return false;
		}
		FdbClassFdbsearchPK castOther = (FdbClassFdbsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.fdbclassid.equals(castOther.fdbclassid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.fdbclassid.hashCode();
		
		return hash;
    }
}
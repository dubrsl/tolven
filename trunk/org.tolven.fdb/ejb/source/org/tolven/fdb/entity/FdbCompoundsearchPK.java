package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_compoundsearch database table.
 * 
 */
@Embeddable
public class FdbCompoundsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer compoundid;

    public FdbCompoundsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getCompoundid() {
		return this.compoundid;
	}
	public void setCompoundid(Integer compoundid) {
		this.compoundid = compoundid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCompoundsearchPK)) {
			return false;
		}
		FdbCompoundsearchPK castOther = (FdbCompoundsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.compoundid.equals(castOther.compoundid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.compoundid.hashCode();
		
		return hash;
    }
}
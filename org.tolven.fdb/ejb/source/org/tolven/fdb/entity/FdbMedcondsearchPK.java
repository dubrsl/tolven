package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcondsearch database table.
 * 
 */
@Embeddable
public class FdbMedcondsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String fdbdx;

    public FdbMedcondsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getFdbdx() {
		return this.fdbdx;
	}
	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMedcondsearchPK)) {
			return false;
		}
		FdbMedcondsearchPK castOther = (FdbMedcondsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.fdbdx.equals(castOther.fdbdx);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.fdbdx.hashCode();
		
		return hash;
    }
}
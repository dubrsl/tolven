package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_class_ahfssearch database table.
 * 
 */
@Embeddable
public class FdbClassAhfssearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String ahfsclassid;

    public FdbClassAhfssearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getAhfsclassid() {
		return this.ahfsclassid;
	}
	public void setAhfsclassid(String ahfsclassid) {
		this.ahfsclassid = ahfsclassid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbClassAhfssearchPK)) {
			return false;
		}
		FdbClassAhfssearchPK castOther = (FdbClassAhfssearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.ahfsclassid.equals(castOther.ahfsclassid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.ahfsclassid.hashCode();
		
		return hash;
    }
}
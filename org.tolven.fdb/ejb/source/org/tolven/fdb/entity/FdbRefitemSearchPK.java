package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_refitem_search database table.
 * 
 */
@Embeddable
public class FdbRefitemSearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer refitemid;

    public FdbRefitemSearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getRefitemid() {
		return this.refitemid;
	}
	public void setRefitemid(Integer refitemid) {
		this.refitemid = refitemid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbRefitemSearchPK)) {
			return false;
		}
		FdbRefitemSearchPK castOther = (FdbRefitemSearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.refitemid.equals(castOther.refitemid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.refitemid.hashCode();
		
		return hash;
    }
}
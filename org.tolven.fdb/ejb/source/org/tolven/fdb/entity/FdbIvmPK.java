package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ivm database table.
 * 
 */
@Embeddable
public class FdbIvmPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String grouptestid;

	private String admix;

	private String compid;

    public FdbIvmPK() {
    }
	public String getGrouptestid() {
		return this.grouptestid;
	}
	public void setGrouptestid(String grouptestid) {
		this.grouptestid = grouptestid;
	}
	public String getAdmix() {
		return this.admix;
	}
	public void setAdmix(String admix) {
		this.admix = admix;
	}
	public String getCompid() {
		return this.compid;
	}
	public void setCompid(String compid) {
		this.compid = compid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbIvmPK)) {
			return false;
		}
		FdbIvmPK castOther = (FdbIvmPK)other;
		return 
			this.grouptestid.equals(castOther.grouptestid)
			&& this.admix.equals(castOther.admix)
			&& this.compid.equals(castOther.compid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.grouptestid.hashCode();
		hash = hash * prime + this.admix.hashCode();
		hash = hash * prime + this.compid.hashCode();
		
		return hash;
    }
}
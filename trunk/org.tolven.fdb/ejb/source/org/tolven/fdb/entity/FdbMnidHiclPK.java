package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_mnid_hicl database table.
 * 
 */
@Embeddable
public class FdbMnidHiclPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer mnid;

	private Integer hicl;

    public FdbMnidHiclPK() {
    }
	public Integer getMnid() {
		return this.mnid;
	}
	public void setMnid(Integer mnid) {
		this.mnid = mnid;
	}
	public Integer getHicl() {
		return this.hicl;
	}
	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMnidHiclPK)) {
			return false;
		}
		FdbMnidHiclPK castOther = (FdbMnidHiclPK)other;
		return 
			this.mnid.equals(castOther.mnid)
			&& this.hicl.equals(castOther.hicl);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.mnid.hashCode();
		hash = hash * prime + this.hicl.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_poem_adminrates database table.
 * 
 */
@Embeddable
public class FdbPoemAdminratePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer orderstringid;

	private Integer sequencenumber;

    public FdbPoemAdminratePK() {
    }
	public Integer getOrderstringid() {
		return this.orderstringid;
	}
	public void setOrderstringid(Integer orderstringid) {
		this.orderstringid = orderstringid;
	}
	public Integer getSequencenumber() {
		return this.sequencenumber;
	}
	public void setSequencenumber(Integer sequencenumber) {
		this.sequencenumber = sequencenumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPoemAdminratePK)) {
			return false;
		}
		FdbPoemAdminratePK castOther = (FdbPoemAdminratePK)other;
		return 
			this.orderstringid.equals(castOther.orderstringid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orderstringid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
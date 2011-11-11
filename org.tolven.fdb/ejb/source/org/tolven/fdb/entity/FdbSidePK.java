package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_side database table.
 * 
 */
@Embeddable
public class FdbSidePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer sideeffid;

	private Integer sequencenumber;

    public FdbSidePK() {
    }
	public Integer getSideeffid() {
		return this.sideeffid;
	}
	public void setSideeffid(Integer sideeffid) {
		this.sideeffid = sideeffid;
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
		if (!(other instanceof FdbSidePK)) {
			return false;
		}
		FdbSidePK castOther = (FdbSidePK)other;
		return 
			this.sideeffid.equals(castOther.sideeffid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.sideeffid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
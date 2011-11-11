package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ind database table.
 * 
 */
@Embeddable
public class FdbIndPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer indid;

	private Integer sequencenumber;

    public FdbIndPK() {
    }
	public Integer getIndid() {
		return this.indid;
	}
	public void setIndid(Integer indid) {
		this.indid = indid;
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
		if (!(other instanceof FdbIndPK)) {
			return false;
		}
		FdbIndPK castOther = (FdbIndPK)other;
		return 
			this.indid.equals(castOther.indid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ddcm database table.
 * 
 */
@Embeddable
public class FdbDdcmPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer contraindid;

	private Integer sequencenumber;

    public FdbDdcmPK() {
    }
	public Integer getContraindid() {
		return this.contraindid;
	}
	public void setContraindid(Integer contraindid) {
		this.contraindid = contraindid;
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
		if (!(other instanceof FdbDdcmPK)) {
			return false;
		}
		FdbDdcmPK castOther = (FdbDdcmPK)other;
		return 
			this.contraindid.equals(castOther.contraindid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.contraindid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
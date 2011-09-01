package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ivm_remarklink database table.
 * 
 */
@Embeddable
public class FdbIvmRemarklinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String grouptestid;

	private Integer seqno;

    public FdbIvmRemarklinkPK() {
    }
	public String getGrouptestid() {
		return this.grouptestid;
	}
	public void setGrouptestid(String grouptestid) {
		this.grouptestid = grouptestid;
	}
	public Integer getSeqno() {
		return this.seqno;
	}
	public void setSeqno(Integer seqno) {
		this.seqno = seqno;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbIvmRemarklinkPK)) {
			return false;
		}
		FdbIvmRemarklinkPK castOther = (FdbIvmRemarklinkPK)other;
		return 
			this.grouptestid.equals(castOther.grouptestid)
			&& this.seqno.equals(castOther.seqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.grouptestid.hashCode();
		hash = hash * prime + this.seqno.hashCode();
		
		return hash;
    }
}
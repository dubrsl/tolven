package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_ddiminteraction database table.
 * 
 */
@Embeddable
public class FdbCustomDdiminteractionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer interactionid;

	private Integer seqno;

    public FdbCustomDdiminteractionPK() {
    }
	public Integer getInteractionid() {
		return this.interactionid;
	}
	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
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
		if (!(other instanceof FdbCustomDdiminteractionPK)) {
			return false;
		}
		FdbCustomDdiminteractionPK castOther = (FdbCustomDdiminteractionPK)other;
		return 
			this.interactionid.equals(castOther.interactionid)
			&& this.seqno.equals(castOther.seqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.interactionid.hashCode();
		hash = hash * prime + this.seqno.hashCode();
		
		return hash;
    }
}
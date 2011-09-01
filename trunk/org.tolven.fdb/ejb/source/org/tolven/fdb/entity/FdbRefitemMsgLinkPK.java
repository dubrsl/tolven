package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_refitem_msg_link database table.
 * 
 */
@Embeddable
public class FdbRefitemMsgLinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer refitemid;

	private Integer messageid;

    public FdbRefitemMsgLinkPK() {
    }
	public Integer getRefitemid() {
		return this.refitemid;
	}
	public void setRefitemid(Integer refitemid) {
		this.refitemid = refitemid;
	}
	public Integer getMessageid() {
		return this.messageid;
	}
	public void setMessageid(Integer messageid) {
		this.messageid = messageid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbRefitemMsgLinkPK)) {
			return false;
		}
		FdbRefitemMsgLinkPK castOther = (FdbRefitemMsgLinkPK)other;
		return 
			this.refitemid.equals(castOther.refitemid)
			&& this.messageid.equals(castOther.messageid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.refitemid.hashCode();
		hash = hash * prime + this.messageid.hashCode();
		
		return hash;
    }
}
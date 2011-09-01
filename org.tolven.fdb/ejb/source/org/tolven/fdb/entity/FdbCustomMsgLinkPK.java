package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_msg_link database table.
 * 
 */
@Embeddable
public class FdbCustomMsgLinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer concepttype;

	private String conceptidstring;

	private Integer messageid;

    public FdbCustomMsgLinkPK() {
    }
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public String getConceptidstring() {
		return this.conceptidstring;
	}
	public void setConceptidstring(String conceptidstring) {
		this.conceptidstring = conceptidstring;
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
		if (!(other instanceof FdbCustomMsgLinkPK)) {
			return false;
		}
		FdbCustomMsgLinkPK castOther = (FdbCustomMsgLinkPK)other;
		return 
			this.concepttype.equals(castOther.concepttype)
			&& this.conceptidstring.equals(castOther.conceptidstring)
			&& this.messageid.equals(castOther.messageid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptidstring.hashCode();
		hash = hash * prime + this.messageid.hashCode();
		
		return hash;
    }
}
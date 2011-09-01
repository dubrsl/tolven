package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_refitem_msg_text database table.
 * 
 */
@Embeddable
public class FdbRefitemMsgTextPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer messageid;

	private Integer sequencenumber;

    public FdbRefitemMsgTextPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getMessageid() {
		return this.messageid;
	}
	public void setMessageid(Integer messageid) {
		this.messageid = messageid;
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
		if (!(other instanceof FdbRefitemMsgTextPK)) {
			return false;
		}
		FdbRefitemMsgTextPK castOther = (FdbRefitemMsgTextPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.messageid.equals(castOther.messageid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.messageid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
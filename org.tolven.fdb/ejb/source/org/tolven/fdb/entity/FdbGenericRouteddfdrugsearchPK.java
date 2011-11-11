package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_generic_routeddfdrugsearch database table.
 * 
 */
@Embeddable
public class FdbGenericRouteddfdrugsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer rtdfgenid;

	private String nametypecode;

	private Integer sequencenumber;

    public FdbGenericRouteddfdrugsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
	}
	public String getNametypecode() {
		return this.nametypecode;
	}
	public void setNametypecode(String nametypecode) {
		this.nametypecode = nametypecode;
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
		if (!(other instanceof FdbGenericRouteddfdrugsearchPK)) {
			return false;
		}
		FdbGenericRouteddfdrugsearchPK castOther = (FdbGenericRouteddfdrugsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.rtdfgenid.equals(castOther.rtdfgenid)
			&& this.nametypecode.equals(castOther.nametypecode)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.rtdfgenid.hashCode();
		hash = hash * prime + this.nametypecode.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
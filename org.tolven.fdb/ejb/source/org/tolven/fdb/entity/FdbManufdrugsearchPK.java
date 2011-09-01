package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_manufdrugsearch database table.
 * 
 */
@Embeddable
public class FdbManufdrugsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String imdid;

	private String nametypecode;

	private Integer sequencenumber;

    public FdbManufdrugsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getImdid() {
		return this.imdid;
	}
	public void setImdid(String imdid) {
		this.imdid = imdid;
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
		if (!(other instanceof FdbManufdrugsearchPK)) {
			return false;
		}
		FdbManufdrugsearchPK castOther = (FdbManufdrugsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.imdid.equals(castOther.imdid)
			&& this.nametypecode.equals(castOther.nametypecode)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.imdid.hashCode();
		hash = hash * prime + this.nametypecode.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
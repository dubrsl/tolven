package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_regpackagedsearch database table.
 * 
 */
@Embeddable
public class FdbRegpackagedsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String ipdid;

	private String nametypecode;

	private Integer sequencenumber;

    public FdbRegpackagedsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getIpdid() {
		return this.ipdid;
	}
	public void setIpdid(String ipdid) {
		this.ipdid = ipdid;
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
		if (!(other instanceof FdbRegpackagedsearchPK)) {
			return false;
		}
		FdbRegpackagedsearchPK castOther = (FdbRegpackagedsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.ipdid.equals(castOther.ipdid)
			&& this.nametypecode.equals(castOther.nametypecode)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.ipdid.hashCode();
		hash = hash * prime + this.nametypecode.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
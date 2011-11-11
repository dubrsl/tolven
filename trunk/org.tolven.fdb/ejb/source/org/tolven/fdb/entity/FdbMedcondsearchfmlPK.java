package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcondsearchfml database table.
 * 
 */
@Embeddable
public class FdbMedcondsearchfmlPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String dxid;

	private Integer synid;

    public FdbMedcondsearchfmlPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getDxid() {
		return this.dxid;
	}
	public void setDxid(String dxid) {
		this.dxid = dxid;
	}
	public Integer getSynid() {
		return this.synid;
	}
	public void setSynid(Integer synid) {
		this.synid = synid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMedcondsearchfmlPK)) {
			return false;
		}
		FdbMedcondsearchfmlPK castOther = (FdbMedcondsearchfmlPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.dxid.equals(castOther.dxid)
			&& this.synid.equals(castOther.synid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.dxid.hashCode();
		hash = hash * prime + this.synid.hashCode();
		
		return hash;
    }
}
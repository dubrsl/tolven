package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_monograph database table.
 * 
 */
@Embeddable
public class FdbCustomMonographPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String versioncode;

	private String categorycode;

	private Integer monographid;

	private Integer sequencenumber;

    public FdbCustomMonographPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getVersioncode() {
		return this.versioncode;
	}
	public void setVersioncode(String versioncode) {
		this.versioncode = versioncode;
	}
	public String getCategorycode() {
		return this.categorycode;
	}
	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}
	public Integer getMonographid() {
		return this.monographid;
	}
	public void setMonographid(Integer monographid) {
		this.monographid = monographid;
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
		if (!(other instanceof FdbCustomMonographPK)) {
			return false;
		}
		FdbCustomMonographPK castOther = (FdbCustomMonographPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.versioncode.equals(castOther.versioncode)
			&& this.categorycode.equals(castOther.categorycode)
			&& this.monographid.equals(castOther.monographid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.versioncode.hashCode();
		hash = hash * prime + this.categorycode.hashCode();
		hash = hash * prime + this.monographid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
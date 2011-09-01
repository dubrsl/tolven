package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_drug_monograph_ext database table.
 * 
 */
@Embeddable
public class FdbDrugMonographExtPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer concepttype;

	private String conceptid;

	private String versioncode;

	private Integer monographid;

    public FdbDrugMonographExtPK() {
    }
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public String getConceptid() {
		return this.conceptid;
	}
	public void setConceptid(String conceptid) {
		this.conceptid = conceptid;
	}
	public String getVersioncode() {
		return this.versioncode;
	}
	public void setVersioncode(String versioncode) {
		this.versioncode = versioncode;
	}
	public Integer getMonographid() {
		return this.monographid;
	}
	public void setMonographid(Integer monographid) {
		this.monographid = monographid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDrugMonographExtPK)) {
			return false;
		}
		FdbDrugMonographExtPK castOther = (FdbDrugMonographExtPK)other;
		return 
			this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid)
			&& this.versioncode.equals(castOther.versioncode)
			&& this.monographid.equals(castOther.monographid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.versioncode.hashCode();
		hash = hash * prime + this.monographid.hashCode();
		
		return hash;
    }
}
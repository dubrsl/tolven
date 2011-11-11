package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_drug_plblw database table.
 * 
 */
@Embeddable
public class FdbDrugPlblwPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer concepttype;

	private String conceptid;

	private String lwid;

    public FdbDrugPlblwPK() {
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
	public String getLwid() {
		return this.lwid;
	}
	public void setLwid(String lwid) {
		this.lwid = lwid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDrugPlblwPK)) {
			return false;
		}
		FdbDrugPlblwPK castOther = (FdbDrugPlblwPK)other;
		return 
			this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid)
			&& this.lwid.equals(castOther.lwid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.lwid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_concept_pemdruglink database table.
 * 
 */
@Embeddable
public class FdbConceptPemdruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String conceptid;

	private Integer concepttype;

	private Integer monographid;

    public FdbConceptPemdruglinkPK() {
    }
	public String getConceptid() {
		return this.conceptid;
	}
	public void setConceptid(String conceptid) {
		this.conceptid = conceptid;
	}
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
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
		if (!(other instanceof FdbConceptPemdruglinkPK)) {
			return false;
		}
		FdbConceptPemdruglinkPK castOther = (FdbConceptPemdruglinkPK)other;
		return 
			this.conceptid.equals(castOther.conceptid)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.monographid.equals(castOther.monographid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.monographid.hashCode();
		
		return hash;
    }
}
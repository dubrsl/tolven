package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ahfsclass_drugconcept database table.
 * 
 */
@Embeddable
public class FdbAhfsclassDrugconceptPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String ahfsclassid;

	private Integer concepttype;

	private Integer conceptid;

    public FdbAhfsclassDrugconceptPK() {
    }
	public String getAhfsclassid() {
		return this.ahfsclassid;
	}
	public void setAhfsclassid(String ahfsclassid) {
		this.ahfsclassid = ahfsclassid;
	}
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public Integer getConceptid() {
		return this.conceptid;
	}
	public void setConceptid(Integer conceptid) {
		this.conceptid = conceptid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbAhfsclassDrugconceptPK)) {
			return false;
		}
		FdbAhfsclassDrugconceptPK castOther = (FdbAhfsclassDrugconceptPK)other;
		return 
			this.ahfsclassid.equals(castOther.ahfsclassid)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.ahfsclassid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		
		return hash;
    }
}
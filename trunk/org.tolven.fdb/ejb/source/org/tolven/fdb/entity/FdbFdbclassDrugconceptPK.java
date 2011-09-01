package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_fdbclass_drugconcept database table.
 * 
 */
@Embeddable
public class FdbFdbclassDrugconceptPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer fdbclassid;

	private Integer concepttype;

	private Integer conceptid;

    public FdbFdbclassDrugconceptPK() {
    }
	public Integer getFdbclassid() {
		return this.fdbclassid;
	}
	public void setFdbclassid(Integer fdbclassid) {
		this.fdbclassid = fdbclassid;
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
		if (!(other instanceof FdbFdbclassDrugconceptPK)) {
			return false;
		}
		FdbFdbclassDrugconceptPK castOther = (FdbFdbclassDrugconceptPK)other;
		return 
			this.fdbclassid.equals(castOther.fdbclassid)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.fdbclassid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		
		return hash;
    }
}
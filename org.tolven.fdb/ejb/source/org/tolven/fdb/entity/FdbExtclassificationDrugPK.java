package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_extclassification_drugs database table.
 * 
 */
@Embeddable
public class FdbExtclassificationDrugPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String classtypecode;

	private String classid;

	private Integer concepttype;

	private Integer conceptid;

    public FdbExtclassificationDrugPK() {
    }
	public String getClasstypecode() {
		return this.classtypecode;
	}
	public void setClasstypecode(String classtypecode) {
		this.classtypecode = classtypecode;
	}
	public String getClassid() {
		return this.classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
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
		if (!(other instanceof FdbExtclassificationDrugPK)) {
			return false;
		}
		FdbExtclassificationDrugPK castOther = (FdbExtclassificationDrugPK)other;
		return 
			this.classtypecode.equals(castOther.classtypecode)
			&& this.classid.equals(castOther.classid)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.classtypecode.hashCode();
		hash = hash * prime + this.classid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		
		return hash;
    }
}
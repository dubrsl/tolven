package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_allergenpicklist database table.
 * 
 */
@Embeddable
public class FdbCustomAllergenpicklistPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private String conceptid;

	private Integer concepttype;

    public FdbCustomAllergenpicklistPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCustomAllergenpicklistPK)) {
			return false;
		}
		FdbCustomAllergenpicklistPK castOther = (FdbCustomAllergenpicklistPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.conceptid.equals(castOther.conceptid)
			&& this.concepttype.equals(castOther.concepttype);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		
		return hash;
    }
}
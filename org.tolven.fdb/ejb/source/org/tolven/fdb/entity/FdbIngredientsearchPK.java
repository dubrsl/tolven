package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ingredientsearch database table.
 * 
 */
@Embeddable
public class FdbIngredientsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private Integer hicseqno;

    public FdbIngredientsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getHicseqno() {
		return this.hicseqno;
	}
	public void setHicseqno(Integer hicseqno) {
		this.hicseqno = hicseqno;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbIngredientsearchPK)) {
			return false;
		}
		FdbIngredientsearchPK castOther = (FdbIngredientsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.hicseqno.equals(castOther.hicseqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.hicseqno.hashCode();
		
		return hash;
    }
}
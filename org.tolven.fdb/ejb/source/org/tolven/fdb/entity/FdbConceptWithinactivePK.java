package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_concept_withinactives database table.
 * 
 */
@Embeddable
public class FdbConceptWithinactivePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String conceptid;

	private Integer concepttype;

	private Integer hicseqno;

    public FdbConceptWithinactivePK() {
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
		if (!(other instanceof FdbConceptWithinactivePK)) {
			return false;
		}
		FdbConceptWithinactivePK castOther = (FdbConceptWithinactivePK)other;
		return 
			this.conceptid.equals(castOther.conceptid)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.hicseqno.equals(castOther.hicseqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.hicseqno.hashCode();
		
		return hash;
    }
}
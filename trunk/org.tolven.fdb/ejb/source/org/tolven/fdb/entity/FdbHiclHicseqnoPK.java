package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_hicl_hicseqno database table.
 * 
 */
@Embeddable
public class FdbHiclHicseqnoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer hicl;

	private Integer hicseqno;

    public FdbHiclHicseqnoPK() {
    }
	public Integer getHicl() {
		return this.hicl;
	}
	public void setHicl(Integer hicl) {
		this.hicl = hicl;
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
		if (!(other instanceof FdbHiclHicseqnoPK)) {
			return false;
		}
		FdbHiclHicseqnoPK castOther = (FdbHiclHicseqnoPK)other;
		return 
			this.hicl.equals(castOther.hicl)
			&& this.hicseqno.equals(castOther.hicseqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.hicl.hashCode();
		hash = hash * prime + this.hicseqno.hashCode();
		
		return hash;
    }
}
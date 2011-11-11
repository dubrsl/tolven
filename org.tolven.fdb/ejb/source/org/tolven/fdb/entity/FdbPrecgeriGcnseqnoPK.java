package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precgeri_gcnseqno database table.
 * 
 */
@Embeddable
public class FdbPrecgeriGcnseqnoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer gericode;

    public FdbPrecgeriGcnseqnoPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getGericode() {
		return this.gericode;
	}
	public void setGericode(Integer gericode) {
		this.gericode = gericode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPrecgeriGcnseqnoPK)) {
			return false;
		}
		FdbPrecgeriGcnseqnoPK castOther = (FdbPrecgeriGcnseqnoPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.gericode.equals(castOther.gericode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.gericode.hashCode();
		
		return hash;
    }
}
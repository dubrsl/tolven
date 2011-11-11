package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpedi_gcnseqno database table.
 * 
 */
@Embeddable
public class FdbPrecpediGcnseqnoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer pedicode;

    public FdbPrecpediGcnseqnoPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getPedicode() {
		return this.pedicode;
	}
	public void setPedicode(Integer pedicode) {
		this.pedicode = pedicode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPrecpediGcnseqnoPK)) {
			return false;
		}
		FdbPrecpediGcnseqnoPK castOther = (FdbPrecpediGcnseqnoPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.pedicode.equals(castOther.pedicode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.pedicode.hashCode();
		
		return hash;
    }
}
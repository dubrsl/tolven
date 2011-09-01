package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_precpreg_gcnseqno database table.
 * 
 */
@Embeddable
public class FdbPrecpregGcnseqnoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer pregcode;

    public FdbPrecpregGcnseqnoPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getPregcode() {
		return this.pregcode;
	}
	public void setPregcode(Integer pregcode) {
		this.pregcode = pregcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPrecpregGcnseqnoPK)) {
			return false;
		}
		FdbPrecpregGcnseqnoPK castOther = (FdbPrecpregGcnseqnoPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.pregcode.equals(castOther.pregcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.pregcode.hashCode();
		
		return hash;
    }
}
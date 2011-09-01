package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_preclact_gcnseqno database table.
 * 
 */
@Embeddable
public class FdbPreclactGcnseqnoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer lactcode;

    public FdbPreclactGcnseqnoPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getLactcode() {
		return this.lactcode;
	}
	public void setLactcode(Integer lactcode) {
		this.lactcode = lactcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPreclactGcnseqnoPK)) {
			return false;
		}
		FdbPreclactGcnseqnoPK castOther = (FdbPreclactGcnseqnoPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.lactcode.equals(castOther.lactcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.lactcode.hashCode();
		
		return hash;
    }
}
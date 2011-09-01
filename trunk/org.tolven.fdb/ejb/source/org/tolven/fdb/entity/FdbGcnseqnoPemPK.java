package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_gcnseqno_pem database table.
 * 
 */
@Embeddable
public class FdbGcnseqnoPemPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer monographid;

    public FdbGcnseqnoPemPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getMonographid() {
		return this.monographid;
	}
	public void setMonographid(Integer monographid) {
		this.monographid = monographid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbGcnseqnoPemPK)) {
			return false;
		}
		FdbGcnseqnoPemPK castOther = (FdbGcnseqnoPemPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.monographid.equals(castOther.monographid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.monographid.hashCode();
		
		return hash;
    }
}
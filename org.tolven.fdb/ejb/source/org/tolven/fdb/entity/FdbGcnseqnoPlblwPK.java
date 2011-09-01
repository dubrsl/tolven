package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_gcnseqno_plblw database table.
 * 
 */
@Embeddable
public class FdbGcnseqnoPlblwPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private String lwid;

    public FdbGcnseqnoPlblwPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public String getLwid() {
		return this.lwid;
	}
	public void setLwid(String lwid) {
		this.lwid = lwid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbGcnseqnoPlblwPK)) {
			return false;
		}
		FdbGcnseqnoPlblwPK castOther = (FdbGcnseqnoPlblwPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.lwid.equals(castOther.lwid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.lwid.hashCode();
		
		return hash;
    }
}
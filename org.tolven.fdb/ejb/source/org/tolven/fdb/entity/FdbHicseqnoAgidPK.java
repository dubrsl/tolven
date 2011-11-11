package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_hicseqno_agid database table.
 * 
 */
@Embeddable
public class FdbHicseqnoAgidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer hicseqno;

	private Integer agid;

    public FdbHicseqnoAgidPK() {
    }
	public Integer getHicseqno() {
		return this.hicseqno;
	}
	public void setHicseqno(Integer hicseqno) {
		this.hicseqno = hicseqno;
	}
	public Integer getAgid() {
		return this.agid;
	}
	public void setAgid(Integer agid) {
		this.agid = agid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbHicseqnoAgidPK)) {
			return false;
		}
		FdbHicseqnoAgidPK castOther = (FdbHicseqnoAgidPK)other;
		return 
			this.hicseqno.equals(castOther.hicseqno)
			&& this.agid.equals(castOther.agid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.hicseqno.hashCode();
		hash = hash * prime + this.agid.hashCode();
		
		return hash;
    }
}
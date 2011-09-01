package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_discreen database table.
 * 
 */
@Embeddable
public class FdbDiscreenPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String drugid;

	private Integer hicseqno;

    public FdbDiscreenPK() {
    }
	public String getDrugid() {
		return this.drugid;
	}
	public void setDrugid(String drugid) {
		this.drugid = drugid;
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
		if (!(other instanceof FdbDiscreenPK)) {
			return false;
		}
		FdbDiscreenPK castOther = (FdbDiscreenPK)other;
		return 
			this.drugid.equals(castOther.drugid)
			&& this.hicseqno.equals(castOther.hicseqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.drugid.hashCode();
		hash = hash * prime + this.hicseqno.hashCode();
		
		return hash;
    }
}
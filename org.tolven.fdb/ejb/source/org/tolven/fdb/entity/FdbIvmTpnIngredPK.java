package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ivm_tpn_ingred database table.
 * 
 */
@Embeddable
public class FdbIvmTpnIngredPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String compid;

	private Integer seqno;

    public FdbIvmTpnIngredPK() {
    }
	public String getCompid() {
		return this.compid;
	}
	public void setCompid(String compid) {
		this.compid = compid;
	}
	public Integer getSeqno() {
		return this.seqno;
	}
	public void setSeqno(Integer seqno) {
		this.seqno = seqno;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbIvmTpnIngredPK)) {
			return false;
		}
		FdbIvmTpnIngredPK castOther = (FdbIvmTpnIngredPK)other;
		return 
			this.compid.equals(castOther.compid)
			&& this.seqno.equals(castOther.seqno);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.compid.hashCode();
		hash = hash * prime + this.seqno.hashCode();
		
		return hash;
    }
}
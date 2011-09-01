package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_hicseqno_axsid database table.
 * 
 */
@Embeddable
public class FdbHicseqnoAxsidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer hicseqno;

	private Integer axsid;

    public FdbHicseqnoAxsidPK() {
    }
	public Integer getHicseqno() {
		return this.hicseqno;
	}
	public void setHicseqno(Integer hicseqno) {
		this.hicseqno = hicseqno;
	}
	public Integer getAxsid() {
		return this.axsid;
	}
	public void setAxsid(Integer axsid) {
		this.axsid = axsid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbHicseqnoAxsidPK)) {
			return false;
		}
		FdbHicseqnoAxsidPK castOther = (FdbHicseqnoAxsidPK)other;
		return 
			this.hicseqno.equals(castOther.hicseqno)
			&& this.axsid.equals(castOther.axsid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.hicseqno.hashCode();
		hash = hash * prime + this.axsid.hashCode();
		
		return hash;
    }
}
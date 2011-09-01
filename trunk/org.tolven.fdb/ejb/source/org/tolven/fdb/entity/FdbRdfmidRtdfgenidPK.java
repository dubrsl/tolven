package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_rdfmid_rtdfgenid database table.
 * 
 */
@Embeddable
public class FdbRdfmidRtdfgenidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer rdfmid;

	private Integer rtdfgenid;

    public FdbRdfmidRtdfgenidPK() {
    }
	public Integer getRdfmid() {
		return this.rdfmid;
	}
	public void setRdfmid(Integer rdfmid) {
		this.rdfmid = rdfmid;
	}
	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}
	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbRdfmidRtdfgenidPK)) {
			return false;
		}
		FdbRdfmidRtdfgenidPK castOther = (FdbRdfmidRtdfgenidPK)other;
		return 
			this.rdfmid.equals(castOther.rdfmid)
			&& this.rtdfgenid.equals(castOther.rtdfgenid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rdfmid.hashCode();
		hash = hash * prime + this.rtdfgenid.hashCode();
		
		return hash;
    }
}
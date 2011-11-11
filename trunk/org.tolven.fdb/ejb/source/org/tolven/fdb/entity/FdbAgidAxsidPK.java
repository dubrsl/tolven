package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_agid_axsid database table.
 * 
 */
@Embeddable
public class FdbAgidAxsidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer agid;

	private Integer axsid;

    public FdbAgidAxsidPK() {
    }
	public Integer getAgid() {
		return this.agid;
	}
	public void setAgid(Integer agid) {
		this.agid = agid;
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
		if (!(other instanceof FdbAgidAxsidPK)) {
			return false;
		}
		FdbAgidAxsidPK castOther = (FdbAgidAxsidPK)other;
		return 
			this.agid.equals(castOther.agid)
			&& this.axsid.equals(castOther.axsid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.agid.hashCode();
		hash = hash * prime + this.axsid.hashCode();
		
		return hash;
    }
}
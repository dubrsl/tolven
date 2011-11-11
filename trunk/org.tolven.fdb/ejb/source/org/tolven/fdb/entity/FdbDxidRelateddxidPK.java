package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_dxid_relateddxid database table.
 * 
 */
@Embeddable
public class FdbDxidRelateddxidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String dxid;

	private String relateddxid;

	private String clinicalcode;

    public FdbDxidRelateddxidPK() {
    }
	public String getDxid() {
		return this.dxid;
	}
	public void setDxid(String dxid) {
		this.dxid = dxid;
	}
	public String getRelateddxid() {
		return this.relateddxid;
	}
	public void setRelateddxid(String relateddxid) {
		this.relateddxid = relateddxid;
	}
	public String getClinicalcode() {
		return this.clinicalcode;
	}
	public void setClinicalcode(String clinicalcode) {
		this.clinicalcode = clinicalcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDxidRelateddxidPK)) {
			return false;
		}
		FdbDxidRelateddxidPK castOther = (FdbDxidRelateddxidPK)other;
		return 
			this.dxid.equals(castOther.dxid)
			&& this.relateddxid.equals(castOther.relateddxid)
			&& this.clinicalcode.equals(castOther.clinicalcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dxid.hashCode();
		hash = hash * prime + this.relateddxid.hashCode();
		hash = hash * prime + this.clinicalcode.hashCode();
		
		return hash;
    }
}
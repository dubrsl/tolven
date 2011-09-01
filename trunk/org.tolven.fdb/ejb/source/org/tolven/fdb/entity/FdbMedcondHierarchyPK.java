package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcond_hierarchy database table.
 * 
 */
@Embeddable
public class FdbMedcondHierarchyPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String dxid;

	private String broaderdxid;

	private Integer toplevelrelind;

    public FdbMedcondHierarchyPK() {
    }
	public String getDxid() {
		return this.dxid;
	}
	public void setDxid(String dxid) {
		this.dxid = dxid;
	}
	public String getBroaderdxid() {
		return this.broaderdxid;
	}
	public void setBroaderdxid(String broaderdxid) {
		this.broaderdxid = broaderdxid;
	}
	public Integer getToplevelrelind() {
		return this.toplevelrelind;
	}
	public void setToplevelrelind(Integer toplevelrelind) {
		this.toplevelrelind = toplevelrelind;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMedcondHierarchyPK)) {
			return false;
		}
		FdbMedcondHierarchyPK castOther = (FdbMedcondHierarchyPK)other;
		return 
			this.dxid.equals(castOther.dxid)
			&& this.broaderdxid.equals(castOther.broaderdxid)
			&& this.toplevelrelind.equals(castOther.toplevelrelind);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dxid.hashCode();
		hash = hash * prime + this.broaderdxid.hashCode();
		hash = hash * prime + this.toplevelrelind.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcondext_hierarchy database table.
 * 
 */
@Embeddable
public class FdbMedcondextHierarchyPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String vocabtypecode;

	private String hexid;

	private String broaderhexid;

	private Integer toplevelrelind;

    public FdbMedcondextHierarchyPK() {
    }
	public String getVocabtypecode() {
		return this.vocabtypecode;
	}
	public void setVocabtypecode(String vocabtypecode) {
		this.vocabtypecode = vocabtypecode;
	}
	public String getHexid() {
		return this.hexid;
	}
	public void setHexid(String hexid) {
		this.hexid = hexid;
	}
	public String getBroaderhexid() {
		return this.broaderhexid;
	}
	public void setBroaderhexid(String broaderhexid) {
		this.broaderhexid = broaderhexid;
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
		if (!(other instanceof FdbMedcondextHierarchyPK)) {
			return false;
		}
		FdbMedcondextHierarchyPK castOther = (FdbMedcondextHierarchyPK)other;
		return 
			this.vocabtypecode.equals(castOther.vocabtypecode)
			&& this.hexid.equals(castOther.hexid)
			&& this.broaderhexid.equals(castOther.broaderhexid)
			&& this.toplevelrelind.equals(castOther.toplevelrelind);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.vocabtypecode.hashCode();
		hash = hash * prime + this.hexid.hashCode();
		hash = hash * prime + this.broaderhexid.hashCode();
		hash = hash * prime + this.toplevelrelind.hashCode();
		
		return hash;
    }
}
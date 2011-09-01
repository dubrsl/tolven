package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_subset_drugvalues database table.
 * 
 */
@Embeddable
public class FdbSubsetDrugvaluePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String siteid;

	private String subsetid;

	private Integer concepttype;

	private String conceptidstring;

	private Integer sequencenumber;

    public FdbSubsetDrugvaluePK() {
    }
	public String getSiteid() {
		return this.siteid;
	}
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
	public String getSubsetid() {
		return this.subsetid;
	}
	public void setSubsetid(String subsetid) {
		this.subsetid = subsetid;
	}
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public String getConceptidstring() {
		return this.conceptidstring;
	}
	public void setConceptidstring(String conceptidstring) {
		this.conceptidstring = conceptidstring;
	}
	public Integer getSequencenumber() {
		return this.sequencenumber;
	}
	public void setSequencenumber(Integer sequencenumber) {
		this.sequencenumber = sequencenumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbSubsetDrugvaluePK)) {
			return false;
		}
		FdbSubsetDrugvaluePK castOther = (FdbSubsetDrugvaluePK)other;
		return 
			this.siteid.equals(castOther.siteid)
			&& this.subsetid.equals(castOther.subsetid)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.conceptidstring.equals(castOther.conceptidstring)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.siteid.hashCode();
		hash = hash * prime + this.subsetid.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptidstring.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
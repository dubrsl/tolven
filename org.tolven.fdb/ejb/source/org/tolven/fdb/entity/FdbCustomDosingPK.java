package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_dosing database table.
 * 
 */
@Embeddable
public class FdbCustomDosingPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private Integer concepttype;

	private Integer conceptid;

	private Integer agelowindays;

	private Integer agehighindays;

	private String doserouteid;

	private String dosetypeid;

	private String fdbdx;

    public FdbCustomDosingPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public Integer getConceptid() {
		return this.conceptid;
	}
	public void setConceptid(Integer conceptid) {
		this.conceptid = conceptid;
	}
	public Integer getAgelowindays() {
		return this.agelowindays;
	}
	public void setAgelowindays(Integer agelowindays) {
		this.agelowindays = agelowindays;
	}
	public Integer getAgehighindays() {
		return this.agehighindays;
	}
	public void setAgehighindays(Integer agehighindays) {
		this.agehighindays = agehighindays;
	}
	public String getDoserouteid() {
		return this.doserouteid;
	}
	public void setDoserouteid(String doserouteid) {
		this.doserouteid = doserouteid;
	}
	public String getDosetypeid() {
		return this.dosetypeid;
	}
	public void setDosetypeid(String dosetypeid) {
		this.dosetypeid = dosetypeid;
	}
	public String getFdbdx() {
		return this.fdbdx;
	}
	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCustomDosingPK)) {
			return false;
		}
		FdbCustomDosingPK castOther = (FdbCustomDosingPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid)
			&& this.agelowindays.equals(castOther.agelowindays)
			&& this.agehighindays.equals(castOther.agehighindays)
			&& this.doserouteid.equals(castOther.doserouteid)
			&& this.dosetypeid.equals(castOther.dosetypeid)
			&& this.fdbdx.equals(castOther.fdbdx);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.agelowindays.hashCode();
		hash = hash * prime + this.agehighindays.hashCode();
		hash = hash * prime + this.doserouteid.hashCode();
		hash = hash * prime + this.dosetypeid.hashCode();
		hash = hash * prime + this.fdbdx.hashCode();
		
		return hash;
    }
}
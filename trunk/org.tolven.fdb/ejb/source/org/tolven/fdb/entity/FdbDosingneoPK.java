package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_dosingneo database table.
 * 
 */
@Embeddable
public class FdbDosingneoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer concepttype;

	private Integer conceptid;

	private Integer agelowindays;

	private Integer agehighindays;

	private String doserouteid;

	private String dosetypeid;

	private String fdbdx;

	private Integer gablowinweeks;

	private Integer gabhighinweeks;

	private Integer weightlowingrams;

	private Integer weighthighingrams;

    public FdbDosingneoPK() {
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
	public Integer getGablowinweeks() {
		return this.gablowinweeks;
	}
	public void setGablowinweeks(Integer gablowinweeks) {
		this.gablowinweeks = gablowinweeks;
	}
	public Integer getGabhighinweeks() {
		return this.gabhighinweeks;
	}
	public void setGabhighinweeks(Integer gabhighinweeks) {
		this.gabhighinweeks = gabhighinweeks;
	}
	public Integer getWeightlowingrams() {
		return this.weightlowingrams;
	}
	public void setWeightlowingrams(Integer weightlowingrams) {
		this.weightlowingrams = weightlowingrams;
	}
	public Integer getWeighthighingrams() {
		return this.weighthighingrams;
	}
	public void setWeighthighingrams(Integer weighthighingrams) {
		this.weighthighingrams = weighthighingrams;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDosingneoPK)) {
			return false;
		}
		FdbDosingneoPK castOther = (FdbDosingneoPK)other;
		return 
			this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid)
			&& this.agelowindays.equals(castOther.agelowindays)
			&& this.agehighindays.equals(castOther.agehighindays)
			&& this.doserouteid.equals(castOther.doserouteid)
			&& this.dosetypeid.equals(castOther.dosetypeid)
			&& this.fdbdx.equals(castOther.fdbdx)
			&& this.gablowinweeks.equals(castOther.gablowinweeks)
			&& this.gabhighinweeks.equals(castOther.gabhighinweeks)
			&& this.weightlowingrams.equals(castOther.weightlowingrams)
			&& this.weighthighingrams.equals(castOther.weighthighingrams);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.agelowindays.hashCode();
		hash = hash * prime + this.agehighindays.hashCode();
		hash = hash * prime + this.doserouteid.hashCode();
		hash = hash * prime + this.dosetypeid.hashCode();
		hash = hash * prime + this.fdbdx.hashCode();
		hash = hash * prime + this.gablowinweeks.hashCode();
		hash = hash * prime + this.gabhighinweeks.hashCode();
		hash = hash * prime + this.weightlowingrams.hashCode();
		hash = hash * prime + this.weighthighingrams.hashCode();
		
		return hash;
    }
}
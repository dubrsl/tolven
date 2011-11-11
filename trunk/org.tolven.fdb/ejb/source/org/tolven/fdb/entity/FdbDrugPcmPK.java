package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_drug_pcm database table.
 * 
 */
@Embeddable
public class FdbDrugPcmPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer concepttype;

	private String conceptid;

	private Integer languageid;

	private Integer pcmid;

    public FdbDrugPcmPK() {
    }
	public Integer getConcepttype() {
		return this.concepttype;
	}
	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}
	public String getConceptid() {
		return this.conceptid;
	}
	public void setConceptid(String conceptid) {
		this.conceptid = conceptid;
	}
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getPcmid() {
		return this.pcmid;
	}
	public void setPcmid(Integer pcmid) {
		this.pcmid = pcmid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDrugPcmPK)) {
			return false;
		}
		FdbDrugPcmPK castOther = (FdbDrugPcmPK)other;
		return 
			this.concepttype.equals(castOther.concepttype)
			&& this.conceptid.equals(castOther.conceptid)
			&& this.languageid.equals(castOther.languageid)
			&& this.pcmid.equals(castOther.pcmid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.concepttype.hashCode();
		hash = hash * prime + this.conceptid.hashCode();
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.pcmid.hashCode();
		
		return hash;
    }
}
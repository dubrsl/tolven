package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_indication database table.
 * 
 */
@Embeddable
public class FdbCustomIndicationPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private Integer rtgenid;

	private String vocabtypecode;

	private String medcondid;

    public FdbCustomIndicationPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getRtgenid() {
		return this.rtgenid;
	}
	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}
	public String getVocabtypecode() {
		return this.vocabtypecode;
	}
	public void setVocabtypecode(String vocabtypecode) {
		this.vocabtypecode = vocabtypecode;
	}
	public String getMedcondid() {
		return this.medcondid;
	}
	public void setMedcondid(String medcondid) {
		this.medcondid = medcondid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCustomIndicationPK)) {
			return false;
		}
		FdbCustomIndicationPK castOther = (FdbCustomIndicationPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.rtgenid.equals(castOther.rtgenid)
			&& this.vocabtypecode.equals(castOther.vocabtypecode)
			&& this.medcondid.equals(castOther.medcondid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.rtgenid.hashCode();
		hash = hash * prime + this.vocabtypecode.hashCode();
		hash = hash * prime + this.medcondid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_dtcat database table.
 * 
 */
@Embeddable
public class FdbCustomDtcatPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private Integer dtcid;

    public FdbCustomDtcatPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getDtcid() {
		return this.dtcid;
	}
	public void setDtcid(Integer dtcid) {
		this.dtcid = dtcid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCustomDtcatPK)) {
			return false;
		}
		FdbCustomDtcatPK castOther = (FdbCustomDtcatPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.dtcid.equals(castOther.dtcid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.dtcid.hashCode();
		
		return hash;
    }
}
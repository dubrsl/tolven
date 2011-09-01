package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_drugmapping database table.
 * 
 */
@Embeddable
public class FdbCustomDrugmappingPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private String customdrugid;

    public FdbCustomDrugmappingPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCustomdrugid() {
		return this.customdrugid;
	}
	public void setCustomdrugid(String customdrugid) {
		this.customdrugid = customdrugid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCustomDrugmappingPK)) {
			return false;
		}
		FdbCustomDrugmappingPK castOther = (FdbCustomDrugmappingPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.customdrugid.equals(castOther.customdrugid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.customdrugid.hashCode();
		
		return hash;
    }
}
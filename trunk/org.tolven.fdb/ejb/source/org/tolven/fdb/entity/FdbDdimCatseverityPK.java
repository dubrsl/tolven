package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ddim_catseverities database table.
 * 
 */
@Embeddable
public class FdbDdimCatseverityPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private Integer interactionid;

    public FdbDdimCatseverityPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getInteractionid() {
		return this.interactionid;
	}
	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDdimCatseverityPK)) {
			return false;
		}
		FdbDdimCatseverityPK castOther = (FdbDdimCatseverityPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.interactionid.equals(castOther.interactionid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.interactionid.hashCode();
		
		return hash;
    }
}
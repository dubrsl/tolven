package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_user_interaction_category database table.
 * 
 */
@Embeddable
public class FdbUserInteractionCategoryPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String interactiontypecode;

	private Integer interactionid;

    public FdbUserInteractionCategoryPK() {
    }
	public String getInteractiontypecode() {
		return this.interactiontypecode;
	}
	public void setInteractiontypecode(String interactiontypecode) {
		this.interactiontypecode = interactiontypecode;
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
		if (!(other instanceof FdbUserInteractionCategoryPK)) {
			return false;
		}
		FdbUserInteractionCategoryPK castOther = (FdbUserInteractionCategoryPK)other;
		return 
			this.interactiontypecode.equals(castOther.interactiontypecode)
			&& this.interactionid.equals(castOther.interactionid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.interactiontypecode.hashCode();
		hash = hash * prime + this.interactionid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_replacementingredients database table.
 * 
 */
@Embeddable
public class FdbReplacementingredientPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer replacedid;

	private Integer replacementid;

    public FdbReplacementingredientPK() {
    }
	public Integer getReplacedid() {
		return this.replacedid;
	}
	public void setReplacedid(Integer replacedid) {
		this.replacedid = replacedid;
	}
	public Integer getReplacementid() {
		return this.replacementid;
	}
	public void setReplacementid(Integer replacementid) {
		this.replacementid = replacementid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbReplacementingredientPK)) {
			return false;
		}
		FdbReplacementingredientPK castOther = (FdbReplacementingredientPK)other;
		return 
			this.replacedid.equals(castOther.replacedid)
			&& this.replacementid.equals(castOther.replacementid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.replacedid.hashCode();
		hash = hash * prime + this.replacementid.hashCode();
		
		return hash;
    }
}
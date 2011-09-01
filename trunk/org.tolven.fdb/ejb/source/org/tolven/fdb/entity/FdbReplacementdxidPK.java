package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_replacementdxids database table.
 * 
 */
@Embeddable
public class FdbReplacementdxidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String replaceddxid;

	private String replacementdxid;

    public FdbReplacementdxidPK() {
    }
	public String getReplaceddxid() {
		return this.replaceddxid;
	}
	public void setReplaceddxid(String replaceddxid) {
		this.replaceddxid = replaceddxid;
	}
	public String getReplacementdxid() {
		return this.replacementdxid;
	}
	public void setReplacementdxid(String replacementdxid) {
		this.replacementdxid = replacementdxid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbReplacementdxidPK)) {
			return false;
		}
		FdbReplacementdxidPK castOther = (FdbReplacementdxidPK)other;
		return 
			this.replaceddxid.equals(castOther.replaceddxid)
			&& this.replacementdxid.equals(castOther.replacementdxid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.replaceddxid.hashCode();
		hash = hash * prime + this.replacementdxid.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_imprintdesctext database table.
 * 
 */
@Embeddable
public class FdbImprintdesctextPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer imptextid;

	private Integer linenumber;

    public FdbImprintdesctextPK() {
    }
	public Integer getImptextid() {
		return this.imptextid;
	}
	public void setImptextid(Integer imptextid) {
		this.imptextid = imptextid;
	}
	public Integer getLinenumber() {
		return this.linenumber;
	}
	public void setLinenumber(Integer linenumber) {
		this.linenumber = linenumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbImprintdesctextPK)) {
			return false;
		}
		FdbImprintdesctextPK castOther = (FdbImprintdesctextPK)other;
		return 
			this.imptextid.equals(castOther.imptextid)
			&& this.linenumber.equals(castOther.linenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.imptextid.hashCode();
		hash = hash * prime + this.linenumber.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_poem_orderstring_addltext database table.
 * 
 */
@Embeddable
public class FdbPoemOrderstringAddltextPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer orderstringid;

	private String textlocationcode;

    public FdbPoemOrderstringAddltextPK() {
    }
	public Integer getOrderstringid() {
		return this.orderstringid;
	}
	public void setOrderstringid(Integer orderstringid) {
		this.orderstringid = orderstringid;
	}
	public String getTextlocationcode() {
		return this.textlocationcode;
	}
	public void setTextlocationcode(String textlocationcode) {
		this.textlocationcode = textlocationcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPoemOrderstringAddltextPK)) {
			return false;
		}
		FdbPoemOrderstringAddltextPK castOther = (FdbPoemOrderstringAddltextPK)other;
		return 
			this.orderstringid.equals(castOther.orderstringid)
			&& this.textlocationcode.equals(castOther.textlocationcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orderstringid.hashCode();
		hash = hash * prime + this.textlocationcode.hashCode();
		
		return hash;
    }
}
package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_poemtext database table.
 * 
 */
@Embeddable
public class FdbPoemtextPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private long poemtextid;

	private Integer linenumber;

    public FdbPoemtextPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public long getPoemtextid() {
		return this.poemtextid;
	}
	public void setPoemtextid(long poemtextid) {
		this.poemtextid = poemtextid;
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
		if (!(other instanceof FdbPoemtextPK)) {
			return false;
		}
		FdbPoemtextPK castOther = (FdbPoemtextPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& (this.poemtextid == castOther.poemtextid)
			&& this.linenumber.equals(castOther.linenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + ((int) (this.poemtextid ^ (this.poemtextid >>> 32)));
		hash = hash * prime + this.linenumber.hashCode();
		
		return hash;
    }
}
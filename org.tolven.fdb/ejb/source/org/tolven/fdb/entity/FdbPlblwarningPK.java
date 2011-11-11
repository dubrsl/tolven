package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_plblwarnings database table.
 * 
 */
@Embeddable
public class FdbPlblwarningPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String lwid;

	private Integer sequencenumber;

    public FdbPlblwarningPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getLwid() {
		return this.lwid;
	}
	public void setLwid(String lwid) {
		this.lwid = lwid;
	}
	public Integer getSequencenumber() {
		return this.sequencenumber;
	}
	public void setSequencenumber(Integer sequencenumber) {
		this.sequencenumber = sequencenumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPlblwarningPK)) {
			return false;
		}
		FdbPlblwarningPK castOther = (FdbPlblwarningPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.lwid.equals(castOther.lwid)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.lwid.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}
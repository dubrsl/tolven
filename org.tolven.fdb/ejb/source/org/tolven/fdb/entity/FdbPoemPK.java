package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_poem database table.
 * 
 */
@Embeddable
public class FdbPoemPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer agelowindays;

	private Integer agehighindays;

	private String fdbdx;

	private Integer orderstringid;

    public FdbPoemPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getAgelowindays() {
		return this.agelowindays;
	}
	public void setAgelowindays(Integer agelowindays) {
		this.agelowindays = agelowindays;
	}
	public Integer getAgehighindays() {
		return this.agehighindays;
	}
	public void setAgehighindays(Integer agehighindays) {
		this.agehighindays = agehighindays;
	}
	public String getFdbdx() {
		return this.fdbdx;
	}
	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}
	public Integer getOrderstringid() {
		return this.orderstringid;
	}
	public void setOrderstringid(Integer orderstringid) {
		this.orderstringid = orderstringid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPoemPK)) {
			return false;
		}
		FdbPoemPK castOther = (FdbPoemPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.agelowindays.equals(castOther.agelowindays)
			&& this.agehighindays.equals(castOther.agehighindays)
			&& this.fdbdx.equals(castOther.fdbdx)
			&& this.orderstringid.equals(castOther.orderstringid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.agelowindays.hashCode();
		hash = hash * prime + this.agehighindays.hashCode();
		hash = hash * prime + this.fdbdx.hashCode();
		hash = hash * prime + this.orderstringid.hashCode();
		
		return hash;
    }
}
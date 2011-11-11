package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_regpackagedextid database table.
 * 
 */
@Embeddable
public class FdbRegpackagedextidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer extidtype;

	private String hexid;

    public FdbRegpackagedextidPK() {
    }
	public Integer getExtidtype() {
		return this.extidtype;
	}
	public void setExtidtype(Integer extidtype) {
		this.extidtype = extidtype;
	}
	public String getHexid() {
		return this.hexid;
	}
	public void setHexid(String hexid) {
		this.hexid = hexid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbRegpackagedextidPK)) {
			return false;
		}
		FdbRegpackagedextidPK castOther = (FdbRegpackagedextidPK)other;
		return 
			this.extidtype.equals(castOther.extidtype)
			&& this.hexid.equals(castOther.hexid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.extidtype.hashCode();
		hash = hash * prime + this.hexid.hashCode();
		
		return hash;
    }
}